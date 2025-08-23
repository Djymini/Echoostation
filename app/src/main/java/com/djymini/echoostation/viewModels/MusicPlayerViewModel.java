package com.djymini.echoostation.viewModels;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.djymini.echoostation.services.MusicPlayerService;
import com.djymini.echoostation.utilities.Constants;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.List;

public class MusicPlayerViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<MediaItem> currentItem = new MutableLiveData<>();

    private MediaController controller;
    private ListenableFuture<MediaController> controllerFuture;

    public LiveData<Boolean> getIsPlaying() { return isPlaying; }
    public LiveData<MediaItem> getCurrentItem() { return currentItem; }

    private void ensureConnected(Context context, Runnable onReady) {
        if (controller != null) {
            if (onReady != null) onReady.run();
            return;
        }

        if (controllerFuture == null) {
            SessionToken token = new SessionToken(context, new ComponentName(context, MusicPlayerService.class));
            controllerFuture = new MediaController.Builder(context, token).buildAsync();

            Futures.addCallback(controllerFuture, new com.google.common.util.concurrent.FutureCallback<MediaController>() {
                @Override
                public void onSuccess(MediaController ctrl) {
                    controller = ctrl;

                    controller.addListener(new Player.Listener() {
                        @Override
                        public void onEvents(@NonNull @NonNull Player player, @NonNull @NonNull Player.Events events) {
                            isPlaying.postValue(player.isPlaying());
                            currentItem.postValue(player.getCurrentMediaItem());
                        }
                    });

                    if (onReady != null) onReady.run();
                }

                @Override
                public void onFailure(@NonNull @NonNull Throwable t) {
                    Log.e("MusicPlayerViewModel", "Erreur lors de la connexion au MediaController", t);
                }
            }, MoreExecutors.directExecutor());
        } else {
            Futures.addCallback(controllerFuture, new com.google.common.util.concurrent.FutureCallback<MediaController>() {
                @Override
                public void onSuccess(MediaController ctrl) {
                    if (onReady != null) onReady.run();
                }

                @Override
                public void onFailure(@NonNull @NonNull Throwable t) {
                    Log.e("MusicPlayerViewModel", "Erreur lors de la reconnexion au MediaController", t);
                }
            }, MoreExecutors.directExecutor());
        }
    }

    public void playPlaylist(Context context, List<MediaItem> items, int startIndex) {
        ensureConnected(context, () -> {
            controller.setMediaItems(items, startIndex, Constants.TIME_UNSET);
            controller.prepare();
            controller.play();
        });
    }

    public void playPause(Context context) {
        ensureConnected(context, () -> {
            if (controller.isPlaying()) controller.pause();
            else controller.play();
        });
    }

    public void next(Context context) {
        ensureConnected(context, () -> controller.seekToNext());
    }

    public void prev(Context context) {
        ensureConnected(context, () -> controller.seekToPrevious());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (controller != null) {
            controller.release();
            controller = null;
        }
        if (controllerFuture != null) {
            controllerFuture.cancel(true);
            controllerFuture = null;
        }
    }
}
