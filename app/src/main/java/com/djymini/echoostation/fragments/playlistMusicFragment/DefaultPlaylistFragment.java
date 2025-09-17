package com.djymini.echoostation.fragments.playlistMusicFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.utilities.UiUtilities;

import java.util.Map;
import java.util.concurrent.Executors;

public class DefaultPlaylistFragment extends PlaylistMusicFragment {
    private static final String ARG_PLAYLIST = "playlist";

    private ImageView playlistDefaultIllustration;

    public DefaultPlaylistFragment() {}

    public static DefaultPlaylistFragment newInstance(String playlistName) {
        DefaultPlaylistFragment fragment = new DefaultPlaylistFragment();
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
                Map.entry("Récemment écoutés", new PlaylistParams(R.drawable.echoostation_cover_recently_3x, () -> main.dbService.getMusicDao().getMusicDetailRecentlyLstening())),
                Map.entry("Favoris", new PlaylistParams(R.drawable.echoostation_cover_favorite_3x, () -> main.dbService.getMusicDao().getMusicByTags(true, null, null, null, null, null, null, null, null, null, null, null, null)))
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
        playlistDefaultIllustration = view.findViewById(R.id.playlist_illustration_default);
    }

    @Override
    public void setupInfoPlaylist(){
        super.setupInfoPlaylist();
        playlistDefaultIllustration.setVisibility(View.VISIBLE);
        UiUtilities.displayImageWithGlide(playlistDefaultImage, playlistDefaultImage, playlistDefaultIllustration, requireContext());
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
