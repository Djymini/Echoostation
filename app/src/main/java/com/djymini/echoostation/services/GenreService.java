package com.djymini.echoostation.services;

import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Genre;
import com.djymini.echoostation.entities.Statistic;

public class GenreService {
    private GenreDao genreDao;
    private StatisticDao statisticDao;

    public GenreService(GenreDao genreDao, StatisticDao statisticDao) {
        this.genreDao = genreDao;
        this.statisticDao = statisticDao;
    }

    // TODO: Faire la gestion de plusieurs genre sur une musique

    public void incrementListeningNumberGenre(Genre genre, StatisticService statisticService){
        long idGenre = genre.id;
        long idStatistic = genre.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(genreDao.existsById(idGenre)){
            statisticService.incrementListeningNumber(statistic);
        }
    }

    public void incrementListeningTimeGenre(Genre genre, StatisticService statisticService, long time){
        long idGenre = genre.id;
        long idStatistic = genre.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(genreDao.existsById(idGenre)){
            statisticService.incrementListeningTime(statistic, time);
        }
    }

    public void incrementAllListeningGenre(Genre genre, StatisticService statisticService, long time){
        long idGenre = genre.id;
        long idStatistic = genre.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(genreDao.existsById(idGenre)){
            statisticService.incrementAllListening(statistic, time);
        }
    }

    public void reinitializeMonthValuesGenre(Genre genre, StatisticService statisticService){
        long idGenre = genre.id;
        long idStatistic = genre.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(genreDao.existsById(idGenre)){
            statisticService.reinitializeMonthValues(statistic);
        }
    }
}
