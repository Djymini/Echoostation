package com.djymini.echoostation.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.djymini.echoostation.adapters.ArtistAdapter;
import com.djymini.echoostation.dataBase.DatabaseClient;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.utilities.SortOptionAlbum;
import com.djymini.echoostation.utilities.SortOptionArtist;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ArtistFragment extends EchoostationFragment {
    private MusicPlayerViewModel playerViewModel;
    private RecyclerView recyclerView;
    private List<ArtistDto> currentArtistList = new ArrayList<>();
    private ArtistAdapter adapter;
    private TextView artistCounterView;
    private Spinner spinner;
    private String search;
    private ActionMode actionMode;
    private ActivityResultLauncher<IntentSenderRequest> deleteMultipleLauncher;
    private List<ArtistDto> artistsPendingDeletion;
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
                Set<ArtistDto> selectedCopy = new HashSet<>(adapter.getSelectedItems());
                //confirmAndDeleteSelectedMusics(selectedCopy);
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            ArtistFragment.this.actionMode = null;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        EchooStationDatabase db = DatabaseClient.getInstance(requireContext()).getDatabase();
        setupDaoAndService(db);

        executor = Executors.newSingleThreadExecutor();

        setupUI(view);
        setupObservers();

        loadArtists();

        return view;
    }

    private void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_artist);
        artistCounterView = view.findViewById(R.id.number_artist);
        spinner = view.findViewById(R.id.spinner);

        setupRecyclerView();
        setupSpinner();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        adapter = new ArtistAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnMusicMenuClickListener((artist, anchorView) -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity main = (MainActivity) getActivity();
                main.appInitializer.getMusicDialogManager().showBottomDialog(artist);
            }
        });

        adapter.setOnItemLongClickListener(position -> {
            if (actionMode == null) {
                actionMode = ((AppCompatActivity) requireActivity())
                        .startSupportActionMode(actionModeCallback);
            }
            ArtistDto artist = adapter.getCurrentList().get(position);
            adapter.toggleSelection(artist);
            updateActionModeTitle();
        });

        adapter.setOnItemClickListener(position -> {
            if (actionMode != null) {
                ArtistDto artist = adapter.getCurrentList().get(position);
                adapter.toggleSelection(artist);
                updateActionModeTitle();
            } else {
                if(getActivity() instanceof MainActivity){
                    MainActivity main = (MainActivity) getActivity();
                    FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

                    Fragment fragment = ArtistInfoFragment.newInstance(adapter.getCurrentList().get(position));

                    if (!fragment.isAdded()) {
                        transaction.add(R.id.frame_layout, fragment);
                    } else {
                        transaction.show(fragment);
                    }

                    transaction.hide(main.navigator.getActiveFragment()).commit();

                    main.navigator.modifyTitle(adapter.getCurrentList().get(position).name);
                    main.navigator.setActiveFragment(fragment);
                }
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
                sortAndDisplayArtists(position);
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
            sortAndDisplayArtists(spinner.getSelectedItemPosition());
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

    private void loadArtists() {
        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            main.navigator.modifyTitle(getString(R.string.library_fragment));
            main.loaderMediaViewModel.loadArtists().observe(getViewLifecycleOwner(), artists -> {
                currentArtistList = new ArrayList<>(artists);
                sortAndDisplayArtists(spinner.getSelectedItemPosition());
                String counterAlbum = artists.size() + getString(R.string.album_fragment);
                artistCounterView.setText(counterAlbum);
            });
        }
    }

    private void sortAndDisplayArtists(int position) {
        if (currentArtistList == null) return;

        executor.execute(() -> {
            List<ArtistDto> filtered = new ArrayList<>(fullTextSearchByLogicalOr(currentArtistList, search));

            if (position >= 0 && position < SortOption.values().length) {
                SortOptionArtist option = SortOptionArtist.values()[position];
                filtered.sort(option.getComparator());
            }

            //playlist = loadPlaylist(filtered);
            requireActivity().runOnUiThread(() -> adapter.submitList(filtered));
        });
    }

    private List<ArtistDto> fullTextSearchByLogicalOr(List<ArtistDto> artistList, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return artistList;

        return artistList.stream()
                .filter(artist -> containsIgnoreCase(artist.name, keyword))
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

    /*private void deleteSelectedMusics(Set<MusicDto> selected) {
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
    }*/

    /*private void confirmAndDeleteSelectedMusics(Set<MusicDto> selected) {
        if (selected.isEmpty()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete_music))
                .setMessage(getString(R.string.delete_request1) + selected.size() + getString(R.string.delete_request2))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteSelectedMusics(selected))
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }*/

    /*private List<MediaItem> loadPlaylist(List<MusicDto> list) {
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
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }
}