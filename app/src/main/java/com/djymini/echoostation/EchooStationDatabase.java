package com.djymini.echoostation;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MusicTagDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.ArtistMusic;
import com.djymini.echoostation.entities.Genre;
import com.djymini.echoostation.entities.MusicTag;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.entities.MusicMood;
import com.djymini.echoostation.entities.MusicPlaylist;
import com.djymini.echoostation.entities.Playlist;
import com.djymini.echoostation.entities.Statistic;

@Database(
        entities = {Album.class, Artist.class, ArtistMusic.class, Genre.class, MusicTag.class, Music.class, MusicPlaylist.class, Playlist.class, Statistic.class},
        version = 3
)
public abstract class EchooStationDatabase extends RoomDatabase {
    public abstract AlbumDao albumDao();
    public abstract ArtistDao artistDao();
    public abstract GenreDao genreDao();
    public abstract MusicDao musicDao();
    public abstract PlaylistDao playlistDao();
    public abstract MusicTagDao musicTagDao();
    public abstract StatisticDao statisticDao();
}
