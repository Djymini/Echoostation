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
import android.widget.ImageButton;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.PlaylistAdapter;
import com.djymini.echoostation.dtos.PlaylistDto;
import com.djymini.echoostation.fragments.playlistMusicFragment.PlaylistPersoFragment;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistFragment extends MediaFragment<PlaylistDto, PlaylistAdapter> {
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
                Set<PlaylistDto> selectedCopy = new HashSet<>(adapter.getSelectedItems());
                main.deleteManager.confirmAndDeleteSelectedMedia(selectedCopy, requireContext(), PlaylistFragment.this, executor);
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            PlaylistFragment.this.actionMode = null;
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
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        setupUI(view);
        setupObservers();
        loadMedias();

        return view;
    }

    @Override
    public void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_media);
        ImageButton addButton = view.findViewById(R.id.add_button);

        addButton.setOnClickListener(v -> main.appInitializer.getMusicDialogManager().showAddPlaylistDialog());


        setupRecyclerView();
        main.deleteManager.setupDeleteLauncher(executor, this);
    }

    @Override
    public void setupRecyclerView() {
        adapter = new PlaylistAdapter();
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
            PlaylistDto playlist = adapter.getCurrentList().get(position);
            adapter.toggleSelection(playlist);
            updateActionModeTitle(adapter.getSelectedItems().size());
        });

        adapter.setOnItemClickListener(position -> {
            PlaylistDto playlist = adapter.getCurrentList().get(position);
            if (actionMode != null) {
                adapter.toggleSelection(playlist);
                updateActionModeTitle(adapter.getSelectedItems().size());
            } else {
                main.navigator.showFragment(PlaylistPersoFragment.newInstance("playlist", playlist.name, playlist.id), changeTheTitle);
            }
        });
    }

    private void setupObservers() {
        ShareSearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(ShareSearchViewModel.class);

        searchViewModel.getQuery().observe(getViewLifecycleOwner(), query -> {
            search = query;
            displayMedias();
        });
    }

    @Override
    public void loadMedias(){
        super.loadMedias();
        main.loaderMediaViewModel.loadPlaylistss().observe(getViewLifecycleOwner(), playlists -> {
            mediaList = new ArrayList<>(playlists);
            requireActivity().runOnUiThread(() -> adapter.submitList(mediaList));
        });
    }

    @Override
    public void displayMedias() {
        if (mediaList == null) return;

        executor.execute(() -> {
            List<PlaylistDto> filtered = new ArrayList<>(fullTextSearchByLogicalOr(mediaList, search, List.of(PlaylistDto::getName)));
            requireActivity().runOnUiThread(() -> adapter.submitList(filtered));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }
}