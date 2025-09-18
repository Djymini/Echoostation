package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.AlbumAdapter;
import com.djymini.echoostation.adapters.PlaylistAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.PlaylistDto;
import com.djymini.echoostation.fragments.playlistMusicFragment.PlaylistPersoFragment;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class PlaylistFragment extends EchoostationFragment {
    private List<PlaylistDto> currentPlaylistList = new ArrayList<>();
    private PlaylistAdapter adapter;
    private ImageButton addButton;

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
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        setupDaoAndService();
        setupLoaderMedia();
        executor = Executors.newSingleThreadExecutor();

        setupUI(view);
        setupObservers();
        loadMedias();

        return view;
    }

    private void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_playlist);
        addButton = view.findViewById(R.id.add_button);

        addButton.setOnClickListener(v -> {
            main.appInitializer.getMusicDialogManager().showAddPlaylistDialog();
        });


        setupRecyclerView();
        main.deleteManager.setupDeleteLauncher(executor, this);
    }

    private void setupRecyclerView() {
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
            updateActionModeTitle();
        });

        adapter.setOnItemClickListener(position -> {
            PlaylistDto playlist = adapter.getCurrentList().get(position);
            if (actionMode != null) {
                adapter.toggleSelection(playlist);
                updateActionModeTitle();
            } else {
                if(getActivity() instanceof MainActivity){
                    MainActivity main = (MainActivity) getActivity();
                    FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

                    Fragment fragment = PlaylistPersoFragment.newInstance("playlist", playlist.name, playlist.id);

                    if (!fragment.isAdded()) {
                        transaction.add(R.id.frame_layout, fragment);
                    } else {
                        transaction.show(fragment);
                    }

                    transaction.hide(main.navigator.getActiveFragment()).commit();

                    main.navigator.setActiveFragment(fragment);
                    main.navigator.updateToolbarMenu(fragment);
                }
            }
        });
    }

    private void setupObservers() {
        ShareSearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(ShareSearchViewModel.class);
        main.playerViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);

        main.playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            // TODO visuel lecture
        });

        main.playerViewModel.getCurrentItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                // TODO visuel item sélectionné
            }
        });
    }

    @Override
    public void loadMedias(){
        main.navigator.modifyTitle(getString(R.string.library_fragment));
        loaderMediaViewModel.loadPlaylistss().observe(getViewLifecycleOwner(), playlists -> {
            currentPlaylistList = new ArrayList<>(playlists);
            requireActivity().runOnUiThread(() -> adapter.submitList(currentPlaylistList));
        });
    }

    private void updateActionModeTitle() {
        int count = adapter.getSelectedItems().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(count + getString(R.string.item_selected));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }
}