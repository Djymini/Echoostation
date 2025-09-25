package com.djymini.echoostation.fragments.mediaFragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.AlbumAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.fragments.mediaDetailFragment.AlbumDetailFragment;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.utilities.SortOptionAlbum;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbumFragment extends MediaFragment<AlbumDto, AlbumAdapter> {
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
                Set<AlbumDto> selectedCopy = new HashSet<>(adapter.getSelectedItems());
                main.deleteManager.confirmAndDeleteSelectedMedia(selectedCopy, requireContext(), AlbumFragment.this, executor);
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            AlbumFragment.this.actionMode = null;
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
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        setupUI(view);
        setupObservers();
        loadMedias();

        return view;
    }

    @Override
    public void setupUI(View view) {
        super.setupUI(view);
        shuffleButton.setOnClickListener(v -> MediaItemHelper.shuffleAlbum(mediaList, main, requireContext(), executor));

        setupRecyclerView();
        setupSpinner();
        main.deleteManager.setupDeleteLauncher(executor, this);
    }

    @Override
    public void setupRecyclerView() {
        adapter = new AlbumAdapter();
        RecyclerViewHelper.setupRecyclerViewGrid(recyclerView, getContext(), adapter, 3, true);

        recyclerView.setBubbleColor(ContextCompat.getColor(requireContext(), R.color.colorSecondary));
        recyclerView.setBubbleTextColor(ContextCompat.getColor(requireContext(), R.color.colorText));
        recyclerView.setHandleColor(ContextCompat.getColor(requireContext(), R.color.colorThird));

        adapter.setOnMusicMenuClickListener((album, anchorView) -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity main = (MainActivity) getActivity();
                main.appInitializer.getMusicDialogManager().showBottomDialog(album);
            }
        });

        adapter.setOnItemLongClickListener(position -> {
            if (actionMode == null) {
                actionMode = ((AppCompatActivity) requireActivity())
                        .startSupportActionMode(actionModeCallback);
            }
            AlbumDto album = adapter.getCurrentList().get(position);
            adapter.toggleSelection(album);
            updateActionModeTitle(adapter.getSelectedItems().size());
        });

        adapter.setOnItemClickListener(position -> {
            if (actionMode != null) {
                AlbumDto album = adapter.getCurrentList().get(position);
                adapter.toggleSelection(album);
                updateActionModeTitle(adapter.getSelectedItems().size());
            } else {
                main.navigator.showFragment(AlbumDetailFragment.newInstance(adapter.getCurrentList().get(position), R.id.library), changeTheTitle);
            }
        });
    }

    private void setupSpinner() {
        List<String> displayNames = new ArrayList<>();
        for (SortOptionAlbum option : SortOptionAlbum.values()) {
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
    }

    @Override
    public void loadMedias(){
        super.loadMedias();
        main.loaderMediaViewModel.loadAlbums().observe(getViewLifecycleOwner(), albums -> {
            mediaList = new ArrayList<>(albums);
            sortAndDisplayMedias(spinner.getSelectedItemPosition());
            String counterAlbum = albums.size() + getString(R.string.album_fragment);
            counterView.setText(counterAlbum);
        });
    }

    @Override
    public void sortAndDisplayMedias(int position) {
        if (mediaList == null) return;

        executor.execute(() -> {
            List<AlbumDto> filtered = new ArrayList<>(fullTextSearchByLogicalOr(mediaList, search, List.of(AlbumDto::getName, AlbumDto::getArtistName)));
            if (position >= 0 && position < SortOption.values().length) {
                SortOptionAlbum option = SortOptionAlbum.values()[position];
                filtered.sort(option.getComparator());
                requireActivity().runOnUiThread(() -> adapter.setSortOption(option));
            }
            requireActivity().runOnUiThread(() -> adapter.submitList(filtered));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }
}