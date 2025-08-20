package com.djymini.echoostation.helpers;

import android.util.Log;

import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Statistic;
import com.djymini.echoostation.services.StatisticService;

public class StatisticHelper<T> {
    private static final String TAG = "StatisticHelper";
    private final StatisticDao statisticDao;
    private final StatisticService statisticService;
    private final EntityExistenceChecker<T> existenceChecker;
    private final StatisticIdProvider<T> statisticIdProvider;

    @FunctionalInterface
    public interface EntityExistenceChecker<T> {
        boolean existsById(long id);
    }

    @FunctionalInterface
    public interface StatisticIdProvider<T> {
        long getStatisticId(T entity);
    }

    public StatisticHelper(StatisticDao statisticDao, StatisticService statisticService,
                           EntityExistenceChecker<T> existenceChecker, StatisticIdProvider<T> statisticIdProvider) {
        this.statisticDao = statisticDao;
        this.statisticService = statisticService;
        this.existenceChecker = existenceChecker;
        this.statisticIdProvider = statisticIdProvider;
    }

    private Statistic getValidStatistic(T entity, long entityId) {
        if (entity == null) {
            Log.e(TAG, "L'entité est null");
            return null;
        }
        if (!existenceChecker.existsById(entityId)) {
            Log.w(TAG, "L'entité avec id " + entityId + " n'existe pas.");
            return null;
        }
        long statisticId = statisticIdProvider.getStatisticId(entity);
        Statistic statistic = statisticDao.getById(statisticId);
        if (statistic == null) {
            Log.w(TAG, "La statistique avec id " + statisticId + " n'existe pas.");
        }
        return statistic;
    }

    public void incrementListeningNumber(T entity, long entityId) {
        Statistic statistic = getValidStatistic(entity, entityId);
        if (statistic != null) {
            statisticService.incrementListeningNumber(statistic);
        }
    }

    public void incrementListeningTime(T entity, long entityId, long time) {
        Statistic statistic = getValidStatistic(entity, entityId);
        if (statistic != null) {
            statisticService.incrementListeningTime(statistic, time);
        }
    }

    public void incrementAllListening(T entity, long entityId, long time) {
        Statistic statistic = getValidStatistic(entity, entityId);
        if (statistic != null) {
            statisticService.incrementAllListening(statistic, time);
        }
    }

    public void reinitializeMonthValues(T entity, long entityId) {
        Statistic statistic = getValidStatistic(entity, entityId);
        if (statistic != null) {
            statisticService.reinitializeMonthValues(statistic);
        }
    }
}

