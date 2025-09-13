package com.djymini.echoostation.services;

import android.util.Log;

import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.MusicTagDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.entities.MusicTag;
import com.djymini.echoostation.entities.Statistic;
import com.djymini.echoostation.helpers.StatisticHelper;
import com.djymini.echoostation.utilities.TimeUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicService {
    private static final String TAG = "MusicService";
    private final MusicDao musicDao;
    private final MusicTagDao musicTagDao;
    private final StatisticHelper<Music> statisticHelper;

    public MusicService(MusicDao musicDao, MusicTagDao musicTagDao, StatisticDao statisticDao, StatisticService statisticService) {
        this.musicDao = musicDao;
        this.musicTagDao = musicTagDao;
        this.statisticHelper = new StatisticHelper<>(
                statisticDao,
                statisticService,
                musicDao::existsById,
                music -> music.statisticId
        );
    }

    public long add(String musicPath, String musicTitle, long musicDuration, int musicTrack, String artistName, long albumId, long genreId, ArtistService artistService, StatisticService statisticService){
        try{
            List<Long> artistIds = artistService.addAllArtist(artistName, statisticService);

            if (musicTitle == null || musicTitle.isEmpty()){
                musicTitle = getNameFile(musicPath);
            }

            if(!musicDao.existsByPath(musicPath)){
                long statisticId = statisticService.createStatistic();
                MusicTag musicTag = new MusicTag();
                long musicTagId = musicTagDao.insert(musicTag);
                Music musicForAddInDb = new Music(musicPath, musicTitle, musicDuration, musicTrack, albumId, genreId, musicTagId, statisticId);
                long musicId = musicDao.insert(musicForAddInDb);
                linkArtistWithMusic(artistIds, musicId);

                return musicId;
            }else {
                return musicDao.getByPath(musicPath).id;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'ajout de la musique", e);
            return -1;
        }
    }

    private String getNameFile(String musicPath){
        String[] newString = musicPath.split("/");
        return newString[newString.length-1];
    }

    public void modify(Music currentMusic, String newTitle, int newTrack, long newAlbumId, long newGenreId, String artistName, ArtistService artistService, StatisticService statisticService){
        if (currentMusic == null) {
            Log.e(TAG, "musique est null");
            return;
        }

        try{
            Music musicModified = new Music(currentMusic, newTitle, newTrack, newAlbumId, newGenreId);

            if(musicDao.existsById(currentMusic.id)){
                musicDao.deleteArtistMusicByMusicId(musicModified.id);
                List<Long> artistIds = artistService.addAllArtist(artistName, statisticService);
                musicDao.update(musicModified);

                linkArtistWithMusic(artistIds, musicModified.id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération de la couverture", e);
        }
    }

    private void linkArtistWithMusic(List<Long> artistIds, long musicId){
        for (int i = 0; i < artistIds.size(); i++) {
            musicDao.insertArtistMusic(artistIds.get(i), musicId, i);
        }
    }

    public void incrementListeningNumberStatistic(Music music){
        statisticHelper.incrementListeningNumber(music, music.id);
        musicDao.updateLastPlay(music.id, TimeUtilities.currentTimeMillis());
    }

    public void incrementListeningTimeStatistic(Music music, long time){
        statisticHelper.incrementListeningTime(music, music.id, time);
    }

    public void incrementAllListeningStatistic(Music music, long time){
        statisticHelper.incrementAllListening(music, music.id, time);
    }

    public void reinitializeMonthValuesStatistic(Music music) {
        statisticHelper.reinitializeMonthValues(music, music.id);
    }
}
