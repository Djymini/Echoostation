package com.djymini.echoostation;

import static com.djymini.echoostation.utilities.Constants.FRAGEMENTS;
import static com.djymini.echoostation.utilities.Constants.FRAGEMENTS_NAMES;
import static com.djymini.echoostation.utilities.Constants.REQUEST_CODE_DELETE;
import static com.djymini.echoostation.utilities.Constants.SCAN_INFOS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.djymini.echoostation.fragments.MusicPlayerFragment;
import com.djymini.echoostation.helpers.AppInitializer;
import com.djymini.echoostation.helpers.FragmentInitalizer;
import com.djymini.echoostation.services.DatabaseService;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.djymini.echoostation.viewModels.loaderMediaViewModel.LoaderMediaViewModel;
import com.djymini.echoostation.viewModels.loaderMediaViewModel.LoaderMusicViewModelFactory;
import com.djymini.echoostation.viewModels.musicScannerViewModel.MusicScannerViewModelFactory;
import com.djymini.echoostation.viewModels.permissionViewModel.PermissionViewModelFactory;
import com.djymini.echoostation.viewModels.musicScannerViewModel.MusicScannerViewModel;
import com.djymini.echoostation.viewModels.permissionViewModel.PermissionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Button grantPermissionButton, exitAppButton;
    private BottomNavigationView bottomNavMenu;
    private Toolbar toolbar;

    private DatabaseService dbService;
    public AppInitializer appInitializer;

    private MusicScannerViewModel musicScannerViewModel;

    private PermissionViewModel permissionViewModel;
    public LoaderMediaViewModel loaderMediaViewModel;

    private boolean hasPermission = false;
    private long lastMusicDeletedId = -1;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private MusicPlayerViewModel viewModel;
    private FrameLayout miniPlayerContainer;

    private Fragment activeFragment;
    private FragmentManager fragmentManager;
    private FragmentInitalizer fragmentInitalizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (hasPermission) {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            } else {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        setUpDatabaseandInitializeApp();
        setUpLoader();

        setupViews();

        setUpPermissionButton();
        setUpExitButton();
        setToolbar();
        setupBottomNavMenu();

        if (savedInstanceState == null) {
            initFragments();
            bottomNavMenu.setSelectedItemId(R.id.home);
        }

        setPermissionViewModel();
        setMusicScanViewModel();
        setMiniPlayer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DELETE) {
            if (resultCode == Activity.RESULT_OK) {
                executor.execute(() -> {
                    lastMusicDeletedId = appInitializer.getMusicDialogManager().getLastMusicDeletedId();
                    dbService.getMusicDao().deleteById(lastMusicDeletedId);
                });
            } else {
                lastMusicDeletedId = -1;
            }
        }
    }

    private void setUpDatabaseandInitializeApp(){
        dbService = new DatabaseService(this);
        appInitializer = new AppInitializer(dbService, requestPermissionLauncher, this::scanDeviceMusic, executor, this, this);
    }

    private void setUpLoader(){
        LoaderMusicViewModelFactory loaderMediaViewModelFactory = new LoaderMusicViewModelFactory(dbService.getMusicDao(), dbService.getArtistDao(), dbService.getAlbumDao(), dbService.getGenreDao());
        loaderMediaViewModel = new ViewModelProvider(this, loaderMediaViewModelFactory).get(LoaderMediaViewModel.class);
    }

    private void scanDeviceMusic(){
        musicScannerViewModel.startScan();
    }

    private void initFragments() {
        fragmentInitalizer = new FragmentInitalizer();
        fragmentManager = getSupportFragmentManager();

        for (int i = 0; i < FRAGEMENTS.length; i++) {
            if(i == 0){
                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, fragmentInitalizer.getFragment(i), FRAGEMENTS_NAMES[i].toUpperCase())
                        .commit();
            }else {
                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, fragmentInitalizer.getFragment(i), FRAGEMENTS_NAMES[i].toUpperCase())
                        .hide(fragmentInitalizer.getFragment(i))
                        .commit();
            }
        }
        modifyTitle(FRAGEMENTS_NAMES[0]);
        activeFragment = fragmentInitalizer.getFragment(0);
    }


    private void showFragment(Fragment fragment) {
        if (fragment == activeFragment) return;

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();

        modifyTitle(fragmentInitalizer.getTitleFragment(fragment));
        activeFragment = fragment;
    }

    private void setupViews(){
        grantPermissionButton = findViewById(R.id.confirm_button);
        exitAppButton = findViewById(R.id.quit_button);
        bottomNavMenu = findViewById(R.id.bottom_nav_menu);
    }

    private void setUpPermissionButton(){
        grantPermissionButton.setOnClickListener(v -> permissionViewModel.checkAndRequestPermission());
    }

    private void setUpExitButton(){
        exitAppButton.setOnClickListener(v -> {
            finish();
            finishAffinity();
        });
    }

    private void setupBottomNavMenu(){
        bottomNavMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragmentForLoad = fragmentInitalizer.getFragmentWithLayout(itemId);
            if(fragmentForLoad != null){
                showFragment(fragmentForLoad);
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
        showFragment(fragmentInitalizer.getLibraryFragment());
    }

    public void modifyTitle(String newText){
        toolbar.setTitle(newText);
    }

    public void setPermissionViewModel(){
        PermissionViewModelFactory factory = new PermissionViewModelFactory(appInitializer.getPermissionManager());
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
            hasPermission = granted;
            ViewCompat.requestApplyInsets(findViewById(R.id.main));
        });
        permissionViewModel.checkPermission(this);
    }

    public void setMusicScanViewModel(){
        MusicScannerViewModelFactory musicScannerFactory = new MusicScannerViewModelFactory(appInitializer.getMusicScanner());
        musicScannerViewModel = new ViewModelProvider(this, musicScannerFactory).get(MusicScannerViewModel.class);
        musicScannerViewModel.getIsScanning().observe(this, scanning -> {
            findViewById(R.id.scan_progress_container).setVisibility(scanning ? View.VISIBLE : View.GONE);
        });
        musicScannerViewModel.getScanProgress().observe(this, progress -> {
            TextView progressText = findViewById(R.id.scan_progress_text);
            progressText.setText(SCAN_INFOS + progress + "/" + musicScannerViewModel.getTotalFiles().getValue());
        });
    }

    public void setMiniPlayer(){
        miniPlayerContainer = findViewById(R.id.mini_player_container);
        viewModel = new ViewModelProvider(this).get(MusicPlayerViewModel.class);
    }

    public void updateMiniPlayerVisibility(Fragment fragment) {
        if (miniPlayerContainer == null) return;

        if (fragment instanceof MusicPlayerFragment) {
            toolbar.setVisibility(View.GONE);
            bottomNavMenu.setVisibility(View.GONE);
            miniPlayerContainer.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            bottomNavMenu.setVisibility(View.VISIBLE);
            viewModel.getCurrentItem().getValue();
            miniPlayerContainer.setVisibility(viewModel.getCurrentItem().getValue() != null ? View.VISIBLE : View.GONE);
        }
    }
}