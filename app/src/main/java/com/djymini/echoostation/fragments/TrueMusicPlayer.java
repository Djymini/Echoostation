package com.djymini.echoostation.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.R;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class TrueMusicPlayer {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private ImageView playerCover;
    private TextView playerTitle, playerArtist, playerAlbum, currentTimeView, durationView;
    private LinearLayout mainContent, fullContent;
    private ImageButton repeatButton, shuffleButon, playPauseButton, nextButton, prevButton;

    private MusicPlayerViewModel viewModel;
    private SeekBar seekBar;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBarRunnable;
    private Context context;

    private final float MINI_SIZE = 64f;   // taille mini player
    private final float FULL_SIZE = 200f;

    public TrueMusicPlayer(View view, LifecycleOwner lifecycleOwner, ViewModelStoreOwner storeOwner, Context context) {
        this.context = context;

        playerCover = view.findViewById(R.id.player_cover);
        playPauseButton = view.findViewById(R.id.btn_play);
        nextButton = view.findViewById(R.id.btn_next);
        prevButton = view.findViewById(R.id.btn_prev);
        playerTitle = view.findViewById(R.id.player_title);
        playerArtist = view.findViewById(R.id.player_artist);
        playerAlbum = view.findViewById(R.id.player_album);
        seekBar = view.findViewById(R.id.player_seekbar);
        currentTimeView = view.findViewById(R.id.player_current_time);
        durationView = view.findViewById(R.id.player_duration);
        repeatButton = view.findViewById(R.id.btn_repeat);
        shuffleButon = view.findViewById(R.id.btn_shuffle);
        mainContent = view.findViewById(R.id.player_bottom_sheet);
        fullContent = view.findViewById(R.id.full_content);

        setupViewModel(lifecycleOwner, storeOwner);
        setupClickListeners();
        setupSeekBar();
    }

    public BottomSheetBehavior<View> getBottomSheetBehavior() {
        return bottomSheetBehavior;
    }

    public void setBottomSheetBehavior(BottomSheetBehavior<View> bottomSheetBehavior) {
        this.bottomSheetBehavior = bottomSheetBehavior;
    }

    public ImageView getPlayerCover() {
        return playerCover;
    }

    public TextView getPlayerTitle() {
        return playerTitle;
    }

    public TextView getPlayerArtist() {
        return playerArtist;
    }

    public LinearLayout getFullContent() {
        return fullContent;
    }

    public ImageButton getShuffleButon() {
        return shuffleButon;
    }

    public TextView getPlayerAlbum() {
        return playerAlbum;
    }

    public LinearLayout getMainContent() {
        return mainContent;
    }

    public void setMainContent(LinearLayout mainContent) {
        this.mainContent = mainContent;
    }

    public void hideFullContent(){
        repeatButton.setVisibility(View.GONE);
        shuffleButon.setVisibility(View.GONE);
        playerAlbum.setVisibility(View.GONE);
        mainContent.setOrientation(LinearLayout.HORIZONTAL);
        playerTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        playerArtist.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
    }

    public void showFullContent(){
        repeatButton.setVisibility(View.VISIBLE);
        shuffleButon.setVisibility(View.VISIBLE);
        playerAlbum.setVisibility(View.VISIBLE);
        mainContent.setOrientation(LinearLayout.VERTICAL);
        playerTitle.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        playerArtist.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    public int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    private void setupViewModel(LifecycleOwner lifecycleOwner, ViewModelStoreOwner storeOwner) {
        viewModel = new ViewModelProvider(storeOwner).get(MusicPlayerViewModel.class);

        viewModel.getIsPlaying().observe(lifecycleOwner, isPlaying ->
                playPauseButton.setImageResource(isPlaying ? R.drawable.round_pause_24 : R.drawable.round_play_arrow_24)
        );

        viewModel.getCurrentItem().observe(lifecycleOwner, this::updateUI);

        viewModel.getRepeatMode().observe(lifecycleOwner, mode -> {
            switch (mode) {
                case Player.REPEAT_MODE_OFF:
                    repeatButton.setImageResource(R.drawable.round_repeat_24);
                    repeatButton.setColorFilter(ContextCompat.getColor(context, R.color.disableText));
                    break;
                case Player.REPEAT_MODE_ONE:
                    repeatButton.setImageResource(R.drawable.round_repeat_one_24);
                    repeatButton.setColorFilter(ContextCompat.getColor(context, R.color.colorText));
                    break;
                case Player.REPEAT_MODE_ALL:
                    repeatButton.setImageResource(R.drawable.round_repeat_24);
                    repeatButton.setColorFilter(ContextCompat.getColor(context, R.color.colorText));
                    break;
            }
        });

        viewModel.getShuffleEnabled().observe(lifecycleOwner, enabled ->
                shuffleButon.setColorFilter(ContextCompat.getColor(context,
                        enabled ? R.color.colorText : R.color.disableText))
        );
    }

    private void updateUI(MediaItem item) {
        if (item != null) {
            playerTitle.setText(item.mediaMetadata.title != null ? item.mediaMetadata.title : "Inconnus");
            playerArtist.setText(item.mediaMetadata.artist != null ? item.mediaMetadata.artist : "Inconnus");
            playerAlbum.setText(item.mediaMetadata.albumTitle != null ? item.mediaMetadata.albumTitle : "Inconnus");
            durationView.setText(TimeUtilities.formatDuration(viewModel.getDuration()));

            Uri artworkUri = item.mediaMetadata.artworkUri;
            Glide.with(context)
                    .load(artworkUri)
                    .placeholder(R.drawable.echoostation_placeholder_music_3x)
                    .error(R.drawable.echoostation_placeholder_music_3x)
                    .into(playerCover);
        } else {
            playerTitle.setText("Inconnus");
            playerArtist.setText("Inconnus");
            playerAlbum.setText("Inconnus");
            playerCover.setImageResource(R.drawable.echoostation_placeholder_music_3x);
        }
    }

    private void setupClickListeners() {
        playPauseButton.setOnClickListener(v -> viewModel.playPause(context));
        nextButton.setOnClickListener(v -> viewModel.next(context));
        prevButton.setOnClickListener(v -> viewModel.prev(context));
        repeatButton.setOnClickListener(v -> viewModel.toggleRepeatMode(context));
        shuffleButon.setOnClickListener(v -> viewModel.toggleShuffle(context));
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && viewModel != null) {
                    long duration = viewModel.getDuration();
                    long newPosition = (duration * progress) / 1000L;
                    viewModel.seekTo(context, newPosition);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                viewModel.pause(context);
                currentTimeView.setText(TimeUtilities.formatDuration(seekBar.getProgress() * viewModel.getDuration() / 1000L));
            }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.play(context);
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
