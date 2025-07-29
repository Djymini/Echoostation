package com.djymini.echoostation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Statistic;
import com.djymini.echoostation.services.StatisticService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class StatisticDaoTest {
    private EchooStationDatabase db;
    private StatisticDao statisticDao;
    private StatisticService statisticService;

    private List<Map<String, Object>> fakeMediaStoreData;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, EchooStationDatabase.class)
                .allowMainThreadQueries()
                .build();

        statisticDao = db.statisticDao();
        statisticService = new StatisticService(statisticDao);

        fakeMediaStoreData = BaseForTest.createFakeData();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndGetStatistic() {
        Statistic statistic0 = new Statistic(0,0, 0, 0);
        Statistic statistic1 = new Statistic(0,0, 0, 0);
        Statistic statistic2 = new Statistic(0,0, 0, 0);

        statisticDao.insertAll(statistic0);
        statisticDao.insertAll(statistic1);
        statisticDao.insertAll(statistic2);

        List<Statistic> result = statisticDao.getAll();
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).id);
    }

    @Test
    public void insertAndDeleteStatistic() {
        Statistic statistic = new Statistic(0,0, 0, 0);

        statisticDao.insertAll(statistic);
        List<Statistic> list = statisticDao.getAll();
        statisticDao.delete(list.get(0));

        List<Statistic> result = statisticDao.getAll();
        assertEquals(0, result.size());
    }

    @Test
    public void insertAndCheckExistence() {
        Statistic statistic0 = new Statistic(0,0, 0, 0);
        statisticDao.insertAll(statistic0);
        assertTrue(statisticDao.existsById(1));
    }

    @Test
    public void insertAndUpdateStatistic() {
        Statistic statistic = new Statistic(0,0, 0, 0);
        statisticDao.insertAll(statistic);
        Statistic result1 = statisticDao.getById(1);

        statisticService.updateStatistic(result1.id, 12, 15, 0, 16);
        Statistic result2 = statisticDao.getById(1);

        assertEquals(1, result1.id);
        assertEquals(0, result1.listeningNumber);
        assertEquals(1, result2.id);
        assertEquals(12, result2.listeningNumber);
    }
}
