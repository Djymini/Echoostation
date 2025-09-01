package com.djymini.echoostation.viewModels.loaderMediaViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Genre;

import java.util.List;

public class LoaderMediaViewModel extends ViewModel {
    private final MusicDao musicDao;
    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final GenreDao genreDao;

    public LoaderMediaViewModel(MusicDao musicDao, ArtistDao artistDao, AlbumDao albumDao, GenreDao genreDao) {
        this.musicDao = musicDao;
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.genreDao = genreDao;
    }

    public LiveData<List<MusicDto>> loadMusics() {return musicDao.getAllMusicDetailLive();}
    public LiveData<List<Artist>> loadArtists() {return artistDao.getAllLive();}
    public LiveData<List<AlbumDto>> loadAlbums() {return albumDao.getAllAlbumDetailLive();}
    public LiveData<List<Genre>> loadGenres() {return genreDao.getAllLive();}
}
