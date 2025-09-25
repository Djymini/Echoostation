package com.djymini.echoostation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.djymini.echoostation.helpers.AppInitializer;
import com.djymini.echoostation.helpers.DeleteManager;
import com.djymini.echoostation.viewModels.playlistAndMixViewModel.PlaylistAndMixViewModel;
import com.djymini.echoostation.helpers.MusicScanner;
import com.djymini.echoostation.helpers.Navigator;
import com.djymini.echoostation.helpers.PermissionManager;
import com.djymini.echoostation.services.DatabaseService;
import com.djymini.echoostation.ui.MusicDialogManager;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.djymini.echoostation.viewModels.playlistAndMixViewModel.PlaylistAndMixViewModelFactory;
import com.djymini.echoostation.viewModels.loaderMediaViewModel.LoaderMediaViewModel;
import com.djymini.echoostation.viewModels.loaderMediaViewModel.LoaderMusicViewModelFactory;
import com.djymini.echoostation.viewModels.musicScannerViewModel.MusicScannerViewModelFactory;
import com.djymini.echoostation.viewModels.permissionViewModel.PermissionViewModelFactory;
import com.djymini.echoostation.viewModels.musicScannerViewModel.MusicScannerViewModel;
import com.djymini.echoostation.viewModels.permissionViewModel.PermissionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public DatabaseService dbService;
    public AppInitializer appInitializer;
    public DeleteManager deleteManager;

    private MusicScannerViewModel musicScannerViewModel;
    private PermissionViewModel permissionViewModel;
    public LoaderMediaViewModel loaderMediaViewModel;
    public MusicPlayerViewModel playerViewModel;

    public Navigator navigator;
    private boolean hasPermission = false;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public PlaylistAndMixViewModel playlistAndMixViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        applyInsets();
        setupDatabaseAndInitializeApp();
        setupLoader();

        Toolbar toolbar = findViewById(R.id.toolbar);
        BottomNavigationView bottomNavMenu = findViewById(R.id.bottom_nav_menu);

        playerViewModel = new ViewModelProvider(this).get(MusicPlayerViewModel.class);

        navigator = new Navigator(
                getSupportFragmentManager(),
                toolbar,
                bottomNavMenu,
                playerViewModel,
                this.findViewById(R.id.main),
                this,
                this,
                this,
                this
        );

        setupButtons();
        setSupportActionBar(toolbar);
        navigator.setupBottomNav();

        if (savedInstanceState == null) {
            navigator.initFragments();
            navigator.initMusicPlayer();
            bottomNavMenu.setSelectedItemId(R.id.home);
        }

        setupPermissionViewModel();
        setupMusicScanViewModel();
        deleteManager = new DeleteManager(this, this);
        navigator.setupTrueMusicPlayer(findViewById(R.id.player_bottom_sheet));
    }

    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupDatabaseAndInitializeApp() {
        dbService = new DatabaseService(this);
        MusicScanner scanner = new MusicScanner(this, dbService.getMusicDao(), dbService.getAlbumService(), dbService.getArtistService(), dbService.getGenreService(), dbService.getMusicService(), dbService.getStatisticService(), executor);
        PermissionManager permission = new PermissionManager(this, null, this::scanDeviceMusic, null);
        permission.registerPermissionLauncher();
        MusicDialogManager dialogManager = new MusicDialogManager(this, dbService.getMusicDao(), dbService.getAlbumDao(), dbService.getArtistDao(), dbService.getMusicService(), dbService.getAlbumService(), dbService.getArtistService(), dbService.getGenreService(), dbService.getStatisticService(), executor);
        appInitializer = new AppInitializer(scanner, permission, dialogManager);
    }

    private void setupLoader() {
        LoaderMusicViewModelFactory factory = new LoaderMusicViewModelFactory(
                dbService.getMusicDao(),
                dbService.getArtistDao(),
                dbService.getAlbumDao(),
                dbService.getGenreDao(),
                dbService.getPlaylistDao()
        );
        loaderMediaViewModel = new ViewModelProvider(this, factory).get(LoaderMediaViewModel.class);

        PlaylistAndMixViewModelFactory factory2 = new PlaylistAndMixViewModelFactory(
                dbService.getMusicDao(),
                dbService.getMusicService(),
                executor
        );
        playlistAndMixViewModel = new ViewModelProvider(this, factory2).get(PlaylistAndMixViewModel.class);
    }

    private void setupButtons() {
        Button grantPermissionButton = findViewById(R.id.confirm_button);
        Button exitAppButton = findViewById(R.id.quit_button);

        grantPermissionButton.setOnClickListener(v -> permissionViewModel.checkAndRequestPermission());
        exitAppButton.setOnClickListener(v -> finishAffinity());
    }

    private void setupPermissionViewModel() {
        PermissionViewModelFactory factory = new PermissionViewModelFactory(appInitializer.getPermissionManager());
        permissionViewModel = new ViewModelProvider(this, factory).get(PermissionViewModel.class);

        permissionViewModel.getIsPermissionGranted().observe(this, granted -> {
            findViewById(R.id.app_layout).setVisibility(granted ? View.VISIBLE : View.GONE);
            findViewById(R.id.authorization_layout).setVisibility(granted ? View.GONE : View.VISIBLE);

            hasPermission = granted;
            ViewCompat.requestApplyInsets(findViewById(R.id.main));

            if (granted) {
                scanDeviceMusic();
            }
        });
        permissionViewModel.checkPermission(this);
    }

    private void setupMusicScanViewModel() {
        MusicScannerViewModelFactory factory =
                new MusicScannerViewModelFactory(appInitializer.getMusicScanner());
        musicScannerViewModel = new ViewModelProvider(this, factory).get(MusicScannerViewModel.class);

        musicScannerViewModel.getIsScanning().observe(this,
                scanning -> findViewById(R.id.scan_progress_container)
                        .setVisibility(scanning ? View.VISIBLE : View.GONE));

        musicScannerViewModel.getScanProgress().observe(this, progress -> {
            TextView progressText = findViewById(R.id.scan_progress_text);
            String text = getString(R.string.scanning_indicator) + " " + progress
                    + "/" + musicScannerViewModel.getTotalFiles().getValue();
            progressText.setText(text);
        });
    }

    private void scanDeviceMusic() {
        musicScannerViewModel.startScan();
    }
    public void openLibraryTab(int tabIndex) {
        navigator.openLibraryTab(tabIndex);
    }
}