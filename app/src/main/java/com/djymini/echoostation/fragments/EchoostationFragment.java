package com.djymini.echoostation.fragments;

import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MoodDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EchoostationFragment extends Fragment{
    public AlbumDao albumDao;
    public ArtistDao artistDao;
    public GenreDao genreDao;
    public MoodDao moodDao;
    public MusicDao musicDao;
    public PlaylistDao playlistDao;
    public StatisticDao statisticDao;

    public AlbumService albumService;
    public ArtistService artistService;
    public GenreService genreService;
    public MusicService musicService;
    public StatisticService statisticService;

    public MainActivity main;
    public FastScrollRecyclerView recyclerView;
    public TextView counterView;
    public Spinner spinner;
    public String search;
    public ActionMode actionMode;
    public ExecutorService executor;

    public void setupDaoAndService(){
        musicDao = main.dbService.getMusicDao();
        statisticDao = main.dbService.getStatisticDao();
        artistDao = main.dbService.getArtistDao();
        albumDao = main.dbService.getAlbumDao();
        genreDao = main.dbService.getGenreDao();
        moodDao = main.dbService.getMoodDao();
        playlistDao = main.dbService.getPlaylistDao();

        statisticService = new StatisticService(statisticDao);
        albumService = new AlbumService(albumDao, statisticDao, statisticService);
        artistService = new ArtistService(artistDao, statisticDao, statisticService, requireContext());
        genreService = new GenreService(genreDao, statisticDao, statisticService, requireContext());
        musicService = new MusicService(musicDao, statisticDao, statisticService);
    }

    public void loadMedias() {}

    public void sortAndDisplayMedias(int position) {}

    public static <E> List<E> fullTextSearchByLogicalOr(List<E> list, String keyword, List<Function<E, String>> mappers) {
        if (keyword == null || keyword.trim().isEmpty()) return list;

        String lowerKeyword = keyword.toLowerCase();

        return list.stream().filter(e -> mappers.stream().anyMatch(
                        mapper -> containsIgnoreCase(mapper.apply(e), lowerKeyword)
                ))
                .collect(Collectors.toList());
    }

    private static boolean containsIgnoreCase(String text, String keyword) {
        return text != null && keyword != null && text.toLowerCase().contains(keyword);
    }
}
