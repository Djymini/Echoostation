package com.djymini.echoostation.fragments.playlistMusicFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.utilities.UiUtilities;

import java.util.Map;
import java.util.concurrent.Executors;

public class GenrePlaylistFragment extends PlaylistMusicFragment{
    private static final String ARG_PLAYLIST = "playlist";
    private static final String ARG_GENRE_NAME = "genreName";
    private static final String ARG_GENRE_ID = "genreId";

    private String genreName;
    private long genreId;

    public GenrePlaylistFragment() {}

    public static GenrePlaylistFragment newInstance(String playlistName, String genreName, long genreId) {
        GenrePlaylistFragment fragment = new GenrePlaylistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST, playlistName);
        args.putString(ARG_GENRE_NAME, genreName);
        args.putLong(ARG_GENRE_ID, genreId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistName = getArguments().getString(ARG_PLAYLIST);
            genreName = getArguments().getString(ARG_GENRE_NAME);
            genreId = getArguments().getLong(ARG_GENRE_ID);
        }
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();

        mapPlaylist = Map.ofEntries(
                Map.entry("Genre", new PlaylistParams(R.drawable.echoostation_placeholder_genre_3x, () -> main.dbService.getMusicDao().getMusicDetailByGenre(genreId)))
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
    public void setupInfoPlaylist(){
        super.setupInfoPlaylist();
        playlistNameView.setText(genreName);
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
