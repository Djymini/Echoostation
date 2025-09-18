package com.djymini.echoostation.fragments.playlistMusicFragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.PlaylistDto;
import com.djymini.echoostation.utilities.UiUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

public class PlaylistPersoFragment extends PlaylistMusicFragment{
    private static final String ARG_PLAYLIST = "playlist";
    private static final String ARG_PLAYLISTNAME = "playlistName";
    private static final String ARG_ID = "playlistId";

    private String playlistName;
    private String playlistTrueName;
    private long playlistId;
    private PlaylistDto playlist;

    private ImageView playlistCover1, playlistCover2Image1, playlistCover2Image2, playlistCover2Image3, playlistCover2Image4;
    private GridLayout playlistCover2;

    public PlaylistPersoFragment() {}

    public static PlaylistPersoFragment newInstance(String playlistName, String playlistTrueName, long playlistId) {
        PlaylistPersoFragment fragment = new PlaylistPersoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST, playlistName);
        args.putString(ARG_PLAYLISTNAME, playlistTrueName);
        args.putLong(ARG_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PlaylistPersoFragment", "test");
        if (getArguments() != null) {
            playlistName = getArguments().getString(ARG_PLAYLIST);
            playlistTrueName = getArguments().getString(ARG_PLAYLIST);
            playlistId = getArguments().getLong(ARG_ID);
        }
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();

        mapPlaylist = Map.ofEntries(
                Map.entry("playlist", new PlaylistParams(R.drawable.echoostation_placeholder_playlist_3x, () -> main.dbService.getMusicDao().getMusicDetailByPlaylist(playlistId)))
        );

        executor.execute(() -> {
            Log.d("PlaylistPersoFragment", main.dbService.getMusicDao().getMusicDetailByPlaylist(playlistId).toString());
            musicList = getMusicList(playlistName);
            playlist = main.dbService.getPlaylistDao().getDtoById(playlistId);
        });
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
        playlistCover1 = view.findViewById(R.id.playlist_cover);
        playlistCover2 = view.findViewById(R.id.playlist_cover2);
        playlistCover2Image1 = view.findViewById(R.id.playlist_cover2_image1);
        playlistCover2Image2 = view.findViewById(R.id.playlist_cover2_image2);
        playlistCover2Image3 = view.findViewById(R.id.playlist_cover2_image3);
        playlistCover2Image4 = view.findViewById(R.id.playlist_cover2_image4);
    }

    @Override
    public void setupInfoPlaylist(){
        super.setupInfoPlaylist();

        playlistNameView.setText(playlistTrueName);
        if(!musicList.isEmpty()){
            String[] coverPlaylists = playlist.coverList.split(",");
            Set<String> set = new LinkedHashSet<>(Arrays.asList(coverPlaylists));
            List<String> coverForDisplay = new ArrayList<>(set);
            if (coverForDisplay.size() > 4) {
                playlistCover2.setVisibility(View.VISIBLE);
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(0)), R.drawable.echoostation_placeholder_album_3x, playlistCover2Image1, requireContext());
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(1)), R.drawable.echoostation_placeholder_album_3x, playlistCover2Image2, requireContext());
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(2)), R.drawable.echoostation_placeholder_album_3x, playlistCover2Image3, requireContext());
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(3)), R.drawable.echoostation_placeholder_album_3x, playlistCover2Image4, requireContext());
            }else {
                playlistCover1.setVisibility(View.VISIBLE);
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(0)), R.drawable.echoostation_placeholder_album_3x, playlistCover1, requireContext());
            }
        }else {
            UiUtilities.displayImageWithGlide(R.drawable.echoostation_placeholder_playlist_3x, R.drawable.echoostation_placeholder_album_3x, playlistCover1, requireContext());
        }
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
