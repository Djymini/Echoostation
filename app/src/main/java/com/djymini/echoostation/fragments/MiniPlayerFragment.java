package com.djymini.echoostation.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.R;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;

public class MiniPlayerFragment extends Fragment {

    private static final float PLAY_PAUSE_SCALE = 1.2f;
    private static final int ANIMATION_DURATION = 150;

    private MusicPlayerViewModel viewModel;

    private LinearLayout container;
    private ImageView cover;
    private ImageButton playPause, next, prev;
    private TextView title, artist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup containerParent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_player, containerParent, false);

        container = view.findViewById(R.id.mini_player_container);
        cover = view.findViewById(R.id.cover_art);
        playPause = view.findViewById(R.id.play_pause_button);
        next = view.findViewById(R.id.next_button);
        prev = view.findViewById(R.id.prev_button);
        title = view.findViewById(R.id.music_title);
        artist = view.findViewById(R.id.music_artist);

        viewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);

        viewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            playPause.setImageResource(isPlaying ? R.drawable.round_pause_24 : R.drawable.round_play_arrow_24);
            animatePlayPause(isPlaying);
        });

        viewModel.getCurrentItem().observe(getViewLifecycleOwner(), this::updateUI);

        playPause.setOnClickListener(v -> viewModel.playPause(requireContext()));
        next.setOnClickListener(v -> viewModel.next(requireContext()));
        prev.setOnClickListener(v -> viewModel.prev(requireContext()));

        container.setOnClickListener(v -> openFullPlayer());

        return view;
    }

    private void updateUI(@Nullable MediaItem item) {
        if (item != null && item.mediaMetadata != null) {
            title.setText(item.mediaMetadata.title != null ? item.mediaMetadata.title : getString(R.string.unknow));
            artist.setText(item.mediaMetadata.artist != null ? item.mediaMetadata.artist : getString(R.string.unknow));

            Uri artwork = item.mediaMetadata.artworkUri;
            Glide.with(this)
                    .load(artwork)
                    .placeholder(R.drawable.echoostation_placeholder_music_3x)
                    .error(R.drawable.echoostation_placeholder_music_3x)
                    .into(cover);
        } else {
            title.setText(getString(R.string.unknow));
            artist.setText(getString(R.string.unknow));
            cover.setImageResource(R.drawable.echoostation_placeholder_music_3x);
        }
    }

    private void animatePlayPause(boolean isPlaying) {
        playPause.animate()
                .scaleX(PLAY_PAUSE_SCALE)
                .scaleY(PLAY_PAUSE_SCALE)
                .setDuration(ANIMATION_DURATION)
                .withEndAction(() -> playPause.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(ANIMATION_DURATION)
                        .start())
                .start();
    }

    private void openFullPlayer() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new MusicPlayerFragment())
                .addToBackStack(null)
                .commit();
    }
}