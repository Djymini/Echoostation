package com.djymini.echoostation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> requestPermissionLauncher;

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

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Permission accordée", Toast.LENGTH_SHORT).show();
                        readMusicOfDevice();
                    } else {
                        Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
                    }
                });

        checkAndRequestMusicPermission();
    }

    private void readMusicOfDevice(){
        Log.d("Test EchooStation", "Bienvenue sur Echoostation");
        String[] projection = new String[] {
                MediaStore.Audio.Media.RELATIVE_PATH,
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
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            Log.d("Test EchooStation", title);
        }
    }

    private void checkAndRequestMusicPermission() {
        String permission;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_AUDIO;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            readMusicOfDevice();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle("Accès requis")
                    .setMessage("L'application a besoin d'accéder à vos musiques pour fonctionner correctement.")
                    .setPositiveButton("Autoriser", (dialog, which) -> {
                        requestPermissionLauncher.launch(permission);
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

}