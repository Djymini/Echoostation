package com.djymini.echoostation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.adapters.MusicAdapter;
import com.djymini.echoostation.adapters.MusicAlbumAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.fragments.AlbumInfoFragment;
import com.djymini.echoostation.utilities.TimeUtilities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistListMusicFragment extends Fragment {
    private static final String ARG_PLAYLIST = "playlist";
    private static final String ARG_TYPE = "type";

    private MainActivity main;
    private ExecutorService executor;
    private Map<Integer, PlaylistType> typeMap = new HashMap<>();
    private List<MusicDto> musicList;

    private ImageView iconPlaylist;
    private TextView playlistNameView, playlistNumberTrack, playlistDurationTotal;
    private Button playButton, shuffleButton;
    private int playlistDefaultImage;

    private RecyclerView recyclerView;
    private MusicAdapter adapter;
    private List<MediaItem> playlist;

    private String playlistName;
    private int playlistType;

    public PlaylistListMusicFragment() {
    }

    public static PlaylistListMusicFragment newInstance(String playlistName, int playlistType) {
        PlaylistListMusicFragment fragment = new PlaylistListMusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST, playlistName);
        args.putInt(ARG_TYPE, playlistType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistName = getArguments().getString(ARG_PLAYLIST);
            playlistType = getArguments().getInt(ARG_TYPE);
            main = (MainActivity) getActivity();
        }

        typeMap.put(0, PlaylistType.DEFAULT);
        typeMap.put(1, PlaylistType.USER);

        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            musicList = getMusicList(playlistName, typeMap.get(playlistType));
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

        return view;
    }

    public List<MusicDto> getMusicList(String playlistName, PlaylistType type){
        if (type == PlaylistType.DEFAULT){
            switch (playlistName){
                case "Récemment écoutés":
                    playlistDefaultImage = R.drawable.round_history_24;
                    return main.dbService.getMusicDao().getMusicDetailRecentlyLstening();
                case "Favoris":
                    playlistDefaultImage = R.drawable.round_favorite_border_24;
                    return main.dbService.getMusicDao().getMusicDetailFavorite();
                case "Les plus écoutés":
                    playlistDefaultImage = R.drawable.round_trending_up_24;
                    return main.dbService.getMusicDao().getMusicDetailMostListening();
                case "Good vibe":
                    playlistDefaultImage = R.drawable.round_sentiment_very_satisfied_24;
                    return main.dbService.getMusicDao().getMusicDetailRecentlyLstening();
                case "Motivation":
                    playlistDefaultImage = R.drawable.round_fitness_center_24;
                    return main.dbService.getMusicDao().getMusicDetailFavorite();
                case "Fête":
                    playlistDefaultImage = R.drawable.outline_celebration_24;
                    return main.dbService.getMusicDao().getMusicDetailMostListening();
                case "Détente":
                    playlistDefaultImage = R.drawable.outline_spa_24;
                    return main.dbService.getMusicDao().getMusicDetailMostListening();
                case "Nuit":
                    playlistDefaultImage = R.drawable.outline_bedtime_24;
                    return main.dbService.getMusicDao().getMusicDetailMostListening();
                case "Tristesse":
                    playlistDefaultImage = R.drawable.round_sentiment_dissatisfied_24;
                    return main.dbService.getMusicDao().getMusicDetailMostListening();
                case "Travail":
                    playlistDefaultImage = R.drawable.outline_work_outline_24;
                    return main.dbService.getMusicDao().getMusicDetailMostListening();
            }
        }
        return musicList;
    }

    private void bindView(View view){
        iconPlaylist = view.findViewById(R.id.icon_playlist);
        playlistNameView = view.findViewById(R.id.playlist_name);
        playlistNumberTrack = view.findViewById(R.id.number_tracks);
        playlistDurationTotal = view.findViewById(R.id.duration_total);
        playButton = view.findViewById(R.id.play_button);
        shuffleButton = view.findViewById(R.id.shuffle_button);
        recyclerView = view.findViewById(R.id.recycler_view_song_playlist);
    }

    private void setupInfoPlaylist(){
        Glide.with(requireContext())
                .load(playlistDefaultImage)
                .placeholder(R.drawable.echoostation_placeholder_album_3x)
                .error(R.drawable.echoostation_placeholder_album_3x)
                .into(iconPlaylist);

        playlistNameView.setText(playlistName);

        String totalMusic = musicList.size() > 1 ? String.valueOf(musicList.size()) + " morceaux" : String.valueOf(musicList.size()) + " morceau";
        String duration = "Durée : " + TimeUtilities.durationTotal(musicList);
        playlistNumberTrack.setText(totalMusic);
        playlistDurationTotal.setText(duration);
    }

    private void setupButton(){
        playButton.setOnClickListener(v -> {
            playAlbum();
        });

        shuffleButton.setOnClickListener(v -> {
            shuffleAlbum();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnMusicMenuClickListener((music, anchorView) -> {
            main.appInitializer.getMusicDialogManager().showBottomDialog(music);
        });

        adapter.setOnItemClickListener(position -> {
            main.playerViewModel.playPlaylist(requireContext(), playlist, position);
        });
    }

    private void sortAndDisplayMusics() {
        if (musicList == null) return;

        executor.execute(() -> {
            playlist = loadPlaylist(musicList);
            requireActivity().runOnUiThread(() -> adapter.submitList(musicList));
        });
    }

    private List<MediaItem> loadPlaylist(List<MusicDto> list) {
        List<MediaItem> items = new ArrayList<>();
        for (MusicDto music : list) {
            MediaMetadata metadata = new MediaMetadata.Builder()
                    .setTitle(music.title)
                    .setArtist(music.artistName)
                    .setAlbumTitle(music.albumName)
                    .setArtworkUri(music.getCover())
                    .setDurationMs(music.duration)
                    .build();

            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(music.path)
                    .setMediaId(String.valueOf(music.id))
                    .setMediaMetadata(metadata)
                    .build();

            items.add(mediaItem);
        }
        return items;
    }

    private void playAlbum(){
        main.playerViewModel.playPlaylist(requireContext(), playlist, 0);
    }

    private void shuffleAlbum(){
        int musicPosition = (int) ( Math.random() * musicList.size()-1 );
        main.playerViewModel.playPlaylist(requireContext(), playlist, musicPosition);
        main.playerViewModel.toggleShuffle(requireContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }
}