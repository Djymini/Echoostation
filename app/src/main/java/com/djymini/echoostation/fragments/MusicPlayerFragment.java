package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.MusicDto;

import java.util.ArrayList;

public class MusicPlayerFragment extends Fragment {
    private PlayerView playerView;
    private ExoPlayer exoPlayer;

    private ArrayList<MusicDto> playlist;
    private int startIndex;

    public static MusicPlayerFragment newInstance(ArrayList<MusicDto> playlist, int startIndex) {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("playlist", playlist);
        args.putInt("startIndex", startIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        playerView = view.findViewById(R.id.player_view);

        if (getArguments() != null) {
            playlist = getArguments().getParcelableArrayList("playlist");
            startIndex = getArguments().getInt("startIndex", 0);
        }

        initPlayer();
        return view;
    }

    private void initPlayer() {
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(exoPlayer);

        // Construire la playlist ExoPlayer
        for (MusicDto music : playlist) {
            MediaItem item = MediaItem.fromUri(music.path); // attention : MusicDto doit contenir un path Uri valide
            exoPlayer.addMediaItem(item);
        }

        exoPlayer.prepare();
        exoPlayer.seekTo(startIndex, 0);
        exoPlayer.play();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}