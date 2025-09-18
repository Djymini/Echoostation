package com.djymini.echoostation.utilities;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.dtos.MusicDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerUtilities {
    public static void playMusicList(List<MediaItem> playlist, MainActivity mainActivity, Context context){
        mainActivity.playerViewModel.playPlaylist(context, playlist, 0);
    }

    public static void shuffleMusicList(List<MediaItem> playlist, List<MusicDto> musicList, MainActivity mainActivity, Context context){
        int musicPosition = (int) ( Math.random() * musicList.size()-1 );
        mainActivity.playerViewModel.playPlaylist(context, playlist, musicPosition);
        mainActivity.playerViewModel.toggleShuffle(context);
    }

    public static List<MediaItem> loadPlaylist(List<MusicDto> list) {
        List<MediaItem> items = new ArrayList<>();
        for (MusicDto music : list) {
            MediaMetadata metadata = new MediaMetadata.Builder()
                    .setTitle(music.title)
                    .setArtist(music.artistName)
                    .setAlbumTitle(music.albumName)
                    .setArtworkUri(music.getCover())
                    .setDurationMs(music.duration)
                    .build();

            Uri uri = Uri.fromFile(new File(music.path));
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(uri)
                    .setMediaId(String.valueOf(music.id))
                    .setMediaMetadata(metadata)
                    .build();

            items.add(mediaItem);
        }
        return items;
    }
}
