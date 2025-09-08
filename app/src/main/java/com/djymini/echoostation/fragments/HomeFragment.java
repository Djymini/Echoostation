package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.PlaylistListMusicFragment;
import com.djymini.echoostation.PlaylistType;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.AlbumAdapter;
import com.djymini.echoostation.adapters.ArtistAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.utilities.SectionLibrary;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.utilities.SortOptionArtist;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.views.ViewHomeData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends EchoostationFragment {
    private final Map<Integer, SectionLibrary> imageButtonInfo = Map.of(
            0, new SectionLibrary(R.id.recently_button, "Récemment écoutés"),
            1, new SectionLibrary(R.id.favorite_button, "Favoris"),
            2, new SectionLibrary(R.id.trend_button, "Les plus écoutés"),
            3, new SectionLibrary(R.id.good_button, "Mix Good vibe"),
            4, new SectionLibrary(R.id.motived_button, "Mix Motivation"),
            5, new SectionLibrary(R.id.party_button, "Mix Fête"),
            6, new SectionLibrary(R.id.chill_button, "Mix Détente"),
            7, new SectionLibrary(R.id.night_button, "Mix Nuit"),
            8, new SectionLibrary(R.id.sad_button, "Mix Tristesse"),
            9, new SectionLibrary(R.id.work_button, "Mix Travail")
    );

    private final Map<Integer, SectionLibrary> sections = Map.of(
            0, new SectionLibrary(R.id.data_music, Constants.LIBRARY_TAB_TITLE[0].toUpperCase()),
            1, new SectionLibrary(R.id.data_album, Constants.LIBRARY_TAB_TITLE[1].toUpperCase()),
            2, new SectionLibrary(R.id.data_artist, Constants.LIBRARY_TAB_TITLE[2].toUpperCase()),
            3, new SectionLibrary(R.id.data_genre, Constants.LIBRARY_TAB_TITLE[3].toUpperCase())
    );

    private MainActivity main;
    private ExecutorService executor;
    private List<ImageButton> imageButtonList = new ArrayList<>();

    private RecyclerView recyclerViewTopArtist;
    private RecyclerView recyclerViewTopAlbum;
    private RecyclerView recyclerViewAlbumRecentlyAdded;
    private ArtistAdapter artistAdapter;
    private AlbumAdapter albumAdapter, recentAlbumAdapter;

    private List<ArtistDto> topArtistList = new ArrayList<>();
    private List<AlbumDto> topAlbumList = new ArrayList<>();
    private List<AlbumDto> recentAlbumList = new ArrayList<>();

    private TextView listeningMounth, timeListeningMounth, listeningTotal, timeListeningTotal, neverPlayed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bindView(view);
        setupRecyclerViewRecentAlbum();
        setupRecyclerViewTopArtist();
        setupRecyclerViewTopAlbum();
        loadArtists();
        loadAlbums();

        updateStats();

        FragmentActivity activity = requireActivity();
        if (!(activity instanceof MainActivity)) return view;
        MainActivity mainActivity = (MainActivity) activity;

        sections.forEach((index, section) -> {
            ViewHomeData sectionView = view.findViewById(section.viewId);
            sectionView.setTitle(section.title);

            sectionView.setOnClickListener(v -> openLibraryTab(index));

            switch (index) {
                case 0:
                    mainActivity.loaderMediaViewModel.loadMusics().observe(getViewLifecycleOwner(),
                            musics -> sectionView.setData(String.valueOf(musics.size())));
                    break;
                case 1:
                    mainActivity.loaderMediaViewModel.loadAlbums().observe(getViewLifecycleOwner(),
                            albums -> sectionView.setData(String.valueOf(albums.size())));
                    break;
                case 2:
                    mainActivity.loaderMediaViewModel.loadArtists().observe(getViewLifecycleOwner(),
                            artists -> sectionView.setData(String.valueOf(artists.size())));
                    break;
                case 3:
                    mainActivity.loaderMediaViewModel.loadGenres().observe(getViewLifecycleOwner(),
                            genres -> sectionView.setData(String.valueOf(genres.size())));
                    break;
            }
        });

        return view;
    }

    private void openLibraryTab(int tabIndex) {
        FragmentActivity activity = requireActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).openLibraryTab(tabIndex);

        }
    }

    private void bindView(View view){
        recyclerViewTopAlbum = view.findViewById(R.id.recycler_view_top_album);
        recyclerViewAlbumRecentlyAdded = view.findViewById(R.id.recycler_view_new_album);
        recyclerViewTopArtist = view.findViewById(R.id.recycler_view_top_artist);
        for (int i = 0; i < imageButtonInfo.size(); i++) {
            int position = i;
            imageButtonList.add(view.findViewById(imageButtonInfo.get(i).viewId));
            imageButtonList.get(i).setOnClickListener(v -> {
                FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

                Fragment fragment = PlaylistListMusicFragment.newInstance(imageButtonInfo.get(position).title, 0);

                if (!fragment.isAdded()) {
                    transaction.add(R.id.frame_layout, fragment);
                } else {
                    transaction.show(fragment);
                }

                transaction.hide(main.navigator.getActiveFragment()).commit();

                main.navigator.modifyTitle(imageButtonInfo.get(position).title);
                main.navigator.setActiveFragment(fragment);
                main.navigator.updateToolbarMenu(fragment);
            });
        }

        listeningMounth = view.findViewById(R.id.listening_mounth);
        timeListeningMounth = view.findViewById(R.id.time_listening_mounth);
        listeningTotal = view.findViewById(R.id.listening_total);
        timeListeningTotal = view.findViewById(R.id.time_listening_total);
        neverPlayed = view.findViewById(R.id.never_played);
    }

    private void updateStats(){
        main.dbService.getMusicDao().countNeverPlayed().observe(getViewLifecycleOwner(),
                stat -> neverPlayed.setText(String.valueOf(stat)));

        main.dbService.getMusicDao().countMonthMusicPlayed().observe(getViewLifecycleOwner(),
                stat -> listeningMounth.setText(String.valueOf(stat)));

        main.dbService.getMusicDao().countMonthMusicPlayedTime().observe(getViewLifecycleOwner(),
                stat -> timeListeningMounth.setText(TimeUtilities.formatDurationWithHour(stat)));

        main.dbService.getMusicDao().countMusicPlayed().observe(getViewLifecycleOwner(),
                stat -> listeningTotal.setText(String.valueOf(stat)));

        main.dbService.getMusicDao().countMusicPlayedTime().observe(getViewLifecycleOwner(),
                stat -> timeListeningTotal.setText(TimeUtilities.formatDurationWithHour(stat)));
    }

    private void setupRecyclerViewTopArtist() {
        recyclerViewTopArtist.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewTopArtist.setClipToPadding(false);
        recyclerViewTopArtist.setClipChildren(false);
        artistAdapter = new ArtistAdapter();
        recyclerViewTopArtist.setAdapter(artistAdapter);

        artistAdapter.setOnItemClickListener(position -> {
            FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

            Fragment fragment = ArtistInfoFragment.newInstance(artistAdapter.getCurrentList().get(position));

            if (!fragment.isAdded()) {
                transaction.add(R.id.frame_layout, fragment);
            } else {
                transaction.show(fragment);
            }

            transaction.hide(main.navigator.getActiveFragment()).commit();

            main.navigator.modifyTitle(artistAdapter.getCurrentList().get(position).name);
            main.navigator.setActiveFragment(fragment);
            main.navigator.updateToolbarMenu(fragment);
        });
    }

    private void sortAndDisplayArtists() {
        if (topArtistList == null) return;

        executor.execute(() -> {
            requireActivity().runOnUiThread(() -> artistAdapter.submitList(topArtistList));
        });
    }

    private void loadArtists() {
        main.dbService.getArtistDao().getTopDetailLive().observe(getViewLifecycleOwner(), artists -> {
            topArtistList = new ArrayList<>(artists);
            sortAndDisplayArtists();
        });
    }

    private void setupRecyclerViewTopAlbum() {
        recyclerViewTopAlbum.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewTopAlbum.setClipToPadding(false);
        recyclerViewTopAlbum.setClipChildren(false);
        albumAdapter = new AlbumAdapter();
        recyclerViewTopAlbum.setAdapter(albumAdapter);

        albumAdapter.setOnItemClickListener(position -> {
            FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

            Fragment fragment = AlbumInfoFragment.newInstance(albumAdapter.getCurrentList().get(position));

            if (!fragment.isAdded()) {
                transaction.add(R.id.frame_layout, fragment);
            } else {
                transaction.show(fragment);
            }

            transaction.hide(main.navigator.getActiveFragment()).commit();

            main.navigator.modifyTitle(albumAdapter.getCurrentList().get(position).name);
            main.navigator.setActiveFragment(fragment);
            main.navigator.updateToolbarMenu(fragment);
        });
    }

    private void setupRecyclerViewRecentAlbum() {
        recyclerViewAlbumRecentlyAdded.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewAlbumRecentlyAdded.setClipToPadding(false);
        recyclerViewAlbumRecentlyAdded.setClipChildren(false);
        recentAlbumAdapter = new AlbumAdapter();
        recyclerViewAlbumRecentlyAdded.setAdapter(recentAlbumAdapter);

        recentAlbumAdapter.setOnItemClickListener(position -> {
            FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

            Fragment fragment = AlbumInfoFragment.newInstance(recentAlbumAdapter.getCurrentList().get(position));

            if (!fragment.isAdded()) {
                transaction.add(R.id.frame_layout, fragment);
            } else {
                transaction.show(fragment);
            }

            transaction.hide(main.navigator.getActiveFragment()).commit();

            main.navigator.modifyTitle(recentAlbumAdapter.getCurrentList().get(position).name);
            main.navigator.setActiveFragment(fragment);
            main.navigator.updateToolbarMenu(fragment);
        });
    }

    private void sortAndDisplayAlbums() {
        if (topAlbumList == null) return;

        executor.execute(() -> {
            requireActivity().runOnUiThread(() -> albumAdapter.submitList(topAlbumList));
        });
    }

    private void sortAndDisplayRecentAlbums() {
        if (recentAlbumList == null) return;

        executor.execute(() -> {
            requireActivity().runOnUiThread(() -> recentAlbumAdapter.submitList(recentAlbumList));
        });
    }

    private void loadAlbums() {
        main.dbService.getAlbumDao().getTopAlbumsDetail().observe(getViewLifecycleOwner(), albums -> {
            topAlbumList = new ArrayList<>(albums);
            sortAndDisplayAlbums();
        });
        main.dbService.getAlbumDao().getRecentAlbumsDetail().observe(getViewLifecycleOwner(), albums -> {
            recentAlbumList = new ArrayList<>(albums);
            sortAndDisplayRecentAlbums();
        });
    }
}