package com.djymini.echoostation.fragments.playlistMusicFragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.ui.HomeImageButton;
import com.djymini.echoostation.utilities.HomeFragmentContants;
import com.djymini.echoostation.utilities.UiUtilities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class MixPlaylistFragment extends PlaylistMusicFragment{
    private static final String ARG_PLAYLIST = "playlist";

    public MixPlaylistFragment() {}

    public static MixPlaylistFragment newInstance(String playlistName) {
        MixPlaylistFragment fragment = new MixPlaylistFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_list_music, container, false);
        bindView(view);

        main.playlistAndMixViewModel.getMixMapLive().observe(getViewLifecycleOwner(), map -> {
            musicList = map.get(playlistName);
            setupInfoPlaylist();
            sortAndDisplayMusics();
        });
        setupButton();
        setupRecyclerView();
        backButtonManager(R.id.home);
        return view;
    }

    @Override
    public void setupInfoPlaylist(){
        super.setupInfoPlaylist();

        Map<String, HomeImageButton> tag = new HashMap<>();
        for (HomeImageButton imageButton : HomeFragmentContants.homeImageButtonListMix){
            tag.put(imageButton.getNameButton(), imageButton);
        }

        String mixName = "Mix " + playlistName;
        playlistNameView.setText(mixName);

        shuffleButton.setVisibility(View.GONE);
        reloadButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void setupButton(){
        super.setupButton();
        reloadButton.setOnClickListener(v -> {
            main.playlistAndMixViewModel.remakeTheMix(playlistName);
            sortAndDisplayMusics();

        });
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
