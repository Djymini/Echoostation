package com.djymini.echoostation.services;

import android.content.Context;
import android.net.Uri;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.entities.Statistic;
import com.djymini.echoostation.utilities.Constants;

public class AlbumService {
    private AlbumDao albumDao;
    private StatisticDao statisticDao;

    public AlbumService(AlbumDao albumDao, StatisticDao statisticDao) {
        this.albumDao = albumDao;
        this.statisticDao = statisticDao;
    }

    public long add(String albumName,String coverPath,int year, String artistName, ArtistService artistService, StatisticService statisticService, Context context){
        long idAlbum;
        long idArtist = artistService.addAllArtist(artistName, statisticService, context).get(0);

        if (albumName == "" || albumName == null)
            albumName = Constants.UNKNOWN_ALBUM;

        if(!albumDao.existsByNameAndArtist(albumName, idArtist)){
            Album albumForAddInDb = new Album(albumName, coverPath, year, idArtist, statisticService.createStatistic());
            idAlbum = albumDao.insert(albumForAddInDb);
        }else {
            idAlbum = albumDao.getByNameAndArtist(albumName, idArtist).id;
        }

        return idAlbum;
    }

    public Uri getCover(long idAlbum){
        return Uri.parse(albumDao.getById(idAlbum).coverPath);
    }

    public void modifyCover(Album album, String newCoverPath){
        if(albumDao.existsById(album.id)){
            Album albumForUpdate = new Album(album.id, album.name, newCoverPath, album.year, album.idArtist, album.idStatistic);
            albumDao.update(albumForUpdate);
        }
    }

    /*
    public void modifyName(Album album, String newName){
        if(!albumDao.existsById(album.id))
            return;

        if(albumDao.existsByNameAndArtist(newName, album.idArtist)){
            //TODO: Change the idAlbum of music for exitent album
            albumDao.delete(album);
        }
        else{
            Album albumForUpdate = new Album(album.id, newName, album.coverPath, album.year, album.idArtist, album.idStatistic);
            albumDao.update(albumForUpdate);
        }
    }

    public void modifyYear(Album album, int newYear){
        if(albumDao.existsById(album.id)){
            Album albumForUpdate = new Album(album.id, album.name, album.coverPath, newYear, album.idArtist, album.idStatistic);
            albumDao.update(albumForUpdate);
        }
    }

    public void modifyArtist(Album album, String newArtistName, ArtistService artistService, StatisticService statisticService, Context context){
        if(!albumDao.existsById(album.id))
            return;

        long idArtist = artistService.addAllArtist(newArtistName, statisticService, context).get(0);
        Album albumForUpdate = new Album(album.id, album.name, album.coverPath, album.year, idArtist, album.idStatistic);
        albumDao.update(albumForUpdate);
    }

    public void modify(Album album, String newName, String newCoverPath, int newYear, String newArtistName, ArtistService artistService, StatisticService statisticService, Context context){
        if(!albumDao.existsById(album.id))
            return;

        long idArtist = artistService.addAllArtist(newArtistName, statisticService, context).get(0);

        if(albumDao.existsByNameAndArtist(newName, idArtist)){
            //TODO: Change the idAlbum of music for exitent album
            albumDao.delete(album);
        }
        else{
            Album albumForUpdate = new Album(album.id, newName, newCoverPath, newYear, idArtist, album.idStatistic);
            albumDao.update(albumForUpdate);
        }

    }

    public void incrementListeningNumberStatistic(Album album, StatisticService statisticService){
        long idArtist = album.id;
        long idStatistic = album.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(albumDao.existsById(idArtist)){
            statisticService.incrementListeningNumber(statistic);
        }
    }

    public void incrementListeningTimeStatistic(Album album, StatisticService statisticService, long time){
        long idArtist = album.id;
        long idStatistic = album.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(albumDao.existsById(idArtist)){
            statisticService.incrementListeningTime(statistic, time);
        }
    }

    public void incrementAllListeningStatistic(Album album, StatisticService statisticService, long time){
        long idArtist = album.id;
        long idStatistic = album.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(albumDao.existsById(idArtist)){
            statisticService.incrementAllListening(statistic, time);
        }
    }

    public void reinitializeMonthValuesStatistic(Album album, StatisticService statisticService){
        long idArtist = album.id;
        long idStatistic = album.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(albumDao.existsById(idArtist)){
            statisticService.reinitializeMonthValues(statistic);
        }
    }*/
}
