package com.djymini.echoostation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MoodDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.dataBase.DatabaseClient;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.fragments.EqualizerFragment;
import com.djymini.echoostation.fragments.HomeFragment;
import com.djymini.echoostation.fragments.LibraryFragment;
import com.djymini.echoostation.fragments.SettingsFragment;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;
import com.djymini.echoostation.utilities.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private RelativeLayout authorizationLayout;
    private ConstraintLayout appLayout;
    private Button confirmButton, quitButton;
    private BottomNavigationView bottomNavMenu;

    private AlbumDao albumDao;
    private ArtistDao artistDao;
    private GenreDao genreDao;
    private MoodDao moodDao;
    private MusicDao musicDao;
    private PlaylistDao playlistDao;
    private StatisticDao statisticDao;

    private AlbumService albumService;
    private ArtistService artistService;
    private GenreService genreService;
    private MusicService musicService;
    private StatisticService statisticService;

    private long lastMusicDeletedId = -1;
    private static final int REQUEST_CODE_DELETE = 1001;

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupDbAndDao();
        setupServices();
        setupViews();

        setAuthorizationButton();
        setBottomNavMenu();

        applyRequestPermission();
        checkPermission();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavMenu.setSelectedItemId(R.id.home);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DELETE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("FILE_DELETE", "Fichier supprimé avec confirmation utilisateur");
                executor.execute(() -> {
                    Music musicForDelete = musicDao.getById(lastMusicDeletedId);
                    musicDao.delete(musicForDelete);
                });
            } else {
                Log.d("FILE_DELETE", "Suppression annulée");
            }
        }
    }


    private void readMusicOfDevice(){
        String[] projection = new String[] {
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
        String[] selectionArgs = new String[] { "0" };
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        executor.execute(() -> {
            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
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
                        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                        Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
                        String coverAlbum = albumArtUri.toString();

                        long idGenre = genreService.add(genre, statisticService, this);
                        long idAlbum = albumService.add(album, coverAlbum, year, albumArtist, artistService, statisticService, this);
                        musicService.add(path, title, duration, track, artist, idAlbum, idGenre, artistService, statisticService, this);
                    }
                } finally {
                    cursor.close();
                }
            }
        });
    }

    private void checkAndRequestMusicPermission() {
        String permission = adaptPermissionWithVersion();

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            readMusicOfDevice();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle("Accès requis")
                    .setMessage("L'application a besoin d'accéder à vos musiques pour fonctionner correctement.")
                    .setPositiveButton("Autoriser", (dialog, which) -> requestPermissionLauncher.launch(permission))
                    .setNegativeButton("Annuler", null)
                    .show();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void checkPermission(){
        String permission = adaptPermissionWithVersion();

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            authorizationLayout.setVisibility(View.GONE);
            appLayout.setVisibility(View.VISIBLE);
            readMusicOfDevice();
        } else {
            authorizationLayout.setVisibility(View.VISIBLE);
            appLayout.setVisibility(View.GONE);
        }
    }

    private String adaptPermissionWithVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_AUDIO;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void setupDbAndDao(){
        EchooStationDatabase db = DatabaseClient.getInstance(this).getDatabase();
        albumDao = db.albumDao();
        artistDao = db.artistDao();
        genreDao = db.genreDao();
        moodDao = db.moodDao();
        musicDao = db.musicDao();
        playlistDao = db.playlistDao();
        statisticDao = db.statisticDao();
    }

    private void setupServices(){
        albumService = new AlbumService(albumDao, statisticDao);
        artistService = new ArtistService(artistDao, statisticDao);
        genreService = new GenreService(genreDao, statisticDao);
        musicService = new MusicService(musicDao, statisticDao);
        statisticService = new StatisticService(statisticDao);
    }

    private void setupViews(){
        authorizationLayout = findViewById(R.id.authorization_layout);
        appLayout = findViewById(R.id.app_layout);
        confirmButton = findViewById(R.id.confirm_button);
        quitButton = findViewById(R.id.quit_button);
        bottomNavMenu = findViewById(R.id.bottom_nav_menu);
    }

    private void setAuthorizationButton(){
        confirmButton.setOnClickListener(v -> checkAndRequestMusicPermission());

        quitButton.setOnClickListener(v -> {
            finish();
            System.exit(0);
        });
    }

    private void setBottomNavMenu(){
        bottomNavMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.library) {
                loadFragment(new LibraryFragment());
                return true;
            } else if (itemId == R.id.equalizer) {
                loadFragment(new EqualizerFragment());
                return true;
            } else if (itemId == R.id.settings) {
                loadFragment(new SettingsFragment());
                return true;
            }
            return false;
        });
    }

    private void applyRequestPermission(){
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                authorizationLayout.setVisibility(View.GONE);
                appLayout.setVisibility(View.VISIBLE);
                readMusicOfDevice();
            } else {
                authorizationLayout.setVisibility(View.VISIBLE);
                appLayout.setVisibility(View.GONE);
            }
        });
    }

    private String getGenreFromAudioId(long audioId) {
        String genre = Constants.UNKNOWN_GENRE;
        Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", (int) audioId);
        Cursor genreCursor = getContentResolver().query(uri, new String[]{MediaStore.Audio.Genres.NAME}, null, null, null);
        if (genreCursor != null) {
            if (genreCursor.moveToFirst()) {
                genre = genreCursor.getString(genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME));
            }
            genreCursor.close();
        }
        return genre;
    }

    public void openLibraryTab(int tabIndex) {
        bottomNavMenu.setSelectedItemId(R.id.library);
        loadFragment(LibraryFragment.newInstance(tabIndex));
    }

    public void showBottomDialog(MusicDto musicDto) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        TextView title = dialog.findViewById(R.id.title_dialog);
        title.setText(musicDto.title);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = dialog.findViewById(R.id.layoutShorts);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layout_delete);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Upload a Video is clicked",Toast.LENGTH_SHORT).show();

            }
        });

        shortsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Create a short is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                long mediaStoreId = getMediaStoreIdFromPath(MainActivity.this, musicDto.path);
                if (mediaStoreId != -1) {
                    Uri uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            mediaStoreId
                    );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        List<Uri> uris = Collections.singletonList(uri);
                        PendingIntent deleteRequest = MediaStore.createDeleteRequest(
                                getContentResolver(),
                                uris
                        );
                        try {
                            startIntentSenderForResult(
                                    deleteRequest.getIntentSender(),
                                    REQUEST_CODE_DELETE,
                                    null,
                                    0, 0, 0
                            );
                            lastMusicDeletedId = musicDto.id;
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    } else {
                        int deleted = getContentResolver().delete(uri, null, null);
                        if (deleted > 0) {
                            Log.d("FILE_DELETE", "Supprimé avec succès");
                            executor.execute(() -> {
                                Music musicForDelete = musicDao.getById(musicDto.id);
                                musicDao.delete(musicForDelete);
                            });
                        } else {
                            Log.e("FILE_DELETE", "Échec de suppression");
                        }
                    }
                }
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    public long getMediaStoreIdFromPath(Context context, String path) {
        long id = -1;

        String[] projection = {MediaStore.Audio.Media._ID};
        String selection = MediaStore.Audio.Media.DATA + "=?";
        String[] selectionArgs = new String[]{path};

        try (Cursor cursor = context.getContentResolver().query(
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


}