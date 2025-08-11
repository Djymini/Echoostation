package com.djymini.echoostation;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

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
import com.djymini.echoostation.entities.Album;
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
import com.djymini.echoostation.utilities.MusicDialogManager;
import com.djymini.echoostation.utilities.MusicScanner;
import com.djymini.echoostation.utilities.PermissionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private RelativeLayout authorizationLayout;
    private ConstraintLayout appLayout;
    private Button confirmButton, quitButton;
    private BottomNavigationView bottomNavMenu;
    private Toolbar toolbar;

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

    private PermissionManager permissionManager;
    private MusicScanner musicScanner;
    public MusicDialogManager musicDialogManager;

    private long lastMusicDeletedId = -1;
    private static final int REQUEST_CODE_DELETE = 1001;

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupDbAndDao();
        setupServices();
        setupViews();

        musicScanner = new MusicScanner(this, musicDao, albumService, artistService, genreService, musicService, statisticService, executor);
        permissionManager = new PermissionManager(this, requestPermissionLauncher, this::scanDeviceMusic, null);
        musicDialogManager = new MusicDialogManager(this, musicDao, albumDao, artistDao, musicService, albumService, artistService, genreService, statisticService, executor);

        setAuthorizationButtons();
        setToolbar();
        setupBottomNavMenu();

        permissionManager.registerPermissionLauncher(authorizationLayout, appLayout);
        permissionManager.checkPermission(authorizationLayout, appLayout, this);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavMenu.setSelectedItemId(R.id.home);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_DELETE)
            return;

        if (resultCode == Activity.RESULT_OK) {
            Log.d(Constants.TAG_FILE_DELETE, Constants.MSG_DELETE_WITH_AUTHORIZATION);

            executor.execute(() -> {
                Music musicForDelete = musicDao.getById(lastMusicDeletedId);
                musicDao.delete(musicForDelete);
            });
        } else {
            Log.d(Constants.TAG_FILE_DELETE, Constants.MSG_DELETE_CANCELED);
        }
    }

    private void scanDeviceMusic(){
        musicScanner.scanDeviceMusic();
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
        statisticService = new StatisticService(statisticDao);
        albumService = new AlbumService(albumDao, statisticDao, statisticService);
        artistService = new ArtistService(artistDao, statisticDao, statisticService, this);
        genreService = new GenreService(genreDao, statisticDao, statisticService, this);
        musicService = new MusicService(musicDao, statisticDao, statisticService);
    }

    private void setupViews(){
        authorizationLayout = findViewById(R.id.authorization_layout);
        appLayout = findViewById(R.id.app_layout);
        confirmButton = findViewById(R.id.confirm_button);
        quitButton = findViewById(R.id.quit_button);
        bottomNavMenu = findViewById(R.id.bottom_nav_menu);
    }

    private void setAuthorizationButtons(){
        confirmButton.setOnClickListener(v -> permissionManager.checkAndRequestPermission());

        quitButton.setOnClickListener(v -> {
            finish();
            System.exit(0);
        });
    }

    private void setupBottomNavMenu(){
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

    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void openLibraryTab(int tabIndex) {
        bottomNavMenu.setSelectedItemId(R.id.library);
        loadFragment(LibraryFragment.newInstance(tabIndex));
    }

    public void modifyTitle(String newText){
        toolbar.setTitle(newText);
    }
}