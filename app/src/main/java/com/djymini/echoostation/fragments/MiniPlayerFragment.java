package com.djymini.echoostation.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

        // Observer l'état de lecture
        viewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            playPause.setImageResource(isPlaying ? R.drawable.round_pause_24 : R.drawable.round_play_arrow_24);
            animatePlayPause(isPlaying);
        });

        // Observer la musique actuelle
        viewModel.getCurrentItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                title.setText(item.mediaMetadata.title != null ? item.mediaMetadata.title : "Inconnu");
                artist.setText(item.mediaMetadata.artist != null ? item.mediaMetadata.artist : "Inconnu");

                Uri artwork = item.mediaMetadata.artworkUri;
                Glide.with(this)
                        .load(artwork)
                        .placeholder(R.drawable.echoostation_placeholder_music_3x)
                        .error(R.drawable.echoostation_placeholder_music_3x)
                        .into(cover);
            }
        });

        // Gestion des clics
        playPause.setOnClickListener(v -> viewModel.playPause(requireContext()));
        next.setOnClickListener(v -> viewModel.next(requireContext()));
        prev.setOnClickListener(v -> viewModel.prev(requireContext()));

        // Click sur le mini player pour ouvrir le player full-screen
        container.setOnClickListener(v -> {
            openFullPlayer();
        });

        return view;
    }

    private void animatePlayPause(boolean isPlaying) {
        playPause.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150).withEndAction(() -> {
            playPause.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
        }).start();
    }

    private void openFullPlayer() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new MusicPlayerFragment())
                .addToBackStack(null)
                .commit();
    }
}