package com.djymini.echoostation.fragments;

import static com.djymini.echoostation.utilities.HomeFragmentContants.homeImageButtonListMix;
import static com.djymini.echoostation.utilities.HomeFragmentContants.homeImageButtonListPrimary;
import static com.djymini.echoostation.utilities.HomeFragmentContants.sections;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.AlbumAdapter;
import com.djymini.echoostation.adapters.ArtistAdapter;
import com.djymini.echoostation.adapters.HomeImageButtonAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.helpers.FragmentHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.ListMediaUtilities;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.views.ViewHomeData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends EchoostationFragment {
    private MainActivity main;
    private ExecutorService executor;

    private RecyclerView recyclerViewButtonPrimary, recyclerViewButtonMix, recyclerViewTopArtist, recyclerViewTopAlbum, recyclerViewAlbumRecentlyAdded;

    private ArtistAdapter artistAdapter;
    private AlbumAdapter albumAdapter, recentAlbumAdapter;

    private List<ArtistDto> topArtistList = new ArrayList<>();
    private List<AlbumDto> topAlbumList = new ArrayList<>();
    private List<AlbumDto> recentAlbumList = new ArrayList<>();

    private TextView listeningMonth, timeListeningMonth, listeningTotal, timeListeningTotal, neverPlayed;

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

        setupRecyclerViewButton();
        setupRecyclerViewRecentAlbum();
        setupRecyclerViewTopArtist();
        setupRecyclerViewTopAlbum();
        loadArtistAndAlbum();

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
        recyclerViewButtonPrimary = view.findViewById(R.id.recycler_view_image_button_primary);
        recyclerViewButtonMix = view.findViewById(R.id.recycler_view_image_button_mix);
        recyclerViewTopAlbum = view.findViewById(R.id.recycler_view_top_album);
        recyclerViewAlbumRecentlyAdded = view.findViewById(R.id.recycler_view_new_album);
        recyclerViewTopArtist = view.findViewById(R.id.recycler_view_top_artist);

        listeningMonth = view.findViewById(R.id.listening_mounth);
        timeListeningMonth = view.findViewById(R.id.time_listening_mounth);
        listeningTotal = view.findViewById(R.id.listening_total);
        timeListeningTotal = view.findViewById(R.id.time_listening_total);
        neverPlayed = view.findViewById(R.id.never_played);
    }

    private void setupRecyclerViewButton(){
        HomeImageButtonAdapter homeImageButtonAdapterMix = new HomeImageButtonAdapter(homeImageButtonListMix, main);
        RecyclerViewHelper.setupRecyclerViewGrid(recyclerViewButtonMix, getContext(), homeImageButtonAdapterMix, 4, false);

        HomeImageButtonAdapter homeImageButtonAdapterPrimary = new HomeImageButtonAdapter(homeImageButtonListPrimary, main);
        RecyclerViewHelper.setupRecyclerViewLinear(recyclerViewButtonPrimary, getContext(), homeImageButtonAdapterPrimary, LinearLayoutManager.HORIZONTAL, false);
    }

    private void setupRecyclerViewTopArtist() {
        artistAdapter = new ArtistAdapter();
        RecyclerViewHelper.setupRecyclerViewGrid(recyclerViewTopArtist, getContext(), artistAdapter, 3, false);

        artistAdapter.setOnItemClickListener(position -> {
            Fragment fragment = ArtistInfoFragment.newInstance(artistAdapter.getCurrentList().get(position));
            FragmentHelper.fragmentManager(main, fragment, artistAdapter.getCurrentList().get(position).name);
        });
    }

    private void setupRecyclerViewTopAlbum() {
        albumAdapter = new AlbumAdapter();
        RecyclerViewHelper.setupRecyclerViewGrid(recyclerViewTopAlbum, getContext(), albumAdapter, 3, false);

        albumAdapter.setOnItemClickListener(position -> {
            Fragment fragment = AlbumInfoFragment.newInstance(albumAdapter.getCurrentList().get(position));
            FragmentHelper.fragmentManager(main, fragment, albumAdapter.getCurrentList().get(position).name);
        });
    }

    private void setupRecyclerViewRecentAlbum() {
        recentAlbumAdapter = new AlbumAdapter();
        RecyclerViewHelper.setupRecyclerViewGrid(recyclerViewAlbumRecentlyAdded, getContext(), recentAlbumAdapter, 3, false);

        recentAlbumAdapter.setOnItemClickListener(position -> {
            Fragment fragment = AlbumInfoFragment.newInstance(recentAlbumAdapter.getCurrentList().get(position));
            FragmentHelper.fragmentManager(main, fragment, recentAlbumAdapter.getCurrentList().get(position).name);
        });
    }

    private void updateStats(){
        main.dbService.getMusicDao().countNeverPlayed().observe(getViewLifecycleOwner(),
                stat -> neverPlayed.setText(String.valueOf(stat)));

        main.dbService.getMusicDao().countMonthMusicPlayed().observe(getViewLifecycleOwner(),
                stat -> listeningMonth.setText(String.valueOf(stat)));

        main.dbService.getMusicDao().countMonthMusicPlayedTime().observe(getViewLifecycleOwner(),
                stat -> timeListeningMonth.setText(TimeUtilities.formatDurationWithHour(stat)));

        main.dbService.getMusicDao().countMusicPlayed().observe(getViewLifecycleOwner(),
                stat -> listeningTotal.setText(String.valueOf(stat)));

        main.dbService.getMusicDao().countMusicPlayedTime().observe(getViewLifecycleOwner(),
                stat -> timeListeningTotal.setText(TimeUtilities.formatDurationWithHour(stat)));
    }

    private void loadArtistAndAlbum() {
        main.dbService.getArtistDao().getTopDetailLive().observe(getViewLifecycleOwner(), artists -> {
            topArtistList = new ArrayList<>(artists);
            ListMediaUtilities.displayList(topArtistList, executor, requireActivity(), () -> artistAdapter.submitList(topArtistList));
        });

        main.dbService.getAlbumDao().getTopAlbumsDetail().observe(getViewLifecycleOwner(), albums -> {
            topAlbumList = new ArrayList<>(albums);
            ListMediaUtilities.displayList(topAlbumList, executor, requireActivity(), () -> albumAdapter.submitList(topAlbumList));
        });
        main.dbService.getAlbumDao().getRecentAlbumsDetail().observe(getViewLifecycleOwner(), albums -> {
            recentAlbumList = new ArrayList<>(albums);
            ListMediaUtilities.displayList(recentAlbumList, executor, requireActivity(), () -> recentAlbumAdapter.submitList(recentAlbumList));
        });
    }
}