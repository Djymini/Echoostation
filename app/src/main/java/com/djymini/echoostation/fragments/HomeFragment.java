package com.djymini.echoostation.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.EchooStationDatabase;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.dataBase.DatabaseClient;
import com.djymini.echoostation.views.ViewHomeData;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeFragment extends EchoostationFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO: Make the home fragment for display best album, best artist, best song, never listening and other features (features/home)
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        EchooStationDatabase db = DatabaseClient.getInstance(requireContext()).getDatabase();
        setupDaoAndService(db);

        ViewHomeData musicData = view.findViewById(R.id.data_music);
        ViewHomeData albumData = view.findViewById(R.id.data_album);
        ViewHomeData artisteData = view.findViewById(R.id.data_artist);
        ViewHomeData genreData = view.findViewById(R.id.data_genre);
        ViewHomeData playlistData = view.findViewById(R.id.data_playlist);

        musicData.setTitle("MUSIQUES");
        albumData.setTitle("ALBUMS");
        artisteData.setTitle("ARTISTES");
        genreData.setTitle("GENRES");
        playlistData.setTitle("PLAYLISTS");

        musicData.setOnClickListener(v -> openLibraryTab(0));
        albumData.setOnClickListener(v -> openLibraryTab(1));
        artisteData.setOnClickListener(v -> openLibraryTab(2));
        genreData.setOnClickListener(v -> openLibraryTab(3));
        playlistData.setOnClickListener(v -> openLibraryTab(4));

        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) getActivity()).loaderMediaViewModel.loadMusics().observe(getViewLifecycleOwner(), musics -> {
                musicData.setData(String.valueOf(musics.size()));
            });
            ((MainActivity) getActivity()).loaderMediaViewModel.loadArtists().observe(getViewLifecycleOwner(), artists -> {
                artisteData.setData(String.valueOf(artists.size()));
            });
            ((MainActivity) getActivity()).loaderMediaViewModel.loadAlbums().observe(getViewLifecycleOwner(), albums -> {
                albumData.setData(String.valueOf(albums.size()));
            });
            ((MainActivity) getActivity()).loaderMediaViewModel.loadGenres().observe(getViewLifecycleOwner(), genres -> {
                genreData.setData(String.valueOf(genres.size()));
            });
            playlistData.setData(String.valueOf(0));

            ((MainActivity) getActivity()).navigator.updateMiniPlayerVisibility(this);
        }

        return view;
    }

    private void openLibraryTab(int tabIndex) {
        FragmentActivity activity = requireActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).openLibraryTab();
        }
    }

}