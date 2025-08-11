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
                    while (cursor.moveToNext()) {
                        processMusicCursor(cursor);
                    }
                }
            }
        });
    }

    private void processMusicCursor(Cursor cursor) {
        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
        String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
        long audioId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        String genre = getGenreFromAudioId(audioId);
        int track = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK));
        int year = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
        String albumArtist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST));
        long albumIdMediaStore = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        Uri albumArtUri = ContentUris.withAppendedId(Constants.ALBUM_ART_URI, albumIdMediaStore);
        String coverAlbum = albumArtUri.toString();

        long genreId = genreService.add(genre, statisticService);
        long albumId = albumService.add(album, coverAlbum, year, albumArtist, artistService, statisticService);
        musicService.add(path, title, duration, track, artist, albumId, genreId, artistService, statisticService);
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
