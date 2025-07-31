package com.djymini.echoostation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Statistic;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.StatisticService;
import com.djymini.echoostation.testUtilities.BaseTestUtil;
import com.djymini.echoostation.testUtilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ArtistDaoTest {
    private EchooStationDatabase db;
    private ArtistDao artistDao;
    private StatisticDao statisticDao;
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

        artistDao = db.artistDao();
        statisticDao = db.statisticDao();
        artistService = new ArtistService(artistDao, statisticDao);
        statisticService = new StatisticService(statisticDao);

        fakeMediaStoreData = BaseTestUtil.createFakeData();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndGetArtist() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }

        try {
            List<Artist> result1 = LiveDataTestUtil.getOrAwaitValue(artistDao.getAllLive());
            System.out.println(result1);
            assertEquals(15, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Artist> result2 = artistDao.getAll();
        assertEquals(15, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Yoko Shimomura & Yoshitaka Suzuki", result2.get(0).name);
        assertEquals("", result2.get(14).name);
    }

    @Test
    public void insertAndDeleteArtist() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }

        Artist artistToDelete = artistDao.getById(1);
        artistDao.delete(artistToDelete);

        List<Artist> result2 = artistDao.getAll();
        assertEquals(14, result2.size());
        assertEquals(2, result2.get(0).id);
        assertNotEquals("Aurora", result2.get(0).name);
    }

    @Test
    public void insertAndModifyArtistPhoto() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }
        String newPathPhoto = "newPathPhoto";
        artistService.modifyPhoto(artistDao.getById(1), newPathPhoto);


        List<Artist> result = artistDao.getAll();
        assertEquals(newPathPhoto, result.get(0).pathPhoto);
    }

    @Test
    public void insertAndModifyArtistDescription() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }

        String newDescription = "voici la nouvelle description de l'artiste";

        artistService.modifyDescription(artistDao.getById(1), newDescription);

        List<Artist> result = artistDao.getAll();
        assertEquals(newDescription, result.get(0).description);
    }

    @Test
    public void insertAndModifyArtist() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }
        String newPathPhoto = "newPathPhoto";
        String newDescription = "voici la nouvelle description de l'artiste";

        artistDao.update(new Artist(1, "Aurora", newPathPhoto, newDescription, 1));

        List<Artist> result = artistDao.getAll();
        assertEquals(newPathPhoto, result.get(0).pathPhoto);
        assertEquals(newDescription, result.get(0).description);
    }

    @Test
    public void insertAndIncrementListeningNumberArtist() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }
        Artist artistTest = artistDao.getById(1);
        artistService.incrementListeningNumberStatistic(artistTest, statisticService);

        List<Artist> result = artistDao.getAll();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(1, resultStatistic.listeningNumber);
        assertEquals(1, resultStatistic.monthListeningNumber);
    }

    @Test
    public void insertAndIncrementListeningTimeArtist() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }
        Artist artistTest = artistDao.getById(1);
        artistService.incrementListeningTimeStatistic(artistTest, statisticService, 750);

        List<Artist> result = artistDao.getAll();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(750, resultStatistic.listeningTime);
        assertEquals(750, resultStatistic.monthListeningTime);
    }

    @Test
    public void insertAndIncrementAllListeningArtist() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }
        Artist artistTest = artistDao.getById(1);
        artistService.incrementAllListeningStatistic(artistTest, statisticService, 750);

        List<Artist> result = artistDao.getAll();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(1, resultStatistic.listeningNumber);
        assertEquals(1, resultStatistic.monthListeningNumber);
        assertEquals(750, resultStatistic.listeningTime);
        assertEquals(750, resultStatistic.monthListeningTime);
    }

    @Test
    public void insertAndIncrementAndReinitialiseArtist() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }
        Artist artistTest = artistDao.getById(1);
        artistService.incrementAllListeningStatistic(artistTest, statisticService, 750);
        artistService.reinitializeMonthValuesStatistic(artistTest, statisticService);

        List<Artist> result = artistDao.getAll();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(1, resultStatistic.listeningNumber);
        assertEquals(0, resultStatistic.monthListeningNumber);
        assertEquals(750, resultStatistic.listeningTime);
        assertEquals(0, resultStatistic.monthListeningTime);
    }

    @Test
    public void insertAndSearchArtist() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Artist artistForAddInDb = new Artist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), "pathPhoto", "description de l'artiste", statisticService.createStatistic());
            artistDao.insert(artistForAddInDb);
        }

        List<Artist> result1 = artistDao.search("yoko");
        List<Artist> result2 = artistDao.search("utada");
        List<Artist> result3 = artistDao.search("or");
        assertEquals(1, result1.size());
        assertEquals(2, result2.size());
        assertEquals(3, result3.size());
        assertEquals("Yoko Shimomura & Yoshitaka Suzuki", result1.get(0).name);
        assertEquals("Utada", result2.get(0).name);
        assertEquals("The Sax Bros/Aurora", result3.get(0).name);
    }

    @Test
    public void insertAndGetArtistWithService() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            List<Long> listId = artistService.addAllMusicArtist(fakeMedia.get(MediaStore.Audio.Media.ARTIST).toString(), statisticService, context);
            Log.d(fakeMedia.get(MediaStore.Audio.Media.TITLE).toString(), listId.toString());
        }

        try {
            List<Artist> result1 = LiveDataTestUtil.getOrAwaitValue(artistDao.getAllLive());
            for (Artist artist : result1){
                Log.d("TestArtistName", artist.name);
            }
            assertEquals(22, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Artist> result2 = artistDao.getAll();
        assertEquals(22, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Yoko Shimomura", result2.get(0).name);
        assertEquals("Alice Duport-Percier", result2.get(14).name);
    }
}