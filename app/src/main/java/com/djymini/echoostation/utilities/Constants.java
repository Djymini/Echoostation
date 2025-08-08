package com.djymini.echoostation.utilities;

import android.Manifest;
import android.net.Uri;

public class Constants {
    // Default values
    public static final String UNKNOWN_ALBUM = "Album inconnu";
    public static final String UNKNOWN_GENRE = "Genre inconnu";
    public static final String EMPTY_STRING = "";

    // Logs
    public static final String TAG_FILE_DELETE = "FileDelete";
    public static final String MSG_DELETE_WITH_AUTHORIZATION = "Delete with user authorization";
    public static final String MSG_DELETE_CANCELED = "Delete canceled by user";
    public static final String MSG_DELETE_ERROR = "Error during delete request";
    public static final String MSG_DELETE_SUCCESS = "File deleted successfully";
    public static final String MSG_DELETE_FAILED = "Failed to delete file";

    // Permissions
    public static final String PERMISSION_READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    // Request codes
    public static final int REQUEST_CODE_DELETE = 1001;

    // MediaStore URIs
    public static final String MEDIASTORE_EXTERNAL = "external";
    public static final Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");

    // Bundle Keys
    public static final String EXTRA_MUSIC_ID = "extra_music_id";
}
