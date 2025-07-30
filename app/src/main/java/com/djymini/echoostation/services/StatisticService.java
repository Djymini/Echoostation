package com.djymini.echoostation.services;

import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Statistic;

public class StatisticService {
    private StatisticDao statisticDao;

    public StatisticService(StatisticDao statisticDao) {
        this.statisticDao = statisticDao;
    }

    public long createStatistic(){
        Statistic statistic = new Statistic(0,0, 0, 0);
        return statisticDao.insert(statistic);
    }

    public void updateStatistic(long idOfStatistic, int newListeningNumber, int newMonthListeningNumber, long newListeningTime, long newMonthListeningTime){
        if(statisticDao.existsById(idOfStatistic)){
            Statistic statisticForUpdate = new Statistic(idOfStatistic, newListeningNumber, newMonthListeningNumber, newListeningTime, newMonthListeningTime);
            statisticDao.update(statisticForUpdate);
        }
    }

    public void incrementListeningNumber(Statistic statistic){
        if(statisticDao.existsById(statistic.id)){
            Statistic statisticForUpdate = new Statistic(statistic.id, statistic.listeningNumber+1, statistic.monthListeningNumber+1, statistic.listeningTime, statistic.monthListeningTime);
            statisticDao.update(statisticForUpdate);
        }
    }

    public void incrementListeningTime(Statistic statistic, long time){
        if(statisticDao.existsById(statistic.id)){
            Statistic statisticForUpdate = new Statistic(statistic.id, statistic.listeningNumber, statistic.monthListeningNumber, statistic.listeningTime + time, statistic.monthListeningTime + time);
            statisticDao.update(statisticForUpdate);
        }
    }

    public void incrementAllListening(Statistic statistic, long time){
        if(statisticDao.existsById(statistic.id)){
            Statistic statisticForUpdate = new Statistic(statistic.id, statistic.listeningNumber + 1, statistic.monthListeningNumber + 1, statistic.listeningTime + time, statistic.monthListeningTime + time);
            statisticDao.update(statisticForUpdate);
        }
    }

    public void reinitializeMonthValues(Statistic statistic){
        if(statisticDao.existsById(statistic.id)){
            Statistic statisticForUpdate = new Statistic(statistic.id, statistic.listeningNumber, 0, statistic.listeningTime, 0);
            statisticDao.update(statisticForUpdate);
        }
    }
}
