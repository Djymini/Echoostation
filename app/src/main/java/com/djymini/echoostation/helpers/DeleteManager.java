package com.djymini.echoostation.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.ui.MusicDialogManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class DeleteManager {
    MainActivity main;
    FragmentActivity activity;
    ActivityResultLauncher<IntentSenderRequest> deleteMultipleLauncher;
    List<MusicDto> musicsPendingDeletion;

    public DeleteManager(MainActivity main, FragmentActivity activity) {
        this.main = main;
        this.activity = activity;
    }

    public void deleteSelectedMusics(Set<MusicDto> selected, ExecutorService executor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            List<Uri> uris = new ArrayList<>();
            for (MusicDto music : selected) {
                long mediaStoreId = MusicDialogManager.getMediaStoreIdFromPath(activity, music.path);
                if (mediaStoreId != -1) {
                    uris.add(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaStoreId));
                }
            }

            if (!uris.isEmpty()) {
                musicsPendingDeletion = new ArrayList<>(selected);
                IntentSender sender = MediaStore.createDeleteRequest(
                        activity.getContentResolver(), uris
                ).getIntentSender();
                deleteMultipleLauncher.launch(new IntentSenderRequest.Builder(sender).build());
            }
        } else {
            executor.execute(() -> {
                for (MusicDto music : selected) {
                    long mediaStoreId = MusicDialogManager.getMediaStoreIdFromPath(activity, music.path);
                    if (mediaStoreId != -1) {
                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaStoreId);
                        int deleted = activity.getContentResolver().delete(uri, null, null);
                        if (deleted > 0) {
                            main.dbService.getMusicDao().deleteById(music.id);
                        }
                    }
                }
            });
        }
    }

    public void deleteSelectedAlbums(Set<AlbumDto> selected, ExecutorService executor) {
        executor.execute(() -> {
            for (AlbumDto album : selected) {
                Set<MusicDto> musicDeleteList = new HashSet<>(main.dbService.getMusicDao().getMusicDetailByAlbum(album.id));
                deleteSelectedMusics(musicDeleteList, executor);
                main.dbService.getAlbumDao().deleteById(album.id);
            }
        });
    }

    public void setupDeleteLauncher(ExecutorService executor, Fragment fragment) {
        deleteMultipleLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && musicsPendingDeletion != null) {
                        List<MusicDto> toDelete = new ArrayList<>(musicsPendingDeletion);
                        executor.execute(() -> {
                            for (MusicDto music : toDelete) {
                                main.dbService.getMusicDao().deleteById(music.id);
                            }
                        });
                    }
                    musicsPendingDeletion = null;
                }
        );
    }

    public <E> void confirmAndDeleteSelectedMedia(Set<E> selected, Context context, Fragment fragment, ExecutorService executor) {
        if (selected.isEmpty()) return;
        E first = selected.iterator().next();

        if(first instanceof MusicDto){
            new AlertDialog.Builder(context)
                    .setTitle(fragment.getString(R.string.delete_music))
                    .setMessage(fragment.getString(R.string.delete_request1) + selected.size() + fragment.getString(R.string.delete_request2))
                    .setPositiveButton(fragment.getString(R.string.yes), (dialog, which) -> deleteSelectedMusics((Set<MusicDto>) selected, executor))
                    .setNegativeButton(fragment.getString(R.string.no), null)
                    .show();
        } else if(first instanceof AlbumDto){
            new AlertDialog.Builder(context)
                    .setTitle(fragment.getString(R.string.delete_music))
                    .setMessage(fragment.getString(R.string.delete_request1) + selected.size() + fragment.getString(R.string.delete_request2))
                    .setPositiveButton(fragment.getString(R.string.yes), (dialog, which) -> deleteSelectedAlbums((Set<AlbumDto>) selected, executor))
                    .setNegativeButton(fragment.getString(R.string.no), null)
                    .show();
        }
    }
}
