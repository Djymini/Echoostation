package com.djymini.echoostation.helpers;

import android.app.Activity;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;

import com.djymini.echoostation.services.DatabaseService;
import com.djymini.echoostation.ui.MusicDialogManager;

import java.util.concurrent.Executor;

public class AppInitializer {
    private MusicScanner musicScanner;
    private PermissionManager permissionManager;
    private MusicDialogManager musicDialogManager;

    public AppInitializer(DatabaseService dbService, ActivityResultLauncher<String> requestPermissionLauncher, Runnable onPermissionOn, Executor executor, Context context, Activity activity) {
        musicScanner = new MusicScanner(context, dbService.getMusicDao(), dbService.getAlbumService(), dbService.getArtistService(), dbService.getGenreService(), dbService.getMusicService(), dbService.getStatisticService(), executor);
        permissionManager = new PermissionManager(activity, requestPermissionLauncher, onPermissionOn, null);
        musicDialogManager = new MusicDialogManager(activity, dbService.getMusicDao(), dbService.getAlbumDao(), dbService.getArtistDao(), dbService.getMusicService(), dbService.getAlbumService(), dbService.getArtistService(), dbService.getGenreService(), dbService.getStatisticService(), executor);
        permissionManager.registerPermissionLauncher();
    }

    public MusicScanner getMusicScanner() {
        return musicScanner;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public MusicDialogManager getMusicDialogManager() {
        return musicDialogManager;
    }
}
