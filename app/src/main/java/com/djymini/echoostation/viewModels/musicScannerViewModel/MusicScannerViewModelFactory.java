package com.djymini.echoostation.viewModels.musicScannerViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.djymini.echoostation.helpers.MusicScanner;

public class MusicScannerViewModelFactory implements ViewModelProvider.Factory{
    private final MusicScanner musicScanner;

    public MusicScannerViewModelFactory(MusicScanner musicScanner) {
        this.musicScanner = musicScanner;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MusicScannerViewModel.class)) {
            return (T) new MusicScannerViewModel(musicScanner);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
