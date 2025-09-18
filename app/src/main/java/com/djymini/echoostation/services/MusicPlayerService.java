package com.djymini.echoostation.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaStyleNotificationHelper;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@UnstableApi
public class MusicPlayerService extends MediaLibraryService {

    private static final String CHANNEL_ID = "music_channel";
    private static final int NOTIFICATION_ID = 1;

    private ExoPlayer exoPlayer;
    private MediaLibrarySession mediaLibrarySession;

    @Override
    public void onCreate() {
        super.onCreate();

        // ⚡ Création du canal de notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lecture musique",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // ⚡ Initialisation du player
        exoPlayer = new ExoPlayer.Builder(this).build();

        // ⚡ Création de la MediaLibrarySession
        mediaLibrarySession = new MediaLibrarySession.Builder(this, exoPlayer, new LibraryCallback())
                .build();

        // ⚡ Crée une notification initiale
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public MediaLibrarySession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaLibrarySession;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaLibrarySession != null) {
            mediaLibrarySession.release();
            mediaLibrarySession = null;
        }
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    /**
     * 🔔 Crée une notification MediaStyle (contrôles de musique).
     */
    private Notification createNotification() {
        // Intent vers ton activité principale
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Lecture en cours")
                .setContentText("Écoute ta musique 🎵")
                .setSmallIcon(R.drawable.ic_round_play_circle_outline_24)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setStyle(new MediaStyleNotificationHelper.MediaStyle(mediaLibrarySession))
                .setOngoing(true) // empêche l’utilisateur de fermer sans stopper le service
                .build();
    }

    /**
     * ⚙️ Callback de la MediaLibrary (ex: parcourir bibliothèque)
     */
    private static class LibraryCallback implements MediaLibrarySession.Callback {
        // Implémente si tu veux gérer la recherche, playlists, etc.
    }
}
