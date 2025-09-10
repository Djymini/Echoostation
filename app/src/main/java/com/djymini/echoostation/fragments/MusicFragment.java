package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.view.ActionMode;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.MusicAdapter;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;

public class MusicFragment extends EchoostationFragment {
    private FastScrollRecyclerView recyclerView;
    private List<MusicDto> currentMusicList = new ArrayList<>();
    private MusicAdapter adapter;
    private TextView musicCounterView;
    private Spinner spinner;
    private String search;
    private ActionMode actionMode;
    private ExecutorService executor;
    private List<MediaItem> playlist;
    private MainActivity main;

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
                main.deleteManager.confirmAndDeleteSelectedMusics(selectedCopy, requireContext(), MusicFragment.this, executor);
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
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        setupDaoAndService(main);
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
        ImageButton shuffleButton = view.findViewById(R.id.shuffle_button);

        shuffleButton.setOnClickListener(v -> MediaItemHelper.shuffleMusic(currentMusicList, playlist, main, requireContext()));

        setupRecyclerView();
        setupSpinner();
        main.deleteManager.setupDeleteLauncher(executor, this);
    }

    private void setupRecyclerView() {
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
            updateActionModeTitle();
        });

        adapter.setOnItemClickListener(position -> {
            if (actionMode != null) {
                MusicDto music = adapter.getCurrentList().get(position);
                adapter.toggleSelection(music);
                updateActionModeTitle();
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
                sortAndDisplayMusics(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupObservers() {
        ShareSearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(ShareSearchViewModel.class);

        searchViewModel.getQuery().observe(getViewLifecycleOwner(), query -> {
            search = query;
            sortAndDisplayMusics(spinner.getSelectedItemPosition());
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

                requireActivity().runOnUiThread(() -> adapter.setSortOption(option));
            }

            playlist = MediaItemHelper.loadPlaylist(filtered);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }
}
