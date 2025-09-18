package com.djymini.echoostation.viewModels.loaderMediaViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;

public class LoaderMusicViewModelFactory implements ViewModelProvider.Factory{
    private final MusicDao musicDao;
    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final GenreDao genreDao;
    private final PlaylistDao playlistDao;

    public LoaderMusicViewModelFactory(MusicDao musicDao, ArtistDao artistDao, AlbumDao albumDao, GenreDao genreDao, PlaylistDao playlistDao) {
        this.musicDao = musicDao;
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.genreDao = genreDao;
        this.playlistDao = playlistDao;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoaderMediaViewModel.class)) {
            return (T) new LoaderMediaViewModel(musicDao, artistDao, albumDao, genreDao, playlistDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
