package com.djymini.echoostation.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;

public class MusicPlayerFragment extends Fragment {

    private MusicPlayerViewModel viewModel;

    private ImageView coverArtView;
    private ImageView playPauseButton, nextButton, prevButton;
    private TextView titleView, artistView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);

        bindViews(view);
        setupViewModel();
        setupClickListeners();
        notifyActivityMiniPlayer();

        return view;
    }

    private void bindViews(View view) {
        coverArtView = view.findViewById(R.id.cover_art);
        playPauseButton = view.findViewById(R.id.play_pause_button);
        nextButton = view.findViewById(R.id.next_button);
        prevButton = view.findViewById(R.id.prev_button);
        titleView = view.findViewById(R.id.music_title);
        artistView = view.findViewById(R.id.music_artist);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);

        viewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying ->
                playPauseButton.setImageResource(isPlaying ? R.drawable.round_pause_24 : R.drawable.round_play_arrow_24)
        );

        viewModel.getCurrentItem().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void setupClickListeners() {
        playPauseButton.setOnClickListener(v -> viewModel.playPause(requireContext()));
        nextButton.setOnClickListener(v -> viewModel.next(requireContext()));
        prevButton.setOnClickListener(v -> viewModel.prev(requireContext()));
    }

    private void notifyActivityMiniPlayer() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigator.updateMiniPlayerVisibility(this);
        }
    }

    private void updateUI(MediaItem item) {
        if (item != null) {
            titleView.setText(item.mediaMetadata.title != null ? item.mediaMetadata.title : getString(R.string.unknow));
            artistView.setText(item.mediaMetadata.artist != null ? item.mediaMetadata.artist : getString(R.string.unknow));

            Uri artworkUri = item.mediaMetadata.artworkUri;
            Glide.with(this)
                    .load(artworkUri)
                    .placeholder(R.drawable.echoostation_placeholder_music_3x)
                    .error(R.drawable.echoostation_placeholder_music_3x)
                    .into(coverArtView);
        } else {
            titleView.setText(getString(R.string.unknow));
            artistView.setText(getString(R.string.unknow));
            coverArtView.setImageResource(R.drawable.echoostation_placeholder_music_3x);
        }
    }
}