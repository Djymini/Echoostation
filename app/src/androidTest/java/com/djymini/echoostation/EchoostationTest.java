package com.djymini.echoostation;


import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MusicTagDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

public class EchoostationTest {
    public EchooStationDatabase db;
    public AlbumDao albumDao;
    public ArtistDao artistDao;
    public GenreDao genreDao;
    public MusicTagDao musicTagDao;
    public MusicDao musicDao;
    public PlaylistDao playlistDao;
    public StatisticDao statisticDao;

    public MusicService musicService;
    public AlbumService albumService;
    public GenreService genreService;
    public ArtistService artistService;
    public StatisticService statisticService;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, EchooStationDatabase.class)
                .allowMainThreadQueries()
                .build();

        musicDao = db.musicDao();
        albumDao = db.albumDao();
        artistDao = db.artistDao();
        genreDao = db.genreDao();
        statisticDao = db.statisticDao();
        musicTagDao = db.musicTagDao();
        playlistDao = db.playlistDao();

        statisticService = new StatisticService(statisticDao);
        musicService = new MusicService(musicDao, musicTagDao, statisticDao, statisticService);
        albumService = new AlbumService(albumDao, statisticDao, statisticService);
        genreService = new GenreService(genreDao, statisticDao, statisticService, ApplicationProvider.getApplicationContext());
        artistService = new ArtistService(artistDao, statisticDao, statisticService, ApplicationProvider.getApplicationContext());
    }

    @After
    public void closeDb() {
        db.close();
    }
}
