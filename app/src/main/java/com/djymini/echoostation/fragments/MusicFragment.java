package com.djymini.echoostation.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.view.ActionMode;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.djymini.echoostation.EchooStationDatabase;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.MusicAdapter;
import com.djymini.echoostation.adapters.SpinnerAdapter;
import com.djymini.echoostation.dataBase.DatabaseClient;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.ui.MusicDialogManager;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MusicFragment extends EchoostationFragment {
    private MusicPlayerViewModel playerViewModel;
    private RecyclerView recyclerView;
    private List<MusicDto> currentMusicList = new ArrayList<>();
    private MusicAdapter adapter;
    private TextView counterMusic;
    private Spinner spinner;
    private final String[] sortCategories = Constants.SORT_CATEGORIES;
    private String search;
    private ActionMode actionMode;
    private int REQUEST_CODE_DELETE_MULTIPLE = 1002;
    private ActivityResultLauncher<IntentSenderRequest> deleteMultipleLauncher;
    private List<MusicDto> musicsPendingDeletion;
    private Executor executor = Executors.newSingleThreadExecutor();
    private List<MediaItem> playlist;

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selection_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                Set<MusicDto> selectedCopy = new HashSet<>(adapter.getSelectedItems());
                confirmAndDeleteSelectedMusics(selectedCopy);
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            MusicFragment.this.actionMode = null;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        EchooStationDatabase db = DatabaseClient.getInstance(requireContext()).getDatabase();
        setupDaoAndService(db);

        recyclerView = view.findViewById(R.id.recycler_view_song);
        counterMusic = view.findViewById(R.id.number_music);
        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_categories,
                R.layout.spinner_item
        );
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapterSpinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter();
        recyclerView.setAdapter(adapter);

        ShareSearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(ShareSearchViewModel.class);

        searchViewModel.getQuery().observe(getViewLifecycleOwner(), query -> {
            search = query;
            sortAndDisplayMusics(spinner.getSelectedItemPosition());
        });

        playerViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);

        SpinnerAdapter arrayAdapter = new SpinnerAdapter(requireContext(), sortCategories);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortAndDisplayMusics(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter.setOnMusicMenuClickListener((music, anchorView) -> {
            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).musicDialogManager.showBottomDialog(music);
            }
        });

        loadMusics();

        adapter.setOnItemLongClickListener(position -> {
            if (actionMode == null) {
                actionMode = ((AppCompatActivity) requireActivity())
                        .startSupportActionMode(actionModeCallback);
            }
            MusicDto music = adapter.getCurrentList().get(position);
            adapter.toggleSelection(music);
            updateActionModeTitle();
        });

        adapter.setOnItemClickListener(position -> {
            if (actionMode != null) {
                MusicDto music = adapter.getCurrentList().get(position);
                adapter.toggleSelection(music);
                updateActionModeTitle();
            } else {
                Log.d("echoostation : MusicFragement", String.valueOf(playlist.size()));
                playerViewModel.playPlaylist(requireContext(), playlist, position);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new MusicPlayerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        deleteMultipleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && musicsPendingDeletion != null) {
                        List<MusicDto> toDelete = new ArrayList<>(musicsPendingDeletion);
                        executor.execute(() -> {
                            for (MusicDto music : toDelete) {
                                musicDao.deleteById(music.id);
                            }
                        });
                    }
                    musicsPendingDeletion = null;
                }
        );

        playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            // Ici tu peux changer l’UI, par ex. afficher un bouton pause/play
            Log.d("MusicFragment", "isPlaying = " + isPlaying);
        });

        playerViewModel.getCurrentItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                Log.d("MusicFragment", "Lecture en cours: " + item.mediaMetadata.title);
            }
        });

        return view;
    }

    private void loadMusics() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).modifyTitle("Paramètres");
            ((MainActivity) getActivity()).currentMusicList.observe(getViewLifecycleOwner(), musics -> {
                currentMusicList = new ArrayList<>(musics);
                sortAndDisplayMusics(spinner.getSelectedItemPosition());
                counterMusic.setText(musics.size() + " Titres");
            });
        }
    }

    private void sortAndDisplayMusics(int position) {
        if (currentMusicList == null) return;

        executor.execute(() -> {
            List<MusicDto> sortedList = new ArrayList<>(fullTextSearchByLogicalOr(currentMusicList, search));

            switch (position) {
                case 0:
                    sortedList.sort((m1, m2) -> m1.title.compareToIgnoreCase(m2.title));
                    break;
                case 1:
                    sortedList.sort((m1, m2) -> m2.title.compareToIgnoreCase(m1.title));
                    break;
                case 2:
                    sortedList.sort(Comparator.comparingLong(m -> m.duration));
                    break;
                case 3:
                    sortedList.sort((m1, m2) -> Long.compare(m2.duration, m1.duration));
                    break;
                case 4:
                    sortedList.sort((m1, m2) -> m1.albumName.compareToIgnoreCase(m2.albumName));
                    break;
                case 5:
                    sortedList.sort((m1, m2) -> m2.albumName.compareToIgnoreCase(m1.albumName));
                    break;
                case 6:
                    sortedList.sort((m1, m2) -> m1.artistName.compareToIgnoreCase(m2.artistName));
                    break;
                case 7:
                    sortedList.sort((m1, m2) -> m2.artistName.compareToIgnoreCase(m1.artistName));
                    break;
                case 8:
                    sortedList.sort((m1, m2) -> Integer.compare(m2.listeningNumber, m1.listeningNumber));
                    break;
                case 9:
                    sortedList.sort(Comparator.comparingInt(m -> m.listeningNumber));
                    break;
                case 10:
                    sortedList.sort((m1, m2) -> Long.compare(m2.createdAt, m1.createdAt));
                    break;
                case 11:
                    sortedList.sort(Comparator.comparingLong(m -> m.createdAt));
                    break;
            }

            playlist = loadPlaylist(sortedList);
            requireActivity().runOnUiThread(() -> adapter.submitList(sortedList));
        });
    }

    private List<MusicDto> fullTextSearchByLogicalOr(List<MusicDto> musicDtoList, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return musicDtoList;

        return musicDtoList.stream()
                .filter(musicDto -> musicDto.title != null && musicDto.title.toLowerCase().contains(keyword.toLowerCase())
                || musicDto.albumName != null && musicDto.albumName.toLowerCase().contains(keyword.toLowerCase())
                || musicDto.artistName != null && musicDto.artistName.toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void updateActionModeTitle() {
        int count = adapter.getSelectedItems().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(count + " sélectionné(s)");
        }
    }

    private void deleteSelectedMusics(Set<MusicDto> selected) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            List<Uri> uris = new ArrayList<>();
            for (MusicDto music : selected) {
                long mediaStoreId = MusicDialogManager.getMediaStoreIdFromPath(requireActivity(), music.path);
                if (mediaStoreId != -1) {
                    uris.add(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaStoreId));
                }
            }

            if (!uris.isEmpty()) {
                musicsPendingDeletion = new ArrayList<>(selected);
                IntentSender sender = MediaStore.createDeleteRequest(
                        requireActivity().getContentResolver(), uris
                ).getIntentSender();
                deleteMultipleLauncher.launch(new IntentSenderRequest.Builder(sender).build());
            }
        } else {
            executor.execute(() -> {
                for (MusicDto music : selected) {
                    long mediaStoreId = MusicDialogManager.getMediaStoreIdFromPath(requireActivity(), music.path);
                    if (mediaStoreId != -1) {
                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaStoreId);
                        int deleted = requireActivity().getContentResolver().delete(uri, null, null);
                        if (deleted > 0) {
                            musicDao.deleteById(music.id);
                        }
                    }
                }
            });
        }
    }


    private void confirmAndDeleteSelectedMusics(Set<MusicDto> selected) {
        if (selected.isEmpty()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Supprimer la musique")
                .setMessage("Voulez-vous vraiment supprimer " + selected.size() + " élément(s) ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    deleteSelectedMusics(selected);
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private List<MediaItem> loadPlaylist(List<MusicDto> list) {
        List<MediaItem> items = new ArrayList<>();
        Log.d("echoostation : MusicFragment getCurrentList().size()", String.valueOf(list.size()));
        for (MusicDto music : list) {
            MediaMetadata metadata = new MediaMetadata.Builder()
                    .setTitle(music.title)
                    .setArtist(music.artistName)
                    .setArtworkUri(music.getCover())
                    .build();

            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(music.path)
                    .setMediaId(String.valueOf(music.id))
                    .setMediaMetadata(metadata)
                    .build();

            items.add(mediaItem);
        }

        Log.d("echoostation : MusicFragment items.size()", String.valueOf(items.size()));
        return items;
    }
}
