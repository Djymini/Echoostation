package com.djymini.echoostation.viewModels;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.djymini.echoostation.services.MusicPlayerService;
import com.djymini.echoostation.utilities.Constants;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicPlayerViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<MediaItem> currentItem = new MutableLiveData<>();

    private MediaController controller;
    private ListenableFuture<MediaController> controllerFuture;
    // ✅ Playlist complète (source de vérité)
    private final List<MediaItem> fullPlaylist = new ArrayList<>();

    public void setPlaylist(List<MediaItem> items) {
        fullPlaylist.clear();
        fullPlaylist.addAll(items);
    }

    public List<MediaItem> getPlaylist() {
        return new ArrayList<>(fullPlaylist);
    }

    private final MutableLiveData<Integer> repeatMode = new MutableLiveData<>(Player.REPEAT_MODE_OFF);
    private final MutableLiveData<Boolean> shuffleEnabled = new MutableLiveData<>(false);
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    public boolean canIncremente = true;
    private final MutableLiveData<Boolean> musicChange = new MutableLiveData<>(false);


    private final MutableLiveData<Long> elapsedTime = new MutableLiveData<>(0L);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateElapsedRunnable = new Runnable() {
        @Override
        public void run() {
            if (stopwatch.isRunning()) {
                elapsedTime.postValue(stopwatch.elapsed(TimeUnit.MILLISECONDS));
                handler.postDelayed(this, 100); // update toutes les 100ms
            }
        }
    };

    public LiveData<Long> getElapsedTime() {
        return elapsedTime;
    }


    public LiveData<Integer> getRepeatMode() { return repeatMode; }
    public LiveData<Boolean> getShuffleEnabled() { return shuffleEnabled; }

    public LiveData<Boolean> getIsPlaying() { return isPlaying; }
    public LiveData<Boolean> getMusicChange() { return musicChange; }
    public LiveData<MediaItem> getCurrentItem() { return currentItem; }

    public MediaController getController() {
        return controller;
    }


    @OptIn(markerClass = UnstableApi.class)
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
                        public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
                            boolean playing = player.isPlaying();
                            isPlaying.postValue(playing);
                            currentItem.postValue(player.getCurrentMediaItem());
                            repeatMode.postValue(player.getRepeatMode());
                            shuffleEnabled.postValue(player.getShuffleModeEnabled());

                            if (playing) {
                                if (!stopwatch.isRunning()) {
                                    stopwatch.start();
                                    handler.post(updateElapsedRunnable); // lancer la MAJ périodique
                                    Log.d("Stopwatch", "⏱️ Démarrage du chrono");
                                }
                            } else {
                                if (stopwatch.isRunning()) {
                                    stopwatch.stop();
                                    Log.d("Stopwatch", "⏸️ Pause du chrono, elapsed = "
                                            + stopwatch.elapsed(TimeUnit.MILLISECONDS));
                                }
                            }
                        }



                        @Override
                        public void onMediaItemTransition(@NonNull MediaItem mediaItem, int reason) {
                            currentItem.postValue(mediaItem);
                            if(elapsedTime.getValue() > 0)
                                musicChange.postValue(true);

                            // reset chrono
                            stopwatch.reset();
                            canIncremente = true;
                            if (controller != null && controller.isPlaying()) {
                                stopwatch.start();
                                handler.post(updateElapsedRunnable);
                            }

                            Log.d("Stopwatch", "🔄 Nouveau morceau : " + mediaItem.mediaMetadata.title);
                        }

                        @Override
                        public void onPositionDiscontinuity(
                                @NonNull Player.PositionInfo oldPosition,
                                @NonNull Player.PositionInfo newPosition,
                                int reason
                        ) {
                            if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
                                // soit une transition normale (next) soit repeat one
                                if (controller != null) {
                                    MediaItem item = controller.getCurrentMediaItem();
                                    if (item != null) {
                                        currentItem.postValue(item);
                                        musicChange.postValue(true);

                                        stopwatch.reset();
                                        canIncremente = true;
                                        if (controller.isPlaying()) {
                                            stopwatch.start();
                                            handler.post(updateElapsedRunnable);
                                        }

                                        Log.d("Stopwatch", "🔁 Redémarrage (repeat mode ou auto next): "
                                                + item.mediaMetadata.title);
                                    }
                                }
                            }
                        }


                    });

                    if (onReady != null) onReady.run();
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e("MusicPlayerViewModel", "Erreur lors de la connexion au MediaController", t);
                }
            }, MoreExecutors.directExecutor());
        } else {
            Futures.addCallback(controllerFuture, new com.google.common.util.concurrent.FutureCallback<MediaController>() {
                @Override
                public void onSuccess(MediaController ctrl) {
                    controller = ctrl; // <--- important si reconnect
                    if (onReady != null) onReady.run();
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e("MusicPlayerViewModel", "Erreur lors de la reconnexion au MediaController", t);
                }
            }, MoreExecutors.directExecutor());
        }
    }


    @OptIn(markerClass = UnstableApi.class)
    public void playPlaylist(Context context, MediaItem itemToPlay) {
        ensureConnected(context, () -> {
            int index = fullPlaylist.indexOf(itemToPlay);
            if (index < 0) index = 0; // sécurité

            controller.setMediaItems(fullPlaylist, index, C.TIME_UNSET);
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

    public void pause(Context context) {
        ensureConnected(context, () -> {
            controller.pause();
        });
    }

    public void play(Context context) {
        ensureConnected(context, () -> {
            controller.play();
        });
    }

    public void next(Context context) {
        ensureConnected(context, () -> {
            controller.seekToNext();
            stopwatch.reset();
            canIncremente = true;
        });
    }

    public void prev(Context context) {
        ensureConnected(context, () -> {
            controller.seekToPrevious();
            musicChange.postValue(true);
            stopwatch.reset();
            canIncremente = true;
        });
    }

    public void seekTo(Context context, long position) {
        ensureConnected(context, () -> controller.seekTo(position));
    }

    public int getPlaylistSize(){return (controller != null) ? controller.getMediaItemCount() : 0;}

    public int getIndexCurrentItem(){return (controller != null) ? controller.getCurrentMediaItemIndex() : 0;}

    public long getCurrentPosition() {
        return (controller != null) ? controller.getCurrentPosition() : 0;
    }

    public long getDuration() {
        return (controller != null) ? controller.getDuration() : 0;
    }

    public void toggleRepeatMode(Context context) {
        ensureConnected(context, () -> {
            int mode = controller.getRepeatMode();
            int newMode;
            if (mode == Player.REPEAT_MODE_OFF) newMode = Player.REPEAT_MODE_ALL;
            else if (mode == Player.REPEAT_MODE_ALL) newMode = Player.REPEAT_MODE_ONE;
            else newMode = Player.REPEAT_MODE_OFF;

            controller.setRepeatMode(newMode);
            repeatMode.postValue(newMode);
        });
    }

    public void toggleShuffle(Context context) {
        ensureConnected(context, () -> {
            boolean enabled = !controller.getShuffleModeEnabled();
            controller.setShuffleModeEnabled(enabled);
            shuffleEnabled.postValue(enabled);
        });
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
