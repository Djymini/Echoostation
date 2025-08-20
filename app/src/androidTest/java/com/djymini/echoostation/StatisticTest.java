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

@RunWith(AndroidJUnit4.class)
public class StatisticTest {
    private EchooStationDatabase db;
    private StatisticDao statisticDao;
    private StatisticService statisticService;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, EchooStationDatabase.class)
                .allowMainThreadQueries()
                .build();

        statisticDao = db.statisticDao();
        statisticService = new StatisticService(statisticDao);
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

        statisticDao.insert(statistic0);
        statisticDao.insert(statistic1);
        statisticDao.insert(statistic2);

        List<Statistic> result = statisticDao.getAll();
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).id);
    }

    @Test
    public void insertAndDeleteStatistic() {
        Statistic statistic = new Statistic(0,0, 0, 0);

        statisticDao.insert(statistic);
        List<Statistic> list = statisticDao.getAll();
        statisticDao.delete(list.get(0));

        List<Statistic> result = statisticDao.getAll();
        assertEquals(0, result.size());
    }

    @Test
    public void insertAndCheckExistence() {
        Statistic statistic0 = new Statistic(0,0, 0, 0);
        statisticDao.insert(statistic0);
        assertTrue(statisticDao.existsById(1));
    }

    @Test
    public void insertAndUpdateStatistic() {
        Statistic statistic = new Statistic(0,0, 0, 0);
        statisticDao.insert(statistic);
        Statistic result1 = statisticDao.getById(1);

        statisticService.updateStatistic(result1.id, 12, 15, 0, 16);
        Statistic result2 = statisticDao.getById(1);

        assertEquals(1, result1.id);
        assertEquals(0, result1.listeningNumber);
        assertEquals(1, result2.id);
        assertEquals(12, result2.listeningNumber);
    }

    @Test
    public void insertAndIncremanteListeningNumber() {
        Statistic statistic = new Statistic(0,0, 0, 0);
        statisticDao.insert(statistic);
        Statistic result1 = statisticDao.getById(1);

        statisticService.incrementListeningNumber(result1);
        Statistic result2 = statisticDao.getById(1);

        assertEquals(1, result1.id);
        assertEquals(0, result1.listeningNumber);
        assertEquals(1, result2.id);
        assertEquals(1, result2.listeningNumber);
        assertEquals(1, result2.monthListeningNumber);
    }

    @Test
    public void insertAndIncremanteListeningTime() {
        Statistic statistic = new Statistic(0,0, 0, 0);
        statisticDao.insert(statistic);
        Statistic result1 = statisticDao.getById(1);

        statisticService.incrementListeningTime(result1, 750);
        Statistic result2 = statisticDao.getById(1);

        assertEquals(1, result1.id);
        assertEquals(0, result1.listeningNumber);
        assertEquals(1, result2.id);
        assertEquals(750, result2.listeningTime);
        assertEquals(750, result2.monthListeningTime);
    }

    @Test
    public void insertAndIncremanteAllListening() {
        Statistic statistic = new Statistic(0,0, 0, 0);
        statisticDao.insert(statistic);
        Statistic result1 = statisticDao.getById(1);

        statisticService.incrementAllListening(result1, 750);
        Statistic result2 = statisticDao.getById(1);

        assertEquals(1, result1.id);
        assertEquals(0, result1.listeningNumber);
        assertEquals(1, result2.id);
        assertEquals(1, result2.listeningNumber);
        assertEquals(1, result2.monthListeningNumber);
        assertEquals(750, result2.listeningTime);
        assertEquals(750, result2.monthListeningTime);
    }

    @Test
    public void insertAndReinitializeMonthListening() {
        Statistic statistic = new Statistic(15,15, 1500, 1500);
        statisticDao.insert(statistic);
        Statistic result1 = statisticDao.getById(1);

        statisticService.reinitializeMonthValues(result1);
        Statistic result2 = statisticDao.getById(1);

        assertEquals(1, result1.id);
        assertEquals(15, result1.listeningNumber);
        assertEquals(1, result2.id);
        assertEquals(15, result2.listeningNumber);
        assertEquals(0, result2.monthListeningNumber);
        assertEquals(1500, result2.listeningTime);
        assertEquals(0, result2.monthListeningTime);
    }
}
