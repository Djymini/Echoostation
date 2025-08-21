package com.djymini.echoostation.services;

import android.content.Context;

import com.djymini.echoostation.EchooStationDatabase;
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MoodDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.dataBase.DatabaseClient;

public class DatabaseService {
    private EchooStationDatabase database;
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

    public DatabaseService(Context context) {
        this.database = DatabaseClient.getInstance(context).getDatabase();

        this.albumDao = database.albumDao();
        this.artistDao = database.artistDao();
        this.genreDao =database. genreDao();
        this.moodDao = database.moodDao();
        this.musicDao = database.musicDao();
        this.playlistDao = database.playlistDao();
        this.statisticDao = database.statisticDao();

        this.statisticService = new StatisticService(this.statisticDao);
        this.albumService = new AlbumService(this.albumDao, this.statisticDao, this.statisticService);
        this.artistService = new ArtistService(this.artistDao, this.statisticDao, this.statisticService, context);
        this.genreService = new GenreService(this.genreDao, this.statisticDao, this.statisticService, context);
        this.musicService = new MusicService(this.musicDao, this.statisticDao, this.statisticService);
    }

    public AlbumDao getAlbumDao() {
        return albumDao;
    }

    public ArtistDao getArtistDao() {
        return artistDao;
    }

    public GenreDao getGenreDao() {
        return genreDao;
    }

    public MoodDao getMoodDao() {
        return moodDao;
    }

    public MusicDao getMusicDao() {
        return musicDao;
    }

    public PlaylistDao getPlaylistDao() {
        return playlistDao;
    }

    public StatisticDao getStatisticDao() {
        return statisticDao;
    }

    public AlbumService getAlbumService() {
        return albumService;
    }

    public ArtistService getArtistService() {
        return artistService;
    }

    public GenreService getGenreService() {
        return genreService;
    }

    public MusicService getMusicService() {
        return musicService;
    }

    public StatisticService getStatisticService() {
        return statisticService;
    }
}
