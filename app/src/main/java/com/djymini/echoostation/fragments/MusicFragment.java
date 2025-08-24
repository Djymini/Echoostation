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
import com.djymini.echoostation.dataBase.DatabaseClient;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.ui.MusicDialogManager;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;

public class MusicFragment extends EchoostationFragment {
    private MusicPlayerViewModel playerViewModel;
    private RecyclerView recyclerView;
    private List<MusicDto> currentMusicList = new ArrayList<>();
    private MusicAdapter adapter;
    private TextView musicCounterView;
    private Spinner spinner;
    private String search;
    private ActionMode actionMode;
    private ActivityResultLauncher<IntentSenderRequest> deleteMultipleLauncher;
    private List<MusicDto> musicsPendingDeletion;
    private ExecutorService executor;
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

        executor = Executors.newSingleThreadExecutor();

        setupUI(view);
        setupObservers();

        loadMusics();

        return view;
    }

    private void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_song);
        musicCounterView = view.findViewById(R.id.number_music);
        spinner = view.findViewById(R.id.spinner);

        setupRecyclerView();
        setupSpinner();
        setupDeleteLauncher();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnMusicMenuClickListener((music, anchorView) -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity main = (MainActivity) getActivity();
                main.appInitializer.getMusicDialogManager().showBottomDialog(music);
            }
        });

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
                playerViewModel.playPlaylist(requireContext(), playlist, position);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new MusicPlayerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setupSpinner() {
        List<String> displayNames = new ArrayList<>();
        for (SortOption option : SortOption.values()) {
            displayNames.add(option.getDisplayName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                displayNames
        );
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortAndDisplayMusics(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupObservers() {
        ShareSearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(ShareSearchViewModel.class);
        playerViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);

        searchViewModel.getQuery().observe(getViewLifecycleOwner(), query -> {
            search = query;
            sortAndDisplayMusics(spinner.getSelectedItemPosition());
        });

        playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            // TODO visuel lecture
        });

        playerViewModel.getCurrentItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                // TODO visuel item sélectionné
            }
        });
    }

    private void setupDeleteLauncher() {
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
    }


    private void loadMusics() {
        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            main.navigator.modifyTitle(getString(R.string.library_fragment));
            main.loaderMediaViewModel.loadMusics().observe(getViewLifecycleOwner(), musics -> {
                currentMusicList = new ArrayList<>(musics);
                sortAndDisplayMusics(spinner.getSelectedItemPosition());
                String counterMusic = musics.size() + getString(R.string.music_fragment);
                musicCounterView.setText(counterMusic);
            });
        }
    }

    private void sortAndDisplayMusics(int position) {
        if (currentMusicList == null) return;

        executor.execute(() -> {
            List<MusicDto> filtered = new ArrayList<>(fullTextSearchByLogicalOr(currentMusicList, search));

            if (position >= 0 && position < SortOption.values().length) {
                SortOption option = SortOption.values()[position];
                filtered.sort(option.getComparator());
            }

            playlist = loadPlaylist(filtered);
            requireActivity().runOnUiThread(() -> adapter.submitList(filtered));
        });
    }

    private List<MusicDto> fullTextSearchByLogicalOr(List<MusicDto> musicDtoList, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return musicDtoList;

        return musicDtoList.stream()
                .filter(musicDto -> containsIgnoreCase(musicDto.title, keyword)
                        || containsIgnoreCase(musicDto.albumName, keyword)
                        || containsIgnoreCase(musicDto.artistName, keyword))
                .collect(Collectors.toList());
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && keyword != null && text.toLowerCase().contains(keyword.toLowerCase());
    }

    private void updateActionModeTitle() {
        int count = adapter.getSelectedItems().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(count + getString(R.string.item_selected));
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
                .setTitle(getString(R.string.delete_music))
                .setMessage(getString(R.string.delete_request1) + selected.size() + getString(R.string.delete_request2))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteSelectedMusics(selected))
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private List<MediaItem> loadPlaylist(List<MusicDto> list) {
        List<MediaItem> items = new ArrayList<>();
        for (MusicDto music : list) {
            MediaMetadata metadata = new MediaMetadata.Builder()
                    .setTitle(music.title)
                    .setArtist(music.artistName)
                    .setAlbumTitle(music.albumName)
                    .setArtworkUri(music.getCover())
                    .setDurationMs(music.duration)
                    .build();

            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(music.path)
                    .setMediaId(String.valueOf(music.id))
                    .setMediaMetadata(metadata)
                    .build();

            items.add(mediaItem);
        }

        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }
}
