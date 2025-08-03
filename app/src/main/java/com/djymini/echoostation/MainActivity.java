package com.djymini.echoostation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.djymini.echoostation.fragments.EqualizerFragment;
import com.djymini.echoostation.fragments.HomeFragment;
import com.djymini.echoostation.fragments.LibraryFragment;
import com.djymini.echoostation.fragments.SearchFragment;
import com.djymini.echoostation.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private RelativeLayout authorizationLayout;
    private ConstraintLayout appLayout;
    private Button confirmButton, quitButton;
    private BottomNavigationView bottomNavMenu;

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

        authorizationLayout = (RelativeLayout)findViewById(R.id.authorization_layout);
        appLayout = (ConstraintLayout)findViewById(R.id.app_layout);
        confirmButton = (Button)findViewById(R.id.confirm_button);
        quitButton = (Button)findViewById(R.id.quit_button);
        bottomNavMenu = (BottomNavigationView)findViewById(R.id.bottom_nav_menu);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkAndRequestMusicPermission();
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        bottomNavMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.library) {
                loadFragment(new LibraryFragment());
                return true;
            } else if (itemId == R.id.search) {
                loadFragment(new SearchFragment());
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

        checkPermission();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavMenu.setSelectedItemId(R.id.home); // Sélection visuelle
        }
    }

    private void readMusicOfDevice(){
        Log.d("Test EchooStation", "Bienvenue sur Echoostation");
        String[] projection = new String[] {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.GENRE,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.ALBUM_ARTIST
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

        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            Log.d("Test EchooStation", path);
        }
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


}