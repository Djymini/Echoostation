package com.djymini.echoostation.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class MusicPlayerService extends MediaLibraryService {
    private ExoPlayer exoPlayer;
    private MediaLibrarySession mediaLibrarySession;

    @Override
    public void onCreate() {
        super.onCreate();

        exoPlayer = new ExoPlayer.Builder(this).build();

        mediaLibrarySession = new MediaLibrarySession.Builder(this, exoPlayer, new LibraryCallback())
                .build();
    }

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaLibrarySession;
    }

    @Override
    public void onDestroy() {
        mediaLibrarySession.release();
        exoPlayer.release();
        super.onDestroy();
    }

    private static class LibraryCallback implements MediaLibrarySession.Callback {
        @NonNull
        @Override
        public ListenableFuture<LibraryResult<MediaItem>> onGetItem(
                @NonNull MediaLibrarySession session,
                @NonNull MediaSession.ControllerInfo controller,
                @NonNull String mediaId
        ) {
            MediaItem item = MediaItem.fromUri(mediaId);
            return Futures.immediateFuture(LibraryResult.ofItem(item, /* position */ null));
        }
    }
}
