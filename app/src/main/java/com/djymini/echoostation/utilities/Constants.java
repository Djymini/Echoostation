package com.djymini.echoostation.utilities;

import android.Manifest;
import android.net.Uri;

public class Constants {
    // Default values
    public static final String UNKNOWN_ALBUM = "Album inconnu";
    public static final String UNKNOWN_GENRE = "Genre inconnu";
    public static final String EMPTY_STRING = "";

    // Permissions
    public static final String PERMISSION_READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    // MediaStore URIs
    public static final String MEDIASTORE_EXTERNAL = "external";
    public static final Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");

    // Titles
    public static final String MUSIC_TITLE = "Titres";
    public static final String ALBUM_TITLE = "Albums";
    public static final String ARTIST_TITLE = "Artistes";
    public static final String GENRE_TITLE = "Genres";
    public static final String PLAYLIST_TITLE = "Playlists";

    // Sorting categories
    public static final String[] SORT_CATEGORIES = new String[] {
            "Trie par titre : A -> Z",
            "Trie par titre : Z -> A",
            "Trie par durée : courte -> longue",
            "Trie par durée : longue -> courte",
            "Trie par album : A -> Z",
            "Trie par album : Z -> A",
            "Trie par artiste : A -> Z",
            "Trie par artiste : Z -> A",
            "Trie par nombre d'écoutes : plus -> moins",
            "Trie par nombre d'écoutes : moins -> plus",
            "Trie par date d'ajout : récent -> ancien",
            "Trie par date d'ajout : ancien -> récent"
    };

    // Fragments
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

    // Misc
    public static final long TIME_UNSET = 0;
}
