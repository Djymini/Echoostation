package com.djymini.echoostation.utilities;

import android.Manifest;
import android.net.Uri;
import com.djymini.echoostation.BuildConfig;


public class Constants {
    // -------- Default values --------
    public static final String UNKNOWN_ALBUM = "Album inconnu";
    public static final String UNKNOWN_GENRE = "Genre inconnu";
    public static final String EMPTY_STRING = "";

    // -------- Permissions --------
    public static final String PERMISSION_READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    // -------- MediaStore URIs --------
    public static final String MEDIASTORE_EXTERNAL = "external";
    public static final Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");

    // -------- Titles --------
    public static final String[] LIBRARY_TAB_TITLE = new String[] {
            "Titres",
            "Albums",
            "Artistes",
            "Genres",
            "Playlists",
    };

    // -------- Fragments --------
    public static final String[] FRAGMENTS_NAMES = new String[] {
            "Accueil",
            "Bibliothèque",
            "Egaliseur",
            "Paramètres"
    };

    public static final String[] FRAGMENTS_TAGS = new String[] {
            "HOME",
            "LIBRARY",
            "EQUALIZER",
            "SETTINGS"
    };

    // -------- API Keys --------
    public static final String TA_CLE_LASTFM = BuildConfig.CLE_LASTFM;
    public static final String CLIENT_ID = BuildConfig.CLIENT_ID_SPOTIFY;
    public static final String CLIENT_SECRET = BuildConfig.CLIENT_SECRET_SPOTIFY;

    // -------- Misc --------
    public static final long TIME_UNSET = 0;
}
