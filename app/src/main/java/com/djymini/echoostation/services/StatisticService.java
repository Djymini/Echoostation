package com.djymini.echoostation.services;

import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Statistic;

public class StatisticService {
    private StatisticDao statisticDao;

    public StatisticService(StatisticDao statisticDao) {
        this.statisticDao = statisticDao;
    }

    public void updateStatistic(int idOfStatistic, int newListeningNumber, int newMonthListeningNumber, long newListeningTime, long newMonthListeningTime){
        if(statisticDao.existsById(idOfStatistic)){
            Statistic statisticForUpdate = new Statistic(idOfStatistic, newListeningNumber, newMonthListeningNumber, newListeningTime, newMonthListeningTime);
            statisticDao.update(statisticForUpdate);
        }
    }
}
