package com.djymini.echoostation;

import static com.djymini.echoostation.utilities.Constants.REQUEST_CODE_DELETE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
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
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Genre;
import com.djymini.echoostation.fragments.EqualizerFragment;
import com.djymini.echoostation.fragments.HomeFragment;
import com.djymini.echoostation.fragments.LibraryFragment;
import com.djymini.echoostation.fragments.SettingsFragment;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;
import com.djymini.echoostation.ui.MusicDialogManager;
import com.djymini.echoostation.helpers.MusicScanner;
import com.djymini.echoostation.helpers.PermissionManager;
import com.djymini.echoostation.viewModels.loaderMediaViewModel.LoaderMediaViewModel;
import com.djymini.echoostation.viewModels.loaderMediaViewModel.LoaderMusicViewModelFactory;
import com.djymini.echoostation.viewModels.musicScannerViewModel.MusicScannerViewModelFactory;
import com.djymini.echoostation.viewModels.permissionViewModel.PermissionViewModelFactory;
import com.djymini.echoostation.viewModels.musicScannerViewModel.MusicScannerViewModel;
import com.djymini.echoostation.viewModels.permissionViewModel.PermissionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> requestPermissionLauncher;
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

    private MusicScanner musicScanner;
    private PermissionManager permissionManager;
    private MusicScannerViewModel musicScannerViewModel;
    public MusicDialogManager musicDialogManager;

    private PermissionViewModel permissionViewModel;
    private LoaderMediaViewModel loaderMediaViewModel;

    public LiveData<List<MusicDto>> currentMusicList;
    public LiveData<List<Artist>> currentArtistList;
    public LiveData<List<Album>> currentAlbumList;
    public LiveData<List<Genre>> currentGenreList;


    private long lastMusicDeletedId = -1;

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupDbAndDao();
        setupServices();
        setupViews();
        setupHelpers();

        setAuthorizationButtons();
        setToolbar();
        setupBottomNavMenu();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavMenu.setSelectedItemId(R.id.home);
        }

        setPermissionViewModel();
        setMusicScanViewModel();
        setLoaderMediaViewModel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DELETE) {
            if (resultCode == Activity.RESULT_OK) {
                executor.execute(() -> {
                    lastMusicDeletedId = musicDialogManager.getLastMusicDeletedId();
                    musicDao.deleteById(lastMusicDeletedId);
                });
            } else {
                lastMusicDeletedId = -1;
            }
        }
    }

    private void scanDeviceMusic(){
        musicScannerViewModel.startScan();
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
        confirmButton = findViewById(R.id.confirm_button);
        quitButton = findViewById(R.id.quit_button);
        bottomNavMenu = findViewById(R.id.bottom_nav_menu);
    }

    private void setupHelpers(){
        musicScanner = new MusicScanner(this, musicDao, albumService, artistService, genreService, musicService, statisticService, executor);
        permissionManager = new PermissionManager(this, requestPermissionLauncher, this::scanDeviceMusic, null);
        musicDialogManager = new MusicDialogManager(this, musicDao, albumDao, artistDao, musicService, albumService, artistService, genreService, statisticService, executor);

        permissionManager.registerPermissionLauncher();
    }

    private void setAuthorizationButtons(){
        confirmButton.setOnClickListener(v -> permissionViewModel.checkAndRequestPermission());

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

    public void setPermissionViewModel(){
        PermissionViewModelFactory factory = new PermissionViewModelFactory(permissionManager);
        permissionViewModel = new ViewModelProvider(this, factory).get(PermissionViewModel.class);
        permissionViewModel.getIsPermissionGranted().observe(this, granted -> {
            View appLayout = findViewById(R.id.app_layout);
            View permissionLayout = findViewById(R.id.authorization_layout);

            if (granted) {
                permissionLayout.setVisibility(View.GONE);
                appLayout.setVisibility(View.VISIBLE);
            } else {
                permissionLayout.setVisibility(View.VISIBLE);
                appLayout.setVisibility(View.GONE);
            }
            updateViewCompat(granted);
        });
        permissionViewModel.checkPermission(this);
    }

    public void setMusicScanViewModel(){
        MusicScannerViewModelFactory musicScannerFactory = new MusicScannerViewModelFactory(musicScanner);
        musicScannerViewModel = new ViewModelProvider(this, musicScannerFactory).get(MusicScannerViewModel.class);
        musicScannerViewModel.getIsScanning().observe(this, scanning -> {
            findViewById(R.id.scan_progress_container).setVisibility(scanning ? View.VISIBLE : View.GONE);
        });
        musicScannerViewModel.getScanProgress().observe(this, progress -> {
            TextView progressText = findViewById(R.id.scan_progress_text);
            progressText.setText("Scan en cours... " + progress + "/" + musicScannerViewModel.getTotalFiles().getValue());
        });
    }

    public void setLoaderMediaViewModel(){
        LoaderMusicViewModelFactory loaderMediaViewModelFactory = new LoaderMusicViewModelFactory(musicDao, artistDao, albumDao, genreDao);
        loaderMediaViewModel = new ViewModelProvider(this, loaderMediaViewModelFactory).get(LoaderMediaViewModel.class);
        currentMusicList = loaderMediaViewModel.loadMusics();
        currentArtistList = loaderMediaViewModel.loadArtists();
        currentAlbumList = loaderMediaViewModel.loadAlbums();
        currentGenreList = loaderMediaViewModel.loadGenres();
    }

    private void updateViewCompat(boolean granted){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (granted)
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            else
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}