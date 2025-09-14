package com.djymini.echoostation;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import java.util.function.Supplier;

public class PlaylistListMusicFragment extends Fragment {
    private static final String ARG_PLAYLIST = "playlist";
    private static final String ARG_TYPE = "type";
    private static final String ARG_GENRE_NAME = "genreName";
    private static final String ARG_GENRE_ID = "genreId";

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
    private String genreName;
    private long genreId;

    private static class PlaylistParams {
        int imageRes;
        Supplier<List<MusicDto>> supplier;

        PlaylistParams(int imageRes, Supplier<List<MusicDto>> supplier) {
            this.imageRes = imageRes;
            this.supplier = supplier;
        }
    }

    private Map<String, PlaylistParams> mapPlaylist;

    public PlaylistListMusicFragment() {
    }

    public static PlaylistListMusicFragment newInstance(String playlistName, int playlistType, String genreName, long genreId) {
        PlaylistListMusicFragment fragment = new PlaylistListMusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST, playlistName);
        args.putInt(ARG_TYPE, playlistType);
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
            playlistType = getArguments().getInt(ARG_TYPE);
            genreName = getArguments().getString(ARG_GENRE_NAME);
            genreId = getArguments().getLong(ARG_GENRE_ID);
            main = (MainActivity) getActivity();
        }

        typeMap.put(0, PlaylistType.DEFAULT);
        typeMap.put(1, PlaylistType.USER);

        executor = Executors.newSingleThreadExecutor();

        mapPlaylist = Map.ofEntries(
                Map.entry("Récemment écoutés", new PlaylistParams(R.drawable.round_history_24, () -> main.dbService.getMusicDao().getMusicDetailRecentlyLstening())),
                Map.entry("Favoris", new PlaylistParams(R.drawable.round_favorite_border_24, () -> main.dbService.getMusicDao().getMusicByTags(true, null, null, null, null, null, null, null, null, null, null, null, null))),
                Map.entry("Les plus écoutés", new PlaylistParams(R.drawable.round_trending_up_24, () -> main.dbService.getMusicDao().getMusicDetailMostListening())),
                Map.entry("Good vibe", new PlaylistParams(R.drawable.round_sentiment_very_satisfied_24, () -> main.dbService.getMusicService().makeGoodVibeMix())),
                Map.entry("Motivation", new PlaylistParams(R.drawable.round_fitness_center_24, () -> main.dbService.getMusicService().makeMotivationMix())),
                Map.entry("Fête", new PlaylistParams(R.drawable.outline_celebration_24, () -> main.dbService.getMusicService().makePartyMix())),
                Map.entry("Détente", new PlaylistParams(R.drawable.outline_spa_24, () -> main.dbService.getMusicService().makeChillMix())),
                Map.entry("Nuit", new PlaylistParams(R.drawable.outline_bedtime_24, () -> main.dbService.getMusicService().makeNightMix())),
                Map.entry("Tristesse", new PlaylistParams(R.drawable.round_sentiment_dissatisfied_24, () -> main.dbService.getMusicService().makeSadMix())),
                Map.entry("Travail", new PlaylistParams(R.drawable.outline_work_outline_24, () -> main.dbService.getMusicService().makeWorkMix())),
                Map.entry("Gaming", new PlaylistParams(R.drawable.outline_sports_esports_24, () -> main.dbService.getMusicService().makeGamingMix())),
                Map.entry("Conduite", new PlaylistParams(R.drawable.outline_drive_eta_24, () -> main.dbService.getMusicService().makeDriveeMix())),
                Map.entry("Reflexion", new PlaylistParams(R.drawable.outline_school_24, () -> main.dbService.getMusicService().makeMindMix())),
                Map.entry("Matin", new PlaylistParams(R.drawable.outline_wb_sunny_24, () -> main.dbService.getMusicService().makeMorningMix())),
                Map.entry("Ménage", new PlaylistParams(R.drawable.outline_cleaning_services_24, () -> main.dbService.getMusicService().makeWalkMix())),
                Map.entry("Genre", new PlaylistParams(R.drawable.echoostation_placeholder_album_3x, () -> main.dbService.getMusicDao().getMusicDetailByGenre(genreId)))
        );

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

        Toolbar toolbar = main.navigator.getToolbar(); // ou getActivity().findViewById(...)
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // Affiche le bouton retour
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            boolean nightMode = (getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;


            Drawable upArrow = ContextCompat.getDrawable(requireContext(),
                    nightMode ? R.drawable.round_arrow_back_24_night : R.drawable.round_arrow_back_24_normal);

            if (upArrow != null) {
                activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }

        // Gérer le clic
        toolbar.setNavigationOnClickListener(v -> {
            main.navigator.goBackToLibrary(typeMap.get(playlistType) == PlaylistType.DEFAULT ? R.id.home : R.id.library);
        });

        // Gérer le bouton retour physique
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        main.navigator.goBackToLibrary(typeMap.get(playlistType) == PlaylistType.DEFAULT ? R.id.home : R.id.library);
                    }
                }
        );

        return view;
    }

    public List<MusicDto> getMusicList(String playlistName, PlaylistType type) {
        if (type == PlaylistType.DEFAULT) {
            Log.d("PlaylistListMusicFragment", playlistName);
            PlaylistParams params = mapPlaylist.get(playlistName);
            if (params != null) {
                playlistDefaultImage = params.imageRes;
                return params.supplier.get();
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

        playlistNameView.setText(playlistName.equals("Genre") ? genreName : playlistName);
        if(!playlistName.equals("Genre")){
            ColorStateList tint = ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.accentColor));
            iconPlaylist.setImageTintList(tint);
        }

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
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            activity.getSupportActionBar().setHomeAsUpIndicator(null);
        }
    }
}