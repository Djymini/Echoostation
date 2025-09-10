package com.djymini.echoostation.fragments;

import androidx.fragment.app.Fragment;

import com.djymini.echoostation.EchooStationDatabase;
import com.djymini.echoostation.MainActivity;
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

    public void setupDaoAndService(MainActivity main){
        musicDao = main.dbService.getMusicDao();
        statisticDao = main.dbService.getStatisticDao();
        artistDao = main.dbService.getArtistDao();
        albumDao = main.dbService.getAlbumDao();
        genreDao = main.dbService.getGenreDao();
        moodDao = main.dbService.getMoodDao();
        playlistDao = main.dbService.getPlaylistDao();

        statisticService = new StatisticService(statisticDao);
        albumService = new AlbumService(albumDao, statisticDao, statisticService);
        artistService = new ArtistService(artistDao, statisticDao, statisticService, requireContext());
        genreService = new GenreService(genreDao, statisticDao, statisticService, requireContext());
        musicService = new MusicService(musicDao, statisticDao, statisticService);
    }
}
