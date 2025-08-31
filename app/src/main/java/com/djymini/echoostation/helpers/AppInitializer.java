package com.djymini.echoostation.helpers;

import com.djymini.echoostation.ui.MusicDialogManager;


public class AppInitializer {
    private final MusicScanner musicScanner;
    private final PermissionManager permissionManager;
    private final MusicDialogManager musicDialogManager;

    public AppInitializer(MusicScanner musicScanner, PermissionManager permissionManager, MusicDialogManager musicDialogManager) {
        this.musicScanner = musicScanner;
        this.permissionManager = permissionManager;
        this.musicDialogManager = musicDialogManager;
        this.permissionManager.registerPermissionLauncher();
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
