package com.djymini.echoostation.helpers;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.dtos.MusicDto;

import java.util.ArrayList;
import java.util.List;

public class MediaItemHelper {
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

            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(music.path)
                    .setMediaId(String.valueOf(music.id))
                    .setMediaMetadata(metadata)
                    .build();

            items.add(mediaItem);
        }
        return items;
    }

    public static void shuffleMusic(List<MusicDto> musicDtoList, List<MediaItem> playlist, MainActivity main, Context context){
        int musicPosition = (int) ( Math.random() * musicDtoList.size()-1 );
        main.playerViewModel.playPlaylist(context, playlist, musicPosition);
        main.playerViewModel.toggleShuffle(context);
    }
}
