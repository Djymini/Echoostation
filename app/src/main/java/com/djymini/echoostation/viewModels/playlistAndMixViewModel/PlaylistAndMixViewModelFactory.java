package com.djymini.echoostation.viewModels.playlistAndMixViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.services.MusicService;

import java.util.concurrent.Executor;

public class PlaylistAndMixViewModelFactory implements ViewModelProvider.Factory {
    private MusicDao musicDao;
    private MusicService musicService;
    private Executor executor;

    public PlaylistAndMixViewModelFactory(MusicDao musicDao, MusicService musicService, Executor executor) {
        this.musicDao = musicDao;
        this.musicService = musicService;
        this.executor = executor;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PlaylistAndMixViewModel.class)) {
            return (T) new PlaylistAndMixViewModel(musicDao, musicService, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
