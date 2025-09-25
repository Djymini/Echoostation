package com.djymini.echoostation.fragments.playlistMusicFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.PlaylistDto;

import java.util.Map;
import java.util.concurrent.Executors;

public class PlaylistPersoFragment extends PlaylistMusicFragment{
    private static final String ARG_PLAYLIST = "playlist";
    private static final String ARG_PLAYLIST_NAME = "playlistName";
    private static final String ARG_ID = "playlistId";

    private String playlistName;
    private String playlistTrueName;
    private long playlistId;
    private PlaylistDto playlist;

    public PlaylistPersoFragment() {}

    public static PlaylistPersoFragment newInstance(String playlistName, String playlistTrueName, long playlistId) {
        PlaylistPersoFragment fragment = new PlaylistPersoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST, playlistName);
        args.putString(ARG_PLAYLIST_NAME, playlistTrueName);
        args.putLong(ARG_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistName = getArguments().getString(ARG_PLAYLIST);
            playlistTrueName = getArguments().getString(ARG_PLAYLIST_NAME);
            playlistId = getArguments().getLong(ARG_ID);
        }
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();

        mapPlaylist = Map.ofEntries(
                Map.entry("playlist", new PlaylistParams(R.drawable.echoostation_placeholder_playlist_3x, () -> main.dbService.getMusicDao().getMusicDetailByPlaylist(playlistId)))
        );

        executor.execute(() -> {
            musicList = getMusicList(playlistName);
            playlist = main.dbService.getPlaylistDao().getDtoById(playlistId);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void setupInfoPlaylist(){
        super.setupInfoPlaylist();
        playlistNameView.setText(playlistTrueName);
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
