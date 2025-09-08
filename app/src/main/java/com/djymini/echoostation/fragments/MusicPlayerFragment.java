package com.djymini.echoostation.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;

public class MusicPlayerFragment extends Fragment {

    private MusicPlayerViewModel viewModel;

    private ImageView coverArtView, repeatButton, shuffleButton;
    private ImageView playPauseButton, nextButton, prevButton;
    private TextView titleView, artistView, albumView, currentTimeView, durationView;
    private SeekBar seekBar;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBarRunnable;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSeekBar();
        handler.post(updateSeekBarRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateSeekBarRunnable);
    }

    private void bindViews(View view) {
        coverArtView = view.findViewById(R.id.cover_art);
        playPauseButton = view.findViewById(R.id.play_pause_button);
        nextButton = view.findViewById(R.id.next_button);
        prevButton = view.findViewById(R.id.prev_button);
        titleView = view.findViewById(R.id.music_title);
        artistView = view.findViewById(R.id.music_artist);
        albumView = view.findViewById(R.id.music_album);
        seekBar = view.findViewById(R.id.music_seekbar);
        currentTimeView = view.findViewById(R.id.current_time);
        durationView = view.findViewById(R.id.music_duration);
        repeatButton = view.findViewById(R.id.repeat_button);
        shuffleButton = view.findViewById(R.id.shuffle_button);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);

        viewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying ->
                playPauseButton.setImageResource(isPlaying ? R.drawable.round_pause_24 : R.drawable.round_play_arrow_24)
        );

        viewModel.getCurrentItem().observe(getViewLifecycleOwner(), item -> {
            updateUI(item);

        });

        viewModel.getRepeatMode().observe(getViewLifecycleOwner(), mode -> {
            switch (mode) {
                case Player.REPEAT_MODE_OFF:
                    repeatButton.setImageResource(R.drawable.round_repeat_24);
                    repeatButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.disableText));
                    break;
                case Player.REPEAT_MODE_ONE:
                    repeatButton.setImageResource(R.drawable.round_repeat_one_24);
                    repeatButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorText));
                    break;
                case Player.REPEAT_MODE_ALL:
                    repeatButton.setImageResource(R.drawable.round_repeat_24);
                    repeatButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorText));
                    break;
            }
        });

        viewModel.getShuffleEnabled().observe(getViewLifecycleOwner(), enabled -> {
            shuffleButton.setColorFilter(ContextCompat.getColor(requireContext(),
                    enabled ? R.color.colorText : R.color.disableText));
        });

    }

    private void setupClickListeners() {
        playPauseButton.setOnClickListener(v -> viewModel.playPause(requireContext()));
        nextButton.setOnClickListener(v -> viewModel.next(requireContext()));
        prevButton.setOnClickListener(v -> viewModel.prev(requireContext()));
        repeatButton.setOnClickListener(v -> viewModel.toggleRepeatMode(requireContext()));
        shuffleButton.setOnClickListener(v -> viewModel.toggleShuffle(requireContext()));
    }

    private void notifyActivityMiniPlayer() {
        if (getActivity() instanceof MainActivity) {
            //((MainActivity) getActivity()).navigator.updateMiniPlayerVisibility(this);
        }
    }

    private void updateUI(MediaItem item) {
        if (item != null) {
            titleView.setText(item.mediaMetadata.title != null ? item.mediaMetadata.title : getString(R.string.unknow));
            artistView.setText(item.mediaMetadata.artist != null ? item.mediaMetadata.artist : getString(R.string.unknow));
            albumView.setText(item.mediaMetadata.albumTitle != null ? item.mediaMetadata.albumTitle : getString(R.string.unknow));
            durationView.setText(TimeUtilities.formatDuration(viewModel.getDuration()));

            Uri artworkUri = item.mediaMetadata.artworkUri;
            Glide.with(this)
                    .load(artworkUri)
                    .placeholder(R.drawable.echoostation_placeholder_music_3x)
                    .error(R.drawable.echoostation_placeholder_music_3x)
                    .into(coverArtView);
        } else {
            titleView.setText(getString(R.string.unknow));
            artistView.setText(getString(R.string.unknow));
            albumView.setText(getString(R.string.unknow));
            coverArtView.setImageResource(R.drawable.echoostation_placeholder_music_3x);
        }
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && viewModel != null) {
                    long duration = viewModel.getDuration();
                    long newPosition = (duration * progress) / 1000L;
                    viewModel.seekTo(requireContext(), newPosition);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                viewModel.pause(requireContext());
                currentTimeView.setText(TimeUtilities.formatDuration(seekBar.getProgress() * viewModel.getDuration() / 1000L));
            }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.play(requireContext());
            }
        });

        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewModel != null) {
                    long position = viewModel.getCurrentPosition();
                    long duration = viewModel.getDuration();
                    if (duration > 0) {
                        int progress = (int) ((position * 1000L) / duration);
                        seekBar.setProgress(progress);
                        currentTimeView.setText(TimeUtilities.formatDuration(position));
                    }
                }
                handler.postDelayed(this, 100);
            }
        };
    }
}