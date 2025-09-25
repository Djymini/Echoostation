package com.djymini.echoostation.fragments.playlistMusicFragment;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.MusicAdapter;
import com.djymini.echoostation.customView.ParallelogramImageView;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.utilities.MusicPlayerUtilities;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.utilities.UiUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class PlaylistMusicFragment extends Fragment {
    public MainActivity main;
    public ExecutorService executor;
    public List<MusicDto> musicList = new ArrayList<>();
    public List<MediaItem> playlist = new ArrayList<>();

    public TextView playlistNameView, playlistNumberTrack, playlistDurationTotal;
    public Button playButton, shuffleButton, reloadButton;
    public RecyclerView recyclerView;
    public MusicAdapter adapter;

    public List<ParallelogramImageView> parallelogramImageViewList = new ArrayList<>();

    public String playlistName;
    public int playlistDefaultImage;
    public Map<String, PlaylistParams> mapPlaylist;

    public static class PlaylistParams {
        int imageRes;
        Supplier<List<MusicDto>> supplier;

        PlaylistParams(int imageRes, Supplier<List<MusicDto>> supplier) {
            this.imageRes = imageRes;
            this.supplier = supplier;
        }
    }

    public void bindView(View view){
        playlistNameView = view.findViewById(R.id.playlist_name);
        playlistNumberTrack = view.findViewById(R.id.number_tracks);
        playlistDurationTotal = view.findViewById(R.id.duration_total);
        playButton = view.findViewById(R.id.play_button);
        shuffleButton = view.findViewById(R.id.shuffle_button);
        reloadButton = view.findViewById(R.id.reload_button);
        recyclerView = view.findViewById(R.id.recycler_view_song_playlist);

        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage1));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage2));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage3));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage4));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage5));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage6));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage7));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage8));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage9));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage10));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage11));
        parallelogramImageViewList.add(view.findViewById(R.id.parallelogramImage12));
    }

    public void setupInfoPlaylist(){
        playlistNameView.setText(playlistName);
        String totalMusic = musicList.size() > 1 ? musicList.size() + " morceaux" : musicList.size() + " morceau";
        String duration = "Durée : " + TimeUtilities.durationTotal(musicList);
        playlistNumberTrack.setText(totalMusic);
        playlistDurationTotal.setText(duration);

        for (int i = 0; i < musicList.size() && i < 12; i++) {
            if(musicList.get(i) != null) {
                parallelogramImageViewList.get(i).setVisibility(View.VISIBLE);
                UiUtilities.displayImageWithGlide(musicList.get(i).getCover(), R.drawable.echoostation_placeholder_album_3x, parallelogramImageViewList.get(i), requireContext());
            }else{

            }

        }
    }

    public void setupButton(){
        playButton.setOnClickListener(v -> {
            MediaItemHelper.playPlaylist(adapter.getCurrentList().get(0), main, requireContext());
        });

        shuffleButton.setOnClickListener(v -> {
            int musicPosition = (int) ( Math.random() * musicList.size()-1 );
            MediaItemHelper.shufflePlaylist(adapter.getCurrentList().get(musicPosition), main, requireContext());
        });
    }

    public void sortAndDisplayMusics() {
        if (musicList == null) return;

        executor.execute(() -> {
            List<MediaItem> globalPlaylist = MediaItemHelper.loadPlaylist(musicList);
            main.playerViewModel.setPlaylist(globalPlaylist);

            requireActivity().runOnUiThread(() -> adapter.submitList(musicList));
        });
    }

    public void backButtonManager(int fragmentBackId){
        Toolbar toolbar = main.navigator.getToolbar(); // ou getActivity().findViewById(...)
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            boolean nightMode = (getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

            Drawable upArrow = ContextCompat.getDrawable(requireContext(),
                    nightMode ?  R.drawable.round_arrow_back_24_normal : R.drawable.round_arrow_back_24_night);

            if (upArrow != null) {
                activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }

        toolbar.setNavigationOnClickListener(v -> main.navigator.goBackToLibrary(fragmentBackId));

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        main.navigator.goBackToLibrary(fragmentBackId);
                    }
                }
        );
    }

    public List<MusicDto> getMusicList(String playlistName) {
        PlaylistParams params = mapPlaylist.get(playlistName);
        if (params != null) {
            playlistDefaultImage = params.imageRes;
            return params.supplier.get();
        }

        return musicList;
    }

    public void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnMusicMenuClickListener((music, anchorView) -> main.appInitializer.getMusicDialogManager().showBottomDialog(music));
        adapter.setOnItemClickListener(position -> {
            MusicDto music = adapter.getCurrentList().get(position);
            MediaItem item = MediaItemHelper.toMediaItem(music);

            main.playerViewModel.playPlaylist(requireContext(), item);
        });
    }
}
