package com.djymini.echoostation;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;
import com.djymini.echoostation.testUtilities.BaseTestUtil;
import com.djymini.echoostation.testUtilities.LiveDataTestUtil;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ApplicationProvider;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class MusicDaoTest {
    private EchooStationDatabase db;
    private MusicDao musicDao;
    private AlbumDao albumDao;
    private ArtistDao artistDao;
    private GenreDao genreDao;
    private StatisticDao statisticDao;
    private MusicService musicService;
    private AlbumService albumService;
    private GenreService genreService;
    private ArtistService artistService;
    private StatisticService statisticService;

    private List<Map<String, Object>> fakeMediaStoreData;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, EchooStationDatabase.class)
                .allowMainThreadQueries()
                .build();

        musicDao = db.musicDao();
        albumDao = db.albumDao();
        artistDao = db.artistDao();
        genreDao = db.genreDao();
        statisticDao = db.statisticDao();
        musicService = new MusicService(musicDao, statisticDao);
        albumService = new AlbumService(albumDao, statisticDao);
        genreService = new GenreService(genreDao, statisticDao);
        artistService = new ArtistService(artistDao, statisticDao);
        statisticService = new StatisticService(statisticDao);

        fakeMediaStoreData = BaseTestUtil.createFakeData();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void InsertAndGet(){
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            long idGenre = genreService.addGenre(fakeMedia.get(MediaStore.Audio.Media.GENRE).toString(), statisticService, context);
            long idAlbum = albumService.add(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), artistService, statisticService, context);
            long idMusic = musicService.add(fakeMedia.get(MediaStore.Audio.Media.DATA).toString(), fakeMedia.get(MediaStore.Audio.Media.TITLE).toString(), Long.parseLong(fakeMedia.get(MediaStore.Audio.Media.DURATION).toString()), Integer.parseInt(fakeMedia.get(MediaStore.Audio.Media.TRACK).toString()), fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), idAlbum, idGenre, artistService, statisticService, context);
        }

        try {
            List<Music> result1 = LiveDataTestUtil.getOrAwaitValue(musicDao.getAllLive());
            System.out.println(result1);
            assertEquals(15, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Music> result2 = musicDao.getAll();
        for(Music music : result2){
            String string = String.format("id : %s, path : %s", String.valueOf(music.id), music.path);
            Log.d(music.title, string);
        }
        assertEquals(15, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Morning Light", result2.get(0).title);
        assertEquals("track15.mp3", result2.get(14).title);
    }
}

