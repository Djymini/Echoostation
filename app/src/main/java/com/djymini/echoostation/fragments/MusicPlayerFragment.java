package com.djymini.echoostation.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;

import java.util.ArrayList;

public class MusicPlayerFragment extends Fragment {
    private MusicPlayerViewModel viewModel;

    private ImageView cover, playPause, next, prev;
    private TextView title, artist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);


        cover = view.findViewById(R.id.cover_art);
        playPause = view.findViewById(R.id.play_pause_button);
        next = view.findViewById(R.id.next_button);
        prev = view.findViewById(R.id.prev_button);
        title = view.findViewById(R.id.music_title);
        artist = view.findViewById(R.id.music_artist);

        viewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);

        viewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying ->
                playPause.setImageResource(isPlaying ? R.drawable.round_pause_24 : R.drawable.round_play_arrow_24)
        );

        viewModel.getCurrentItem().observe(getViewLifecycleOwner(), item -> {

            if (item != null) {
                Log.d("echoostation : MusicPlayerFragment", "true");
                Log.d("echoostation : MusicPlayerFragment", item.mediaMetadata.title.toString());
                Log.d("echoostation : MusicPlayerFragment", item.mediaMetadata.artist.toString());
                Log.d("echoostation : MusicPlayerFragment", item.mediaMetadata.artworkUri.toString());

                title.setText(item.mediaMetadata.title != null ? item.mediaMetadata.title.toString() : "Inconnu");
                artist.setText(item.mediaMetadata.artist != null ? item.mediaMetadata.artist.toString() : "Inconnu");

                Uri albumArt = item.mediaMetadata.artworkUri;
                Glide.with(this)
                        .load(albumArt)
                        .placeholder(R.drawable.echoostation_placeholder_music_3x)
                        .error(R.drawable.echoostation_placeholder_music_3x)
                        .into(cover);
            } else {
                Log.d("echoostation : MusicPlayerFragment", "false");
                title.setText("Inconnu");
                artist.setText("Inconnu");
                cover.setImageResource(R.drawable.echoostation_placeholder_music_3x);
            }
        });

        playPause.setOnClickListener(v -> viewModel.playPause(requireContext()));
        next.setOnClickListener(v -> viewModel.next(requireContext()));
        prev.setOnClickListener(v -> viewModel.prev(requireContext()));

        return view;
    }
}