package com.djymini.echoostation.helpers;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

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

    public static void shuffleAlbum(List<AlbumDto> albumDtoList, MainActivity main, Context context, ExecutorService executor){
        List<MediaItem> playlist = new ArrayList<>();

        AlbumDto shuffleAlbum = albumDtoList.get((int) ( Math.random() * albumDtoList.size()-1 ));

        executor.execute(() -> {
            List<MusicDto> musicDtoList = main.dbService.getMusicDao().getMusicDetailByAlbum(shuffleAlbum.id);
            playlist.addAll(loadPlaylist(musicDtoList));
        });

        main.playerViewModel.playPlaylist(context, playlist, 0);
        main.playerViewModel.toggleShuffle(context);
    }

    public static void shuffleArtist(List<ArtistDto> artistDtoList, MainActivity main, Context context, ExecutorService executor){
        List<MediaItem> playlist = new ArrayList<>();

        ArtistDto shuffleArtist = artistDtoList.get((int) ( Math.random() * artistDtoList.size()-1 ));

        executor.execute(() -> {
            List<MusicDto> musicDtoList = main.dbService.getMusicDao().getMusicDetailByArtist(shuffleArtist.name);
            playlist.addAll(loadPlaylist(musicDtoList));
        });

        main.playerViewModel.playPlaylist(context, playlist, 0);
        main.playerViewModel.toggleShuffle(context);
    }

    public static void shuffleGenre(List<Genre> genreList, MainActivity main, Context context, ExecutorService executor){
        List<MediaItem> playlist = new ArrayList<>();

        Genre shuffleGenre = genreList.get((int) ( Math.random() * genreList.size()-1 ));

        executor.execute(() -> {
            List<MusicDto> musicDtoList = main.dbService.getMusicDao().getMusicDetailByGenre(shuffleGenre.id);
            playlist.addAll(loadPlaylist(musicDtoList));
        });

        main.playerViewModel.playPlaylist(context, playlist, 0);
        main.playerViewModel.toggleShuffle(context);
    }
}
