package com.djymini.echoostation.fragments.playlistMusicFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.utilities.UiUtilities;

import java.util.Map;
import java.util.concurrent.Executors;

public class TrendPlaylistFragment extends PlaylistMusicFragment{
    private static final String ARG_PLAYLIST = "playlist";

    private ImageView albumFirstImage, albumSecondImage, albumThirdImage;
    private RelativeLayout playlistTrendIllustration;

    public TrendPlaylistFragment() {}

    public static TrendPlaylistFragment newInstance(String playlistName) {
        TrendPlaylistFragment fragment = new TrendPlaylistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST, playlistName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistName = getArguments().getString(ARG_PLAYLIST);
        }
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();

        mapPlaylist = Map.ofEntries(
                Map.entry("Les plus écoutés", new PlaylistParams(R.drawable.echoostation_cover_trend_3x, () -> main.dbService.getMusicDao().getMusicDetailMostListening()))
        );
        executor.execute(() -> musicList = getMusicList(playlistName));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_list_music, container, false);
        bindView(view);
        setupInfoPlaylist();
        setupButton();
        setupRecyclerView();
        sortAndDisplayMusics();
        backButtonManager(R.id.home);
        return view;
    }

    @Override
    public void bindView(View view){
        super.bindView(view);
        playlistTrendIllustration = view.findViewById(R.id.playlist_illustration_trend);
        albumFirstImage = view.findViewById(R.id.album_image_first);
        albumSecondImage = view.findViewById(R.id.album_image_second);
        albumThirdImage = view.findViewById(R.id.album_image_third);
    }

    @Override
    public void setupInfoPlaylist(){
        super.setupInfoPlaylist();
        playlistTrendIllustration.setVisibility(View.VISIBLE);
        UiUtilities.displayImageWithGlide(musicList.get(0).getCover(), R.drawable.echoostation_placeholder_album_3x, albumFirstImage, requireContext());
        UiUtilities.displayImageWithGlide(musicList.get(1).getCover(), R.drawable.echoostation_placeholder_album_3x, albumSecondImage, requireContext());
        UiUtilities.displayImageWithGlide(musicList.get(2).getCover(), R.drawable.echoostation_placeholder_album_3x, albumThirdImage, requireContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            activity.getSupportActionBar().setHomeAsUpIndicator(null);
        }
    }
}
