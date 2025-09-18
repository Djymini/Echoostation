package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.ArtistAdapter;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.utilities.SortOptionArtist;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ArtistFragment extends EchoostationFragment {
    private List<ArtistDto> currentArtistList = new ArrayList<>();
    private ArtistAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        setupDaoAndService();
        setupLoaderMedia();
        executor = Executors.newSingleThreadExecutor();

        setupUI(view);
        setupObservers();
        loadMedias();

        return view;
    }

    private void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_artist);
        counterView = view.findViewById(R.id.number_artist);
        spinner = view.findViewById(R.id.spinner);
        ImageButton shuffleButton = view.findViewById(R.id.shuffle_button);

        shuffleButton.setOnClickListener(v -> MediaItemHelper.shuffleArtist(currentArtistList, main, requireContext(), executor));

        setupRecyclerView();
        setupSpinner();
    }

    private void setupRecyclerView() {
        adapter = new ArtistAdapter();
        RecyclerViewHelper.setupRecyclerViewGrid(recyclerView, getContext(), adapter, 3, true);

        recyclerView.setBubbleColor(ContextCompat.getColor(requireContext(), R.color.colorSecondary));
        recyclerView.setBubbleTextColor(ContextCompat.getColor(requireContext(), R.color.colorText));
        recyclerView.setHandleColor(ContextCompat.getColor(requireContext(), R.color.colorThird));

        adapter.setOnItemClickListener(position -> {
            FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();
            Fragment fragment = ArtistInfoFragment.newInstance(adapter.getCurrentList().get(position));

            if (!fragment.isAdded()) {
                transaction.add(R.id.frame_layout, fragment);
            } else {
                transaction.show(fragment);
            }
            transaction.hide(main.navigator.getActiveFragment()).commit();

            main.navigator.setActiveFragment(fragment);
            main.navigator.updateToolbarMenu(fragment);
        });
    }

    private void setupSpinner() {
        List<String> displayNames = new ArrayList<>();
        for (SortOptionArtist option : SortOptionArtist.values()) {
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
            // TODO visuel lecture
        });

        main.playerViewModel.getCurrentItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                // TODO visuel item sélectionné
            }
        });
    }

    @Override
    public void loadMedias() {
        main.navigator.modifyTitle(getString(R.string.library_fragment));
        loaderMediaViewModel.loadArtists().observe(getViewLifecycleOwner(), artists -> {
            currentArtistList = new ArrayList<>(artists);
            sortAndDisplayMedias(spinner.getSelectedItemPosition());
            String counterAlbum = artists.size() + getString(R.string.album_fragment);
            counterView.setText(counterAlbum);
        });
    }

    @Override
    public void sortAndDisplayMedias(int position) {
        if (currentArtistList == null) return;

        executor.execute(() -> {
            List<ArtistDto> filtered = new ArrayList<>(fullTextSearchByLogicalOr(currentArtistList, search, List.of(ArtistDto::getName)));

            if (position >= 0 && position < SortOption.values().length) {
                SortOptionArtist option = SortOptionArtist.values()[position];
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