package com.djymini.echoostation.fragments.mediaFragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.GenreAdapter;
import com.djymini.echoostation.entities.Genre;
import com.djymini.echoostation.fragments.playlistMusicFragment.GenrePlaylistFragment;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.utilities.SortOptionGenre;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class GenreFragment extends MediaFragment<Genre, GenreAdapter> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

        setupUI(view);
        setupObservers();
        loadMedias();

        return view;
    }

    @Override
    public void setupUI(View view) {
        super.setupUI(view);
        shuffleButton.setOnClickListener(v -> MediaItemHelper.shuffleGenre(mediaList, main, requireContext(), executor));

        setupRecyclerView();
        setupSpinner();
    }

    @Override
    public void setupRecyclerView() {
        adapter = new GenreAdapter();
        RecyclerViewHelper.setupRecyclerViewGrid(recyclerView, getContext(), adapter, 2, true);

        recyclerView.setBubbleColor(ContextCompat.getColor(requireContext(), R.color.colorSecondary));
        recyclerView.setBubbleTextColor(ContextCompat.getColor(requireContext(), R.color.colorText));
        recyclerView.setHandleColor(ContextCompat.getColor(requireContext(), R.color.colorThird));

        adapter.setOnItemClickListener(position -> main.navigator.showFragment(GenrePlaylistFragment.newInstance("Genre", adapter.getCurrentList().get(position).name, adapter.getCurrentList().get(position).id), changeTheTitle));
    }

    private void setupSpinner() {
        List<String> displayNames = new ArrayList<>();
        for (SortOptionGenre option : SortOptionGenre.values()) {
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
    public void loadMedias() {
        super.loadMedias();
        main.loaderMediaViewModel.loadGenres().observe(getViewLifecycleOwner(), genres -> {
            mediaList = new ArrayList<>(genres);
            sortAndDisplayMedias(spinner.getSelectedItemPosition());
            String counterAlbum = genres.size() + getString(R.string.album_fragment);
            counterView.setText(counterAlbum);
        });
    }

    @Override
    public void sortAndDisplayMedias(int position) {
        if (mediaList == null) return;

        executor.execute(() -> {
            List<Genre> filtered = new ArrayList<>(fullTextSearchByLogicalOr(mediaList, search, List.of(Genre::getName)));

            if (position >= 0 && position < SortOption.values().length) {
                SortOptionGenre option = SortOptionGenre.values()[position];
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