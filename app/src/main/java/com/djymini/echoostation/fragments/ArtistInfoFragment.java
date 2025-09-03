package com.djymini.echoostation.fragments;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.AlbumAdapter;
import com.djymini.echoostation.adapters.MusicAlbumAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.ui.MusicDialogManager;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private MusicPlayerViewModel playerViewModel;
    private RecyclerView recyclerViewBestSongs, recyclerViewAlbum, recyclerViewAlbumApparition;
    private List<MusicDto> currentMusicList = new ArrayList<>();
    private List<MusicDto> bestListeningSong = new ArrayList<>();
    private List<AlbumDto> albumArtistList = new ArrayList<>();
    private MusicAlbumAdapter adapterMusic;
    private AlbumAdapter adapterAlbum, adapterAlbumApparition;
    private TextView musicCounterView;
    private Spinner spinner;
    private String search;
    private ActionMode actionMode;
    private ActivityResultLauncher<IntentSenderRequest> deleteMultipleLauncher;
    private List<MusicDto> musicsPendingDeletion;
    private List<MediaItem> playlist;

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
                .load(file != null ? file : R.drawable.echoostation_placeholder_music_3x)
                .placeholder(R.drawable.echoostation_placeholder_music_3x)
                .error(R.drawable.echoostation_placeholder_music_3x)
                .into(backgroundImage);
    }

    private void setText(){
        artistName.setText(artist.name);
        artistDescription.setText(artist.description);
    }

    private void loadMusics() {
        main.dbService.getMusicDao().getMusicDetailByArtistLive(artist.id).observe(getViewLifecycleOwner(), musics -> {
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
                });
            }

            sortAndDisplayAlbumApparition();
        });

        main.dbService.getMusicDao().getMusicDetailByArtistLiveBest5(artist.id).observe(getViewLifecycleOwner(), musics -> {
            bestListeningSong = new ArrayList<>(musics);
            sortAndDisplayBestMusics();

        });

        main.dbService.getAlbumDao().getAllByArtistDetailLive(artist.id).observe(getViewLifecycleOwner(), albums -> {
            albumList = new ArrayList<>(albums);
            artistNumberAlbum.setText(albumArtistList.size() > 1 ? albums.size() + " albums" : albums.size() + " album");
            sortAndDisplayAlbum();
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
        adapterMusic = new MusicAlbumAdapter();
        recyclerViewBestSongs.setAdapter(adapterMusic);

        adapterMusic.setOnMusicMenuClickListener((music, anchorView) -> {
            main.appInitializer.getMusicDialogManager().showBottomDialog(music);
        });

        adapterMusic.setOnItemClickListener(position -> {
            playerViewModel.playPlaylist(requireContext(), playlist, position);
        });
    }

    private void setupRecyclerViewAlbum() {
        recyclerViewAlbum.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewAlbum.setClipToPadding(false);
        recyclerViewAlbum.setClipChildren(false);
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
        recyclerViewAlbumApparition.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewAlbumApparition.setClipToPadding(false);
        recyclerViewAlbumApparition.setClipChildren(false);
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
        if (musicList == null) return;

        executor.execute(() -> {
            List<MusicDto> filtered = bestListeningSong;
            Collections.sort(filtered, (music1, music2) -> music1.listeningNumber - music2.listeningNumber);
            //playlist = loadPlaylist(filtered);
            requireActivity().runOnUiThread(() -> adapterMusic.submitList(filtered));
        });
    }

    private void sortAndDisplayAlbum() {
        if (albumList == null) return;

        executor.execute(() -> {
            List<AlbumDto> filtered = albumList;
            Collections.sort(filtered, (album1, album2) -> album1.year - album2.year);
            //playlist = loadPlaylist(filtered);
            requireActivity().runOnUiThread(() -> adapterAlbum.submitList(filtered));
        });
    }

    private void sortAndDisplayAlbumApparition() {
        if (albumList == null) return;

        executor.execute(() -> {
            List<AlbumDto> filtered = albumApparitionList;
            Collections.sort(filtered, (album1, album2) -> album1.year - album2.year);
            //playlist = loadPlaylist(filtered);
            requireActivity().runOnUiThread(() -> adapterAlbumApparition.submitList(filtered));
        });
    }
}