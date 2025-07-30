package com.djymini.echoostation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.provider.MediaStore;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Genre;
import com.djymini.echoostation.entities.Statistic;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.StatisticService;
import com.djymini.echoostation.testUtilities.BaseTestUtil;
import com.djymini.echoostation.testUtilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class GenreDaoTest {
    private EchooStationDatabase db;
    private GenreDao genreDao;
    private StatisticDao statisticDao;
    private GenreService genreService;
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

        genreDao = db.genreDao();
        statisticDao = db.statisticDao();
        genreService = new GenreService(genreDao, statisticDao);
        statisticService = new StatisticService(statisticDao);

        fakeMediaStoreData = BaseTestUtil.createFakeData();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndGetGenre() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Genre genreForAddInDb = new Genre(fakeMedia.get(MediaStore.Audio.Media.GENRE).toString(), statisticService.createStatistic());
            genreDao.insert(genreForAddInDb);
        }

        try {
            List<Genre> result1 = LiveDataTestUtil.getOrAwaitValue(genreDao.getAllGenreLive());
            System.out.println(result1);
            assertEquals(15, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Genre> result2 = genreDao.getAllGenre();
        assertEquals(15, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Aurora", result2.get(0).name);
        assertEquals("Stack Overflow; GitHub", result2.get(14).name);
    }

    @Test
    public void insertAndDeleteGenre() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Genre genreForAddInDb = new Genre(fakeMedia.get(MediaStore.Audio.Media.GENRE).toString(), statisticService.createStatistic());
            genreDao.insert(genreForAddInDb);
        }

        Genre genreToDelete = genreDao.getById(1);
        genreDao.delete(genreToDelete);

        List<Genre> result2 = genreDao.getAllGenre();
        assertEquals(14, result2.size());
        assertEquals(2, result2.get(0).id);
        assertNotEquals("Aurora", result2.get(0).name);
    }

    @Test
    public void insertAndModifyGenre() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Genre genreForAddInDb = new Genre(fakeMedia.get(MediaStore.Audio.Media.GENRE).toString(), statisticService.createStatistic());
            genreDao.insert(genreForAddInDb);
        }
        String newPathPhoto = "newPathPhoto";
        String newDescription = "voici la nouvelle description de l'artiste";

        genreDao.update(new Genre(1, "Dance", 1));

        List<Genre> result = genreDao.getAllGenre();
        assertEquals(newPathPhoto, result.get(0).name);
    }

    @Test
    public void insertAndIncrementListeningNumberGenre() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Genre genreForAddInDb = new Genre(fakeMedia.get(MediaStore.Audio.Media.GENRE).toString(), statisticService.createStatistic());
            genreDao.insert(genreForAddInDb);
        }
        Genre genreTest = genreDao.getById(1);
        genreService.incrementListeningNumberGenre(genreTest, statisticService);

        List<Genre> result = genreDao.getAllGenre();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(1, resultStatistic.listeningNumber);
        assertEquals(1, resultStatistic.monthListeningNumber);
    }

    @Test
    public void insertAndIncrementListeningTimeGenre() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Genre genreForAddInDb = new Genre(fakeMedia.get(MediaStore.Audio.Media.GENRE).toString(), statisticService.createStatistic());
            genreDao.insert(genreForAddInDb);
        }
        Genre genreTest = genreDao.getById(1);
        genreService.incrementListeningTimeGenre(genreTest, statisticService, 750);

        List<Genre> result = genreDao.getAllGenre();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(750, resultStatistic.listeningTime);
        assertEquals(750, resultStatistic.monthListeningTime);
    }

    @Test
    public void insertAndIncrementAllListeningGenre() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Genre genreForAddInDb = new Genre(fakeMedia.get(MediaStore.Audio.Media.GENRE).toString(), statisticService.createStatistic());
            genreDao.insert(genreForAddInDb);
        }
        Genre genreTest = genreDao.getById(1);
        genreService.incrementAllListeningGenre(genreTest, statisticService, 750);

        List<Genre> result = genreDao.getAllGenre();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(1, resultStatistic.listeningNumber);
        assertEquals(1, resultStatistic.monthListeningNumber);
        assertEquals(750, resultStatistic.listeningTime);
        assertEquals(750, resultStatistic.monthListeningTime);
    }

    @Test
    public void insertAndIncrementAndReinitialiseGenre() {
        for (Map<String, Object> fakeMedia : fakeMediaStoreData){
            Genre genreForAddInDb = new Genre(fakeMedia.get(MediaStore.Audio.Media.GENRE).toString(), statisticService.createStatistic());
            genreDao.insert(genreForAddInDb);
        }
        Genre genreTest = genreDao.getById(1);
        genreService.incrementAllListeningGenre(genreTest, statisticService, 750);
        genreService.reinitializeMonthValuesGenre(genreTest, statisticService);

        List<Genre> result = genreDao.getAllGenre();
        Statistic resultStatistic = statisticDao.getById(result.get(0).idStatistic);
        assertEquals(1, resultStatistic.listeningNumber);
        assertEquals(0, resultStatistic.monthListeningNumber);
        assertEquals(750, resultStatistic.listeningTime);
        assertEquals(0, resultStatistic.monthListeningTime);
    }

    //TODO : Make the all test
}
