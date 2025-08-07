package com.djymini.echoostation.services;

import android.content.Context;

import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.entities.Statistic;

import java.util.List;

public class MusicService {
    private MusicDao musicDao;
    private StatisticDao statisticDao;

    public MusicService(MusicDao musicDao, StatisticDao statisticDao) {
        this.musicDao = musicDao;
        this.statisticDao = statisticDao;
    }

    public long add(String musicPath, String musicTitle, long musicDuration, int musicTrack, String artistName, long idAlbum, long idGenre, ArtistService artistService, StatisticService statisticService, Context context){
        long idMusic;
        List<Long> idArtists = artistService.addAllArtist(artistName, statisticService, context);
        if (musicTitle == "" || musicTitle == null)
            musicTitle = getNameFile(musicPath);

        if(!musicDao.existsByPath(musicPath)){
            Music musicForAddInDb = new Music(musicPath, musicTitle, musicDuration, musicTrack, false, idAlbum, idGenre, statisticService.createStatistic());
            idMusic = musicDao.insert(musicForAddInDb);
            for (long idArtist : idArtists)
                musicDao.insertArtistMusic(idArtist, idMusic);
        }else {
            idMusic = musicDao.getByPath(musicPath).id;
        }

        return idMusic;
    }

    private String getNameFile(String musicPath){
        String[] newString = musicPath.split("/");
        return newString[newString.length-1];
    }

    public void modify(Music currentMusic, String newTitle, int newTrack, long newIdAlbum, long newIdGenre, String artistName, ArtistService artistService, StatisticService statisticService, Context context){
        Music musicModified = new Music(currentMusic, newTitle, newTrack, newIdAlbum, newIdGenre);
        if(musicDao.existsById(currentMusic.id)){
            musicDao.deleteArtistMusicByMusicId(musicModified.id);
            List<Long> idArtists = artistService.addAllArtist(artistName, statisticService, context);
            musicDao.update(musicModified);

            for (long idArtist : idArtists)
                musicDao.insertArtistMusic(idArtist, musicModified.id);
        }
    }

    /*
    public void incrementListeningNumberStatistic(Music music, StatisticService statisticService){
        long idMusic = music.id;
        long idStatistic = music.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(musicDao.existsById(idMusic)){
            statisticService.incrementListeningNumber(statistic);
        }
    }

    public void incrementListeningTimeStatistic(Music music, StatisticService statisticService, long time){
        long idMusic = music.id;
        long idStatistic = music.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(musicDao.existsById(idMusic)){
            statisticService.incrementListeningTime(statistic, time);
        }
    }

    public void incrementAllListeningStatistic(Music music, StatisticService statisticService, long time){
        long idMusic = music.id;
        long idStatistic = music.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(musicDao.existsById(idMusic)){
            statisticService.incrementAllListening(statistic, time);
        }
    }

    public void reinitializeMonthValuesStatistic(Music music, StatisticService statisticService){
        long idMusic = music.id;
        long idStatistic = music.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(musicDao.existsById(idMusic)){
            statisticService.reinitializeMonthValues(statistic);
        }
    }
    */
}
