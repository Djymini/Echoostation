package com.djymini.echoostation.viewModels.musicScannerViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.djymini.echoostation.helpers.MusicScanner;

public class MusicScannerViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isScanning = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> scanProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalFiles = new MutableLiveData<>(0);

    private final MusicScanner musicScanner;

    public MusicScannerViewModel(MusicScanner musicScanner) {
        this.musicScanner = musicScanner;

        this.musicScanner.setScanListener(new MusicScanner.ScanListener() {
            @Override
            public void onScanStarted(int total) {
                totalFiles.postValue(total);
                isScanning.postValue(true);
            }

            @Override
            public void onScanProgress(int scannedCount, int total) {
                scanProgress.postValue(scannedCount);
            }

            @Override
            public void onScanFinished(int total) {
                isScanning.postValue(false);
            }
        });
    }

    public LiveData<Boolean> getIsScanning() {
        return isScanning;
    }

    public LiveData<Integer> getScanProgress() {
        return scanProgress;
    }

    public LiveData<Integer> getTotalFiles() {
        return totalFiles;
    }

    public void startScan() {
        musicScanner.scanDeviceMusic();
    }
}
