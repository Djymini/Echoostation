package com.djymini.echoostation.fragments.mediaFragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.appcompat.view.ActionMode;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.MusicAdapter;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MusicFragment extends MediaFragment<MusicDto, MusicAdapter> {
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
                main.deleteManager.confirmAndDeleteSelectedMedia(selectedCopy, requireContext(), MusicFragment.this, executor);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        setupUI(view);
        setupObservers();
        loadMedias();

        return view;
    }

    @Override
    public void setupUI(View view) {
        super.setupUI(view);
        shuffleButton.setOnClickListener(v -> MediaItemHelper.shuffleMusic(mediaList, playlist, main, requireContext()));

        setupRecyclerView();
        setupSpinner();
        main.deleteManager.setupDeleteLauncher(executor, this);
    }

    @Override
    public void setupRecyclerView() {
        adapter = new MusicAdapter();
        RecyclerViewHelper.setupRecyclerViewLinear(recyclerView, getContext(), adapter, LinearLayoutManager.VERTICAL, true);

        recyclerView.setBubbleColor(ContextCompat.getColor(requireContext(), R.color.colorSecondary));
        recyclerView.setBubbleTextColor(ContextCompat.getColor(requireContext(), R.color.colorText));
        recyclerView.setHandleColor(ContextCompat.getColor(requireContext(), R.color.colorThird));

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
            updateActionModeTitle(adapter.getSelectedItems().size());
        });

        adapter.setOnItemClickListener(position -> {
            if (actionMode != null) {
                MusicDto music = adapter.getCurrentList().get(position);
                adapter.toggleSelection(music);
                updateActionModeTitle(adapter.getSelectedItems().size());
            } else {
                main.playerViewModel.playPlaylist(requireContext(), playlist, position);
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
                sortAndDisplayMedias(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupObservers() {
        ShareSearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(ShareSearchViewModel.class);

        searchViewModel.getQuery().observe(getViewLifecycleOwner(), query -> {
            search = query;
            sortAndDisplayMedias(spinner.getSelectedItemPosition());
        });

        main.playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            // TODO: visuel lecture
        });

        main.playerViewModel.getCurrentItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                //TODO: visuel item sélectionné
            }
        });
    }

    @Override
    public void loadMedias() {
        super.loadMedias();
        main.loaderMediaViewModel.loadMusics().observe(getViewLifecycleOwner(), musics -> {
            mediaList = new ArrayList<>(musics);
            sortAndDisplayMedias(spinner.getSelectedItemPosition());
            String counterMusic = musics.size() + getString(R.string.music_fragment);
            counterView.setText(counterMusic);
        });
    }

    @Override
    public void sortAndDisplayMedias(int position) {
        if (mediaList == null) return;

        executor.execute(() -> {
            List<MusicDto> filtered = new ArrayList<>(fullTextSearchByLogicalOr(mediaList, search, List.of(MusicDto::getTitle, MusicDto::getAlbumName, MusicDto::getArtistName)));
            if (position >= 0 && position < SortOption.values().length) {
                SortOption option = SortOption.values()[position];
                filtered.sort(option.getComparator());
                requireActivity().runOnUiThread(() -> adapter.setSortOption(option));
            }
            playlist = MediaItemHelper.loadPlaylist(filtered);
            requireActivity().runOnUiThread(() -> adapter.submitList(filtered));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }
}
