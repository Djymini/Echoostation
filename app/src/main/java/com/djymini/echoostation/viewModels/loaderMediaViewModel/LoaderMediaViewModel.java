package com.djymini.echoostation.viewModels.loaderMediaViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.dtos.PlaylistDto;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Genre;

import java.util.ArrayList;
import java.util.List;

public class LoaderMediaViewModel extends ViewModel {
    private MusicDao musicDao;
    private ArtistDao artistDao;
    private AlbumDao albumDao;
    private GenreDao genreDao;
    private PlaylistDao playlistDao;


    public LoaderMediaViewModel(MusicDao musicDao, ArtistDao artistDao, AlbumDao albumDao, GenreDao genreDao, PlaylistDao playlistDao) {
        this.musicDao = musicDao;
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.genreDao = genreDao;
        this.playlistDao = playlistDao;
    }

    public LiveData<List<MusicDto>> loadMusics() {return musicDao.getAllMusicDetailLive();}
    public LiveData<List<ArtistDto>> loadArtists() {return artistDao.getAllDetailLive();}
    public LiveData<List<AlbumDto>> loadAlbums() {return albumDao.getAllAlbumDetailLive();}
    public LiveData<List<Genre>> loadGenres() {return genreDao.getAllLive();}
    public LiveData<List<PlaylistDto>> loadPlaylistss() {return playlistDao.getAllDtoLive();}
}
