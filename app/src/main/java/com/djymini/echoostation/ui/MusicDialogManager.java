package com.djymini.echoostation.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djymini.echoostation.R;
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class MusicDialogManager {

    private final Activity activity;
    private final MusicDao musicDao;
    private final AlbumDao albumDao;
    private final ArtistDao artistDao;
    private final MusicService musicService;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final GenreService genreService;
    private final StatisticService statisticService;
    private final Executor executor;
    private long lastMusicDeletedId = -1;

    public static final int REQUEST_CODE_DELETE = 1001;

    public MusicDialogManager(Activity activity, MusicDao musicDao, AlbumDao albumDao, ArtistDao artistDao, MusicService musicService,
                              AlbumService albumService, ArtistService artistService, GenreService genreService, StatisticService statisticService, Executor executor) {
        this.activity = activity;
        this.musicDao = musicDao;
        this.albumDao = albumDao;
        this.artistDao = artistDao;
        this.musicService = musicService;
        this.albumService = albumService;
        this.artistService = artistService;
        this.genreService = genreService;
        this.statisticService = statisticService;
        this.executor = executor;
    }

    public void showBottomDialog(MusicDto musicDto) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        TextView title = dialog.findViewById(R.id.title_dialog);
        title.setText(musicDto.title);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout editLayout = dialog.findViewById(R.id.layout_edit);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layout_delete);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(activity, "Upload a Video is clicked", Toast.LENGTH_SHORT).show();
        });

        editLayout.setOnClickListener(v -> {
            dialog.dismiss();
            showEditMusicDialog(musicDto);
        });

        deleteLayout.setOnClickListener(v -> {
            dialog.dismiss();
            handleDeleteMusic(musicDto);
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            window.setGravity(Gravity.BOTTOM);
        }

    }

    public void handleDeleteMusic(MusicDto musicDto) {
        long mediaStoreId = getMediaStoreIdFromPath(activity, musicDto.path);
        if (mediaStoreId == -1) return;

        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaStoreId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            List<Uri> uris = Collections.singletonList(uri);
            try {
                IntentSender deleteRequest = MediaStore.createDeleteRequest(
                        activity.getContentResolver(), uris
                ).getIntentSender();

                lastMusicDeletedId = musicDto.id;

                activity.startIntentSenderForResult(deleteRequest, REQUEST_CODE_DELETE, null, 0, 0, 0);

            } catch (IntentSender.SendIntentException e) {
                Log.e("MusicDialogManager", "Erreur lors de la demande de suppression de musique", e);
            }
        } else {
            int deleted = activity.getContentResolver().delete(uri, null, null);
            if (deleted > 0) {
                executor.execute(() -> {
                    Music musicForDelete = musicDao.getById(musicDto.id);
                    if (musicForDelete != null) {
                        musicDao.delete(musicForDelete);
                    }
                });
            }
        }
    }

    public static long getMediaStoreIdFromPath(Context context, String path) {
        long id = -1;
        String[] projection = {MediaStore.Audio.Media._ID};
        String selection = MediaStore.Audio.Media.DATA + "=?";
        String[] selectionArgs = {path};

        try (android.database.Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                id = cursor.getLong(idColumn);
            }
        }

        return id;
    }

    public void showEditMusicDialog(MusicDto musicDto) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_edit_music);

        EditText titleEdit = dialog.findViewById(R.id.editTitle);
        EditText artistEdit = dialog.findViewById(R.id.editArtist);
        EditText albumEdit = dialog.findViewById(R.id.editAlbum);
        EditText trackEdit = dialog.findViewById(R.id.editTrack);
        EditText genreEdit = dialog.findViewById(R.id.editGenre);
        Button saveBtn = dialog.findViewById(R.id.btnSave);

        titleEdit.setText(musicDto.title);
        artistEdit.setText(musicDto.artistName);
        albumEdit.setText(musicDto.albumName);
        trackEdit.setText(String.valueOf(musicDto.track));
        genreEdit.setText(musicDto.genreName);

        saveBtn.setOnClickListener(v -> {
            String newTitle = titleEdit.getText().toString().trim();
            String newArtist = artistEdit.getText().toString().trim();
            String newAlbum = albumEdit.getText().toString().trim();
            String newTrack = trackEdit.getText().toString().trim();
            String newGenre = genreEdit.getText().toString().trim();

            executor.execute(() -> {
                Music music = musicDao.getById(musicDto.id);
                Album album = albumDao.getById(musicDto.albumId);
                String artistAlbum = artistDao.getById(album.artistId).name;

                if (music != null) {
                    boolean hasChanges = !musicDto.title.equals(newTitle)
                            || !musicDto.albumName.equals(newAlbum)
                            || !musicDto.artistName.equals(newArtist)
                            || !musicDto.genreName.equals(newGenre);

                    if (hasChanges) {
                        artistService.addAllArtist(newArtist, statisticService);
                        long genreId = genreService.add(newGenre, statisticService);
                        long albumId = albumService.add(newAlbum, album.coverPath, album.year, artistAlbum, artistService, statisticService);
                        musicService.modify(music, newTitle, Integer.parseInt(newTrack), albumId, genreId, newArtist, artistService, statisticService);
                    }

                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Musique mise à jour", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }
            });
        });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }

    public long getLastMusicDeletedId() {
        return lastMusicDeletedId;
    }
}
