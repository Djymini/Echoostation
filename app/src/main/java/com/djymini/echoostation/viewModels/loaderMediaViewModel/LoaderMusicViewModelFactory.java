package com.djymini.echoostation.viewModels.loaderMediaViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MusicDao;

public class LoaderMusicViewModelFactory implements ViewModelProvider.Factory{
    private final MusicDao musicDao;
    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final GenreDao genreDao;

    public LoaderMusicViewModelFactory(MusicDao musicDao, ArtistDao artistDao, AlbumDao albumDao, GenreDao genreDao) {
        this.musicDao = musicDao;
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.genreDao = genreDao;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoaderMediaViewModel.class)) {
            return (T) new LoaderMediaViewModel(musicDao, artistDao, albumDao, genreDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
