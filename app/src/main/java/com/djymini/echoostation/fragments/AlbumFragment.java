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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.AlbumAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.utilities.SortOptionAlbum;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class AlbumFragment extends EchoostationFragment {
    private List<AlbumDto> currentAlbumList = new ArrayList<>();
    private AlbumAdapter adapter;

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
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        setupDaoAndService();
        executor = Executors.newSingleThreadExecutor();

        setupUI(view);
        setupObservers();
        loadMedias();

        return view;
    }

    private void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_album);
        counterView = view.findViewById(R.id.number_album);
        spinner = view.findViewById(R.id.spinner);
        ImageButton shuffleButton = view.findViewById(R.id.shuffle_button);

        shuffleButton.setOnClickListener(v -> MediaItemHelper.shuffleAlbum(currentAlbumList, main, requireContext(), executor));

        setupRecyclerView();
        setupSpinner();
        main.deleteManager.setupDeleteLauncher(executor, this);
    }

    private void setupRecyclerView() {
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
            updateActionModeTitle();
        });

        adapter.setOnItemClickListener(position -> {
            if (actionMode != null) {
                AlbumDto album = adapter.getCurrentList().get(position);
                adapter.toggleSelection(album);
                updateActionModeTitle();
            } else {
                if(getActivity() instanceof MainActivity){
                    MainActivity main = (MainActivity) getActivity();
                    FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

                    Fragment fragment = AlbumInfoFragment.newInstance(adapter.getCurrentList().get(position));

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
        main.playerViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);

        searchViewModel.getQuery().observe(getViewLifecycleOwner(), query -> {
            search = query;
            sortAndDisplayMedias(spinner.getSelectedItemPosition());
        });

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
        main.loaderMediaViewModel.loadAlbums().observe(getViewLifecycleOwner(), albums -> {
            currentAlbumList = new ArrayList<>(albums);
            sortAndDisplayMedias(spinner.getSelectedItemPosition());
            String counterAlbum = albums.size() + getString(R.string.album_fragment);
            counterView.setText(counterAlbum);
        });
    }

    @Override
    public void sortAndDisplayMedias(int position) {
        if (currentAlbumList == null) return;

        executor.execute(() -> {
            List<AlbumDto> filtered = new ArrayList<>(fullTextSearchByLogicalOr(currentAlbumList, search, List.of(AlbumDto::getName, AlbumDto::getArtistName)));
            if (position >= 0 && position < SortOption.values().length) {
                SortOptionAlbum option = SortOptionAlbum.values()[position];
                filtered.sort(option.getComparator());
                requireActivity().runOnUiThread(() -> adapter.setSortOption(option));
            }
            requireActivity().runOnUiThread(() -> adapter.submitList(filtered));
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