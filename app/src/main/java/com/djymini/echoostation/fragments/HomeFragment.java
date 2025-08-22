package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.utilities.SectionLibrary;
import com.djymini.echoostation.views.ViewHomeData;

import java.util.Map;

public class HomeFragment extends EchoostationFragment {
    private final Map<Integer, SectionLibrary> sections = Map.of(
            0, new SectionLibrary(R.id.data_music, Constants.LIBRARY_TAB_TITLE[0].toUpperCase()),
            1, new SectionLibrary(R.id.data_album, Constants.LIBRARY_TAB_TITLE[1].toUpperCase()),
            2, new SectionLibrary(R.id.data_artist, Constants.LIBRARY_TAB_TITLE[2].toUpperCase()),
            3, new SectionLibrary(R.id.data_genre, Constants.LIBRARY_TAB_TITLE[3].toUpperCase())
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO: Make the home fragment for display best album, best artist, best song, never listening and other features (features/home)
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FragmentActivity activity = requireActivity();
        if (!(activity instanceof MainActivity)) return view;
        MainActivity mainActivity = (MainActivity) activity;

        sections.forEach((index, section) -> {
            ViewHomeData sectionView = view.findViewById(section.viewId);
            sectionView.setTitle(section.title);

            sectionView.setOnClickListener(v -> openLibraryTab(index));

            switch (index) {
                case 0:
                    mainActivity.loaderMediaViewModel.loadMusics().observe(getViewLifecycleOwner(),
                            musics -> sectionView.setData(String.valueOf(musics.size())));
                    break;
                case 1:
                    mainActivity.loaderMediaViewModel.loadAlbums().observe(getViewLifecycleOwner(),
                            albums -> sectionView.setData(String.valueOf(albums.size())));
                    break;
                case 2:
                    mainActivity.loaderMediaViewModel.loadArtists().observe(getViewLifecycleOwner(),
                            artists -> sectionView.setData(String.valueOf(artists.size())));
                    break;
                case 3:
                    mainActivity.loaderMediaViewModel.loadGenres().observe(getViewLifecycleOwner(),
                            genres -> sectionView.setData(String.valueOf(genres.size())));
                    break;
            }
        });

        mainActivity.navigator.updateMiniPlayerVisibility(this);
        return view;
    }

    private void openLibraryTab(int tabIndex) {
        FragmentActivity activity = requireActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).openLibraryTab(tabIndex);

        }
    }
}