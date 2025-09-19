package com.djymini.echoostation.fragments.mediaDetailFragment;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.AlbumAdapter;
import com.djymini.echoostation.adapters.MusicAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.utilities.TimeUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArtistInfoFragment extends Fragment {
    private static final String ARG_ARTIST = "artist";
    private ArtistDto artist;

    private List<MusicDto> musicList;
    private List<AlbumDto> albumList;
    private List<AlbumDto> albumApparitionList = new ArrayList<>();

    private ImageView backgroundImage, albumImage, artistImage;
    private TextView albumName, albumDate, artistName, numberTrack, durationTotal, artistNumberAlbum, artistDescription;
    private Button playButton, shuffleButton;
    private LinearLayout bestSongContainer, albumContainer, albumApparitionContainer, biographieContainer;

    private RecyclerView recyclerViewBestSongs, recyclerViewAlbum, recyclerViewAlbumApparition;
    private List<MusicDto> currentMusicList = new ArrayList<>();
    private List<MusicDto> bestListeningSong = new ArrayList<>();
    private List<MediaItem> playlist2;
    private List<AlbumDto> albumArtistList = new ArrayList<>();
    private MusicAdapter adapterMusic;
    private AlbumAdapter adapterAlbum, adapterAlbumApparition;
    private TextView musicCounterView;
    private Spinner spinner;
    private String search;
    private ActionMode actionMode;
    private ActivityResultLauncher<IntentSenderRequest> deleteMultipleLauncher;
    private List<MusicDto> musicsPendingDeletion;
    private List<MediaItem> bestListeningMusicPlaylist;

    private MainActivity main;
    private ExecutorService executor;

    public ArtistInfoFragment() {
    }

    public static ArtistInfoFragment newInstance(ArtistDto newArtist) {
        ArtistInfoFragment fragment = new ArtistInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ARTIST, newArtist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artist = getArguments().getParcelable(ARG_ARTIST);
            main = (MainActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_info, container, false);
        executor = Executors.newSingleThreadExecutor();
        bindView(view);
        setupUi();
        setupRecyclerViewBestSongs();
        setupRecyclerViewAlbum();
        setupRecyclerViewAlbumApparition();

        loadMusics();

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
            main.navigator.goBackToLibrary(R.id.library);
        });

        // Gérer le bouton retour physique
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        main.navigator.goBackToLibrary(R.id.library);
                    }
                }
        );

        return view;
    }

    private void bindView(View view){
        backgroundImage = view.findViewById(R.id.background_image_artist);
        artistName = view.findViewById(R.id.artist_name);
        artistNumberAlbum = view.findViewById(R.id.artist_number_album);
        numberTrack = view.findViewById(R.id.artist_number_songs);
        durationTotal = view.findViewById(R.id.duration_total);
        artistDescription = view.findViewById(R.id.artist_description);
        recyclerViewBestSongs = view.findViewById(R.id.recycler_view_best_song);
        recyclerViewAlbum = view.findViewById(R.id.recycler_view_album);
        recyclerViewAlbumApparition = view.findViewById(R.id.recycler_view_album_apparition);

        bestSongContainer = view.findViewById(R.id.best_song_container);
        albumContainer = view.findViewById(R.id.album_container);
        albumApparitionContainer = view.findViewById(R.id.album_apparition_container);
        biographieContainer = view.findViewById(R.id.biographie_container);
    }

    private void setupUi(){
        setText();
        setArtistImage();
    }

    private void setArtistImage(){
        String photoPath = artist.photoPath;
        File file = null;

        if (photoPath != null && !photoPath.isEmpty()) {
            file = new File(photoPath);
            if (!file.exists()) file = null; // fallback si fichier supprimé
        }
        Glide.with(requireContext())
                .load(file != null ? file : R.drawable.echoostation_placeholder_artist_3x)
                .placeholder(R.drawable.echoostation_placeholder_artist_3x)
                .error(R.drawable.echoostation_placeholder_artist_3x)
                .into(backgroundImage);
    }

    private void setText() {
        artistName.setText(artist.name);

        if (artist.description != null && !artist.description.isEmpty()) {
            artistDescription.setText(HtmlCompat.fromHtml(artist.description, HtmlCompat.FROM_HTML_MODE_LEGACY));
            artistDescription.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            biographieContainer.setVisibility(View.GONE);
        }
    }

    private void loadMusics() {
        main.dbService.getMusicDao().getMusicDetailByArtistLiveBest5(String.valueOf(artist.id)).observe(getViewLifecycleOwner(), musics -> {
            bestListeningSong = new ArrayList<>(musics);
            sortAndDisplayBestMusics();
            bestListeningMusicPlaylist = MediaItemHelper.loadPlaylist(bestListeningSong);
        });

        main.dbService.getAlbumDao().getAllByArtistDetailLive(artist.id).observe(getViewLifecycleOwner(), albums -> {
            albumList = new ArrayList<>(albums);
            artistNumberAlbum.setText(albumArtistList.size() > 1 ? albums.size() + " albums" : albums.size() + " album");
            sortAndDisplayAlbum();
        });

        main.dbService.getMusicDao().getMusicDetailByArtistLive(String.valueOf(artist.id)).observe(getViewLifecycleOwner(), musics -> {
            currentMusicList = new ArrayList<>(musics);
            durationTotal.setText(durationTotal(musics));
            numberTrack.setText(currentMusicList.size() > 1 ? musics.size() + " morceaux" : musics.size() + " morceau");

            for(MusicDto musicDto : musics){
                executor.execute(() -> {
                    AlbumDto albumDto = main.dbService.getAlbumDao().getAlbumDetail(musicDto.albumId);
                    boolean alreadyInList = albumList.stream().anyMatch(a -> a.id == albumDto.id)
                            || albumApparitionList.stream().anyMatch(a -> a.id == albumDto.id);

                    if (!alreadyInList) {
                        albumApparitionList.add(albumDto);
                    }
                    sortAndDisplayAlbumApparition();
                });
            }

        });

    }

    private String durationTotal(List<MusicDto> musicList){
        long durationTotal = 0;
        for(MusicDto music : musicList){
            durationTotal += music.duration;
        }

        return TimeUtilities.formatDurationWithHour(durationTotal);
    }

    private void setupRecyclerViewBestSongs() {
        recyclerViewBestSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterMusic = new MusicAdapter();
        recyclerViewBestSongs.setAdapter(adapterMusic);

        adapterMusic.setOnMusicMenuClickListener((music, anchorView) -> {
            main.appInitializer.getMusicDialogManager().showBottomDialog(music);
        });

        adapterMusic.setOnItemClickListener(position -> {
            List<MediaItem> globalPlaylist = MediaItemHelper.loadPlaylist(bestListeningSong);
            main.playerViewModel.setPlaylist(globalPlaylist);

            MusicDto music = adapterMusic.getCurrentList().get(position);
            MediaItem item = MediaItemHelper.toMediaItem(music);

            main.playerViewModel.playPlaylist(requireContext(), item);
        });
    }

    private void setupRecyclerViewAlbum() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerViewAlbum.setLayoutManager(linearLayoutManager);
        adapterAlbum = new AlbumAdapter();
        recyclerViewAlbum.setAdapter(adapterAlbum);

        adapterAlbum.setOnMusicMenuClickListener((album, anchorView) -> {
            main.appInitializer.getMusicDialogManager().showBottomDialog(album);
        });

        adapterAlbum.setOnItemClickListener(position -> {
            FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

            Fragment fragment = AlbumInfoFragment.newInstance(adapterAlbum.getCurrentList().get(position));

            if (!fragment.isAdded()) {
                transaction.add(R.id.frame_layout, fragment);
            } else {
                transaction.show(fragment);
            }

            transaction.hide(main.navigator.getActiveFragment()).commit();

            main.navigator.modifyTitle(adapterAlbum.getCurrentList().get(position).name);
            main.navigator.setActiveFragment(fragment);
        });
    }

    private void setupRecyclerViewAlbumApparition() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerViewAlbumApparition.setLayoutManager(linearLayoutManager);
        adapterAlbumApparition = new AlbumAdapter();
        recyclerViewAlbumApparition.setAdapter(adapterAlbumApparition);

        adapterAlbumApparition.setOnMusicMenuClickListener((album, anchorView) -> {
            main.appInitializer.getMusicDialogManager().showBottomDialog(album);
        });

        adapterAlbumApparition.setOnItemClickListener(position -> {
            FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

            Fragment fragment = AlbumInfoFragment.newInstance(adapterAlbumApparition.getCurrentList().get(position));

            if (!fragment.isAdded()) {
                transaction.add(R.id.frame_layout, fragment);
            } else {
                transaction.show(fragment);
            }

            transaction.hide(main.navigator.getActiveFragment()).commit();

            main.navigator.modifyTitle(adapterAlbumApparition.getCurrentList().get(position).name);
            main.navigator.setActiveFragment(fragment);
        });
    }

    private void sortAndDisplayBestMusics() {
        if (bestListeningSong == null) return;

        if(bestListeningSong.size() >= 1){
            bestSongContainer.setVisibility(View.VISIBLE);
        }else {
            bestSongContainer.setVisibility(View.GONE);
        }

        executor.execute(() -> {
            List<MusicDto> filtered = bestListeningSong;
            requireActivity().runOnUiThread(() -> adapterMusic.submitList(filtered));
        });
    }

    private void sortAndDisplayAlbum() {
        if (albumList == null) return;

        if(albumList.size() >= 1){
            albumContainer.setVisibility(View.VISIBLE);
        }else {
            albumContainer.setVisibility(View.GONE);
        }

        executor.execute(() -> {
            List<AlbumDto> filtered = albumList;
            Collections.sort(filtered, (album1, album2) -> album1.year - album2.year);
            //playlist = loadPlaylist(filtered);
            requireActivity().runOnUiThread(() -> adapterAlbum.submitList(filtered));
        });
    }

    private void sortAndDisplayAlbumApparition() {
        if (albumApparitionList == null) return;


        if(albumApparitionList.size() >= 1){
            albumApparitionContainer.setVisibility(View.VISIBLE);
        }else {
            albumApparitionContainer.setVisibility(View.GONE);
        }

        executor.execute(() -> {
            List<AlbumDto> filtered = albumApparitionList;
            Collections.sort(filtered, (album1, album2) -> album1.year - album2.year);
            //playlist = loadPlaylist(filtered);
            requireActivity().runOnUiThread(() -> adapterAlbumApparition.submitList(filtered));
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