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
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.ui.HomeImageButton;
import com.djymini.echoostation.utilities.HomeFragmentContants;
import com.djymini.echoostation.utilities.UiUtilities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class MixPlaylistFragment extends PlaylistMusicFragment{
    private static final String ARG_PLAYLIST = "playlist";

    private ImageView playlistMixGenreIllustration;
    private TextView playlistMixGenreText;
    private RelativeLayout playlistMixGenreContainer;
    private CardView playlistMixGenreTextContainer;

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

        musicList = main.mixManager.mixMap.get(playlistName);
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
        playlistMixGenreContainer = view.findViewById(R.id.playlist_illustration_mix_and_genre_container);
        playlistMixGenreIllustration = view.findViewById(R.id.playlist_illustration_mix_and_genre);
        playlistMixGenreTextContainer = view.findViewById(R.id.playlist_illustration_text_container);
        playlistMixGenreText = view.findViewById(R.id.playlist_illustration_text);
    }

    @Override
    public void setupInfoPlaylist(){
        super.setupInfoPlaylist();
        playlistMixGenreContainer.setVisibility(View.VISIBLE);

        Map<String, HomeImageButton> tag = new HashMap<>();
        for (HomeImageButton imageButton : HomeFragmentContants.homeImageButtonListMix){
            tag.put(imageButton.getNameButton(), imageButton);
        }

        String mixName = "Mix " + playlistName;
        if(!musicList.isEmpty())
            UiUtilities.displayImageWithGlide(musicList.get(0).getCover(), R.drawable.echoostation_placeholder_playlist_3x, playlistMixGenreIllustration, requireContext());
        playlistNameView.setText(mixName);

        ColorStateList tint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), tag.get(playlistName).getBackgroundColor()));
        playlistMixGenreTextContainer.setCardBackgroundColor(tint);
        playlistMixGenreText.setText(mixName.toUpperCase());

        shuffleButton.setVisibility(View.GONE);
        reloadButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void setupButton(){
        super.setupButton();
        reloadButton.setOnClickListener(v -> {
            executor.execute(() -> {
                musicList = main.mixManager.remakeTheMix(playlistName);
                sortAndDisplayMusics();
            });

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
