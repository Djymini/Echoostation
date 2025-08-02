package com.djymini.echoostation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Statistic;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.StatisticService;
import com.djymini.echoostation.testUtilities.BaseTestUtil;
import com.djymini.echoostation.testUtilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class AlbumDaoTest {
    private EchooStationDatabase db;
    private AlbumDao albumDao;
    private ArtistDao artistDao;
    private StatisticDao statisticDao;
    private AlbumService albumService;
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

        albumDao = db.albumDao();
        artistDao = db.artistDao();
        statisticDao = db.statisticDao();
        albumService = new AlbumService(albumDao, statisticDao);
        artistService = new ArtistService(artistDao, statisticDao);
        statisticService = new StatisticService(statisticDao);

        fakeMediaStoreData = BaseTestUtil.createFakeData();
    }

    @After
    public void closeDb() {
        db.close();
    }

    //TODO: Make the test of Dao and service album

    @Test
    public void InsertAndGet(){
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Album albumForAddInDb = new Album(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), artistService.addAllMusicArtist(fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), statisticService, context).get(0), statisticService.createStatistic());
            albumDao.insert(albumForAddInDb);
        }

        try {
            List<Album> result1 = LiveDataTestUtil.getOrAwaitValue(albumDao.getAllLive());
            System.out.println(result1);
            assertEquals(15, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Album> result2 = albumDao.getAll();
        assertEquals(15, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Sunrise Vibes", result2.get(0).name);
        assertEquals("", result2.get(14).name);
    }

    @Test
    public void insertAndDelete() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Album albumForAddInDb = new Album(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), artistService.addAllMusicArtist(fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), statisticService, context).get(0), statisticService.createStatistic());
            albumDao.insert(albumForAddInDb);
        }

        Album albumToDelete = albumDao.getById(1);
        albumDao.delete(albumToDelete);

        List<Album> result2 = albumDao.getAll();
        assertEquals(14, result2.size());
        assertEquals(2, result2.get(0).id);
        assertNotEquals("Aurora", result2.get(0).name);
    }

    @Test
    public void insertAndSearch() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Album albumForAddInDb = new Album(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), artistService.addAllMusicArtist(fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), statisticService, context).get(0), statisticService.createStatistic());
            albumDao.insert(albumForAddInDb);
        }

        List<Album> result1 = albumDao.search("mystic");
        List<Album> result2 = albumDao.search("blue");
        List<Album> result3 = albumDao.search("or");

        assertEquals(1, result1.size());
        assertEquals(2, result2.size());
        assertEquals(1, result3.size());
        assertEquals("Mystic Rhythms", result1.get(0).name);
        assertEquals("Blue Horizons", result2.get(0).name);
        assertEquals("Blue Horizons", result3.get(0).name);
    }

    @Test
    public void insertAndGetByArtist(){
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Album albumForAddInDb = new Album(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), artistService.addAllMusicArtist(fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), statisticService, context).get(0), statisticService.createStatistic());
            albumDao.insert(albumForAddInDb);
        }

        Artist resultArtist = artistDao.getByName("Lorien Testard");
        List<Album> result = albumDao.getAllByArtist(resultArtist.id);

        assertEquals(8, resultArtist.id);
        assertEquals("Lorien Testard", resultArtist.name);
        assertEquals(2, result.size());
        assertEquals("HyperSpeed", result.get(0).name);
    }

    @Test
    public void addAndGetArtistWithService() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            long idAlbum = albumService.add(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), artistService, statisticService, context);
            String string = String.format("ID : %s, artist : %s", String.valueOf(idAlbum), artistDao.getById(albumDao.getById(idAlbum).idArtist).name);
            Log.d(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), string);
        }

        try {
            List<Album> result1 = LiveDataTestUtil.getOrAwaitValue(albumDao.getAllLive());
            System.out.println(result1);
            assertEquals(15, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Album> result2 = albumDao.getAll();
        assertEquals(15, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Sunrise Vibes", result2.get(0).name);
        assertEquals("", result2.get(14).name);
    }

    @Test
    public void addAndModifyWithService() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            long idAlbum = albumService.add(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), artistService, statisticService, context);
            String string = String.format("ID : %s, artist : %s", String.valueOf(idAlbum), artistDao.getById(albumDao.getById(idAlbum).idArtist).name);
            Log.d(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), string);
        }
        Album test = albumDao.getById(1);
        albumService.modify(albumDao.getById(1), "new name", "new_cover_path", 1995, "Test", artistService, statisticService, context);

        List<Album> result = albumDao.getAll();
        assertEquals(test.id, result.get(0).id);
        assertNotEquals(test.name, result.get(0).name);
        assertNotEquals(test.coverPath, result.get(0).coverPath);
        assertNotEquals(test.year, result.get(0).year);
        assertNotEquals(artistDao.getById(test.idArtist).name, artistDao.getById(result.get(0).idArtist).name);
        assertEquals("new name", result.get(0).name);
        assertEquals("new_cover_path", result.get(0).coverPath);
        assertEquals(1995, result.get(0).year);
        assertEquals("Test", artistDao.getById(result.get(0).idArtist).name);
    }

    @Test
    public void insertAndIncrementListeningNumber() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            long idAlbum = albumService.add(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), artistService, statisticService, context);
            String string = String.format("ID : %s, artist : %s", String.valueOf(idAlbum), artistDao.getById(albumDao.getById(idAlbum).idArtist).name);
            Log.d(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), string);
        }
        Album albumTest = albumDao.getById(1);
        albumService.incrementListeningNumberStatistic(albumTest, statisticService);

        List<Album> result = albumDao.getAll();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(1, resultStatistic.listeningNumber);
        assertEquals(1, resultStatistic.monthListeningNumber);
    }

    @Test
    public void insertAndIncrementListeningTime() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            long idAlbum = albumService.add(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), artistService, statisticService, context);
            String string = String.format("ID : %s, artist : %s", String.valueOf(idAlbum), artistDao.getById(albumDao.getById(idAlbum).idArtist).name);
            Log.d(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), string);
        }
        Album albumTest = albumDao.getById(1);
        albumService.incrementListeningTimeStatistic(albumTest, statisticService, 750);

        List<Album> result = albumDao.getAll();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(750, resultStatistic.listeningTime);
        assertEquals(750, resultStatistic.monthListeningTime);
    }

    @Test
    public void insertAndIncrementAllListening() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            long idAlbum = albumService.add(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), artistService, statisticService, context);
            String string = String.format("ID : %s, artist : %s", String.valueOf(idAlbum), artistDao.getById(albumDao.getById(idAlbum).idArtist).name);
            Log.d(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), string);
        }
        Album albumTest = albumDao.getById(1);
        albumService.incrementAllListeningStatistic(albumTest, statisticService, 750);

        List<Album> result = albumDao.getAll();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(1, resultStatistic.listeningNumber);
        assertEquals(1, resultStatistic.monthListeningNumber);
        assertEquals(750, resultStatistic.listeningTime);
        assertEquals(750, resultStatistic.monthListeningTime);
    }

    @Test
    public void insertAndIncrementAndReinitialise() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            long idAlbum = albumService.add(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), "album_cover_path", (int)fakeMedia.get(MediaStore.Audio.Media.YEAR), fakeMedia.get(MediaStore.Audio.Media.ALBUM_ARTIST).toString(), artistService, statisticService, context);
            String string = String.format("ID : %s, artist : %s", String.valueOf(idAlbum), artistDao.getById(albumDao.getById(idAlbum).idArtist).name);
            Log.d(fakeMedia.get(MediaStore.Audio.Media.ALBUM).toString(), string);
        }
        Album albumTest = albumDao.getById(1);
        albumService.incrementAllListeningStatistic(albumTest, statisticService, 750);
        albumService.reinitializeMonthValuesStatistic(albumTest, statisticService);

        List<Album> result = albumDao.getAll();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(1, resultStatistic.listeningNumber);
        assertEquals(0, resultStatistic.monthListeningNumber);
        assertEquals(750, resultStatistic.listeningTime);
        assertEquals(0, resultStatistic.monthListeningTime);
    }
}
