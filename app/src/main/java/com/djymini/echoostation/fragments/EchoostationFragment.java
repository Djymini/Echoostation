package com.djymini.echoostation.fragments;

import androidx.fragment.app.Fragment;

import com.djymini.echoostation.EchooStationDatabase;
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MoodDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;

public class EchoostationFragment extends Fragment{
    public AlbumDao albumDao;
    public ArtistDao artistDao;
    public GenreDao genreDao;
    public MoodDao moodDao;
    public MusicDao musicDao;
    public PlaylistDao playlistDao;
    public StatisticDao statisticDao;

    public AlbumService albumService;
    public ArtistService artistService;
    public GenreService genreService;
    public MusicService musicService;
    public StatisticService statisticService;

    public void setupDaoAndService(EchooStationDatabase db){
        musicDao = db.musicDao();
        statisticDao = db.statisticDao();
        artistDao = db.artistDao();
        albumDao = db.albumDao();
        genreDao = db.genreDao();
        moodDao = db.moodDao();
        playlistDao = db.playlistDao();

        artistService = new ArtistService(artistDao, statisticDao);
        albumService = new AlbumService(albumDao, statisticDao);
        genreService = new GenreService(genreDao, statisticDao);
        musicService = new MusicService(musicDao, statisticDao);
    }
}
