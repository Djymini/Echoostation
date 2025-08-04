package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djymini.echoostation.EchooStationDatabase;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.MusicAdapter;
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MoodDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.dataBase.DatabaseClient;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;

import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends Fragment {
    private RecyclerView recyclerView;
    private MusicAdapter adapter;

    private AlbumDao albumDao;
    private ArtistDao artistDao;
    private GenreDao genreDao;
    private MoodDao moodDao;
    private MusicDao musicDao;
    private PlaylistDao playlistDao;
    private StatisticDao statisticDao;

    private AlbumService albumService;
    private ArtistService artistService;
    private GenreService genreService;
    private MusicService musicService;
    private StatisticService statisticService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        EchooStationDatabase db = DatabaseClient.getInstance(requireContext()).getDatabase();
        musicDao = db.musicDao();
        statisticDao = db.statisticDao();
        artistDao = db.artistDao();
        albumDao = db.albumDao();
        artistService = new ArtistService(artistDao, statisticDao);
        albumService = new AlbumService(albumDao, statisticDao);

        recyclerView = view.findViewById(R.id.recycler_view_song);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter(artistService, albumService);
        recyclerView.setAdapter(adapter);

        loadMusics();

        return view;
    }

    private void loadMusics() {
        musicDao.getAllLive().observe(getViewLifecycleOwner(), musics -> {
            adapter.submitList(musics);
        });
    }
}
