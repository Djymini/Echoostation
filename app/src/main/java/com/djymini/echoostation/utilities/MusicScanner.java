package com.djymini.echoostation.utilities;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

public class MusicScanner {

    private final Context context;
    private final MusicDao musicDao;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final GenreService genreService;
    private final MusicService musicService;
    private final StatisticService statisticService;
    private final Executor executor;

    public interface ScanListener {
        void onScanStarted(int totalFiles);
        void onScanProgress(int scannedCount, int totalFiles);
        void onScanFinished(int totalFiles);
    }

    private ScanListener listener;

    public void setScanListener(ScanListener listener) {
        this.listener = listener;
    }

    public MusicScanner(Context context, MusicDao musicDao, AlbumService albumService, ArtistService artistService,
                        GenreService genreService, MusicService musicService, StatisticService statisticService, Executor executor) {
        this.context = context;
        this.musicDao = musicDao;
        this.albumService = albumService;
        this.artistService = artistService;
        this.genreService = genreService;
        this.musicService = musicService;
        this.statisticService = statisticService;
        this.executor = executor;
    }

    public void scanDeviceMusic() {
        executor.execute(() -> {
            Set<String> dbPath = new HashSet<>(musicDao.getAllPath());
            Set<String> devicePath = new HashSet<>();

            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.TRACK,
                    MediaStore.Audio.Media.YEAR,
                    MediaStore.Audio.Media.ALBUM_ARTIST,
                    MediaStore.Audio.Media.ALBUM_ID
            };

            String selection = MediaStore.Audio.Media.IS_MUSIC + " != ?";
            String[] selectionArgs = { "0" };
            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

            try (Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            )) {
                if (cursor != null) {
                    int total = cursor.getCount();
                    if (listener != null) listener.onScanStarted(total);

                    int idxPath = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    int idxTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int idxAlbum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                    int idxArtist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    int idxDuration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                    int idxTrack = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);
                    int idxYear = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR);
                    int idxAlbumArtist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST);
                    int idxAlbumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                    int idxId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

                    int count = 0;
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(idxPath);
                        devicePath.add(path);

                        // Ajouter seulement si pas déjà en base
                        if (!dbPath.contains(path)) {
                            String title = cursor.getString(idxTitle);
                            String album = cursor.getString(idxAlbum);
                            String artist = cursor.getString(idxArtist);
                            long duration = cursor.getLong(idxDuration);
                            int track = cursor.getInt(idxTrack);
                            int year = cursor.getInt(idxYear);
                            String albumArtist = cursor.getString(idxAlbumArtist);
                            long albumIdMediaStore = cursor.getLong(idxAlbumId);
                            long audioId = cursor.getLong(idxId);

                            String genre = getGenreFromAudioId(audioId);
                            Uri albumArtUri = ContentUris.withAppendedId(Constants.ALBUM_ART_URI, albumIdMediaStore);
                            String coverAlbum = albumArtUri.toString();

                            long genreId = genreService.add(genre, statisticService);
                            long albumId = albumService.add(album, coverAlbum, year, albumArtist, artistService, statisticService);
                            musicService.add(path, title, duration, track, artist, albumId, genreId, artistService, statisticService);
                        }

                        count++;
                        if (listener != null) listener.onScanProgress(count, total);
                    }

                    // Nettoyer la DB des fichiers supprimés
                    for (String path : dbPath) {
                        if (!devicePath.contains(path)) {
                            musicDao.deleteByPath(path);
                        }
                    }

                    if (listener != null) listener.onScanFinished(total);
                }
            }
        });
    }

    private String getGenreFromAudioId(long audioId) {
        String genre = Constants.UNKNOWN_GENRE;
        Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId(Constants.MEDIASTORE_EXTERNAL, (int) audioId);

        try (Cursor genreCursor = context.getContentResolver().query(
                uri,
                new String[]{MediaStore.Audio.Genres.NAME},
                null,
                null,
                null
        )) {
            if (genreCursor != null && genreCursor.moveToFirst()) {
                genre = genreCursor.getString(genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME));
            }
        }

        return genre;
    }
}
