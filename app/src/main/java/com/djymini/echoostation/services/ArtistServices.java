package com.djymini.echoostation.services;

import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Statistic;

public class ArtistServices {
    private ArtistDao artistDao;
    private StatisticDao statisticDao;

    public ArtistServices(ArtistDao artistDao, StatisticDao statisticDao) {
        this.artistDao = artistDao;
        this.statisticDao = statisticDao;
    }

    // TODO: Faire la gestion de plusieurs artiste sur une musique

    public void modifyPhoto(Artist artist, String newPhotoPath){
        if(artistDao.existsById(artist.id)){
            Artist artistForUpdate = new Artist(artist.id,artist.name, newPhotoPath, artist.description, artist.idStatistic);
            artistDao.update(artistForUpdate);
        }
    }

    public void modifyDescription(Artist artist, String newDescription){
        if(artistDao.existsById(artist.id)){
            Artist artistForUpdate = new Artist(artist.id,artist.name, artist.pathPhoto, newDescription, artist.idStatistic);
            artistDao.update(artistForUpdate);
        }
    }

    public void incrementListeningNumberArtist(Artist artist, StatisticService statisticService){
        long idArtist = artist.id;
        long idStatistic = artist.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(artistDao.existsById(idArtist)){
            statisticService.incrementListeningNumber(statistic);
        }
    }

    public void incrementListeningTimeArtist(Artist artist, StatisticService statisticService, long time){
        long idArtist = artist.id;
        long idStatistic = artist.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(artistDao.existsById(idArtist)){
            statisticService.incrementListeningTime(statistic, time);
        }
    }

    public void incrementAllListeningArtist(Artist artist, StatisticService statisticService, long time){
        long idArtist = artist.id;
        long idStatistic = artist.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(artistDao.existsById(idArtist)){
            statisticService.incrementAllListening(statistic, time);
        }
    }

    public void reinitializeMonthValuesArtist(Artist artist, StatisticService statisticService){
        long idArtist = artist.id;
        long idStatistic = artist.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(artistDao.existsById(idArtist)){
            statisticService.reinitializeMonthValues(statistic);
        }
    }
}
