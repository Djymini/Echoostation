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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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

    public void linkPlaylistWithMusic(long playlistId, long musicId){
        musicDao.insertMusicPlaylist(playlistId, musicId);
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

    public static List<MusicDto> buildMixedPlaylist(List<MusicDto> allMusics) {
        int randomMusicNumber;
        List<MusicDto> workingList = new ArrayList<>(allMusics);
        List<MusicDto> result = new ArrayList<>();

        List<MusicDto> favorites = workingList.stream()
                .filter(m -> m.favoriteMusic)
                .collect(Collectors.toList());
        Collections.shuffle(favorites);
        favorites = favorites.stream().limit(5).collect(Collectors.toList());
        result.addAll(favorites);
        workingList.removeAll(favorites);

        List<MusicDto> mostListened = workingList.stream()
                .sorted(Comparator.comparingInt((MusicDto m) -> m.listeningNumber).reversed())
                .limit(5)
                .collect(Collectors.toList());
        result.addAll(mostListened);
        workingList.removeAll(mostListened);

        List<MusicDto> lestListened = workingList.stream()
                .sorted(Comparator.comparingInt((MusicDto m) -> m.listeningNumber))
                .limit(5)
                .collect(Collectors.toList());
        result.addAll(lestListened);
        workingList.removeAll(lestListened);

        randomMusicNumber = 30 - result.size();

        Collections.shuffle(workingList);
        List<MusicDto> randoms = workingList.stream().limit(randomMusicNumber).collect(Collectors.toList());
        result.addAll(randoms);
        workingList.removeAll(randoms);

        Collections.shuffle(result);

        return result;
    }

    public List<MusicDto> makeGoodVibeMix(){
        List<MusicDto> goodVibeList = musicDao.getMusicByTags(null, true, null, null, null, null, null, null, null, null, null, null, null);

        if(goodVibeList.size() <= 30){
            Collections.shuffle(goodVibeList);
            return goodVibeList;
        }else {
            return buildMixedPlaylist(goodVibeList);
        }
    }

    public List<MusicDto> makeMotivationMix(){
        List<MusicDto> motivationList = musicDao.getMusicByTags(null, null, true, null, null, null, null, null, null, null, null, null, null);

        if(motivationList.size() <= 30){
            Collections.shuffle(motivationList);
            return motivationList;
        }else {
            return buildMixedPlaylist(motivationList);
        }
    }

    public List<MusicDto> makePartyMix(){
        List<MusicDto> partyList = musicDao.getMusicByTags(null, null, null, true, null, null, null, null, null, null, null, null, null);

        if(partyList.size() <= 30){
            Collections.shuffle(partyList);
            return partyList;
        }else {
            return buildMixedPlaylist(partyList);
        }
    }

    public List<MusicDto> makeChillMix(){
        List<MusicDto> chillList = musicDao.getMusicByTags(null, null, null, null, true, null, null, null, null, null, null, null, null);

        if(chillList.size() <= 30){
            Collections.shuffle(chillList);
            return chillList;
        }else {
            return buildMixedPlaylist(chillList);
        }
    }

    public List<MusicDto> makeNightMix(){
        List<MusicDto> nightList = musicDao.getMusicByTags(null, null, null, null, null, true, null, null, null, null, null, null, null);

        if(nightList.size() <= 30){
            Collections.shuffle(nightList);
            return nightList;
        }else {
            return buildMixedPlaylist(nightList);
        }
    }

    public List<MusicDto> makeSadMix(){
        List<MusicDto> sadList = musicDao.getMusicByTags(null, null, null, null, null, null, true, null, null, null, null, null, null);

        if(sadList.size() <= 30){
            Collections.shuffle(sadList);
            return sadList;
        }else {
            return buildMixedPlaylist(sadList);
        }
    }

    public List<MusicDto> makeGamingMix(){
        List<MusicDto> gamingList = musicDao.getMusicByTags(null, null, null, null, null, null, null, true, null, null, null, null, null);

        if(gamingList.size() <= 30){
            Collections.shuffle(gamingList);
            return gamingList;
        }else {
            return buildMixedPlaylist(gamingList);
        }
    }

    public List<MusicDto> makeMorningMix(){
        List<MusicDto> morningList = musicDao.getMusicByTags(null, null, null, null, null, null, null, null, true, null, null, null, null);

        if(morningList.size() <= 30){
            Collections.shuffle(morningList);
            return morningList;
        }else {
            return buildMixedPlaylist(morningList);
        }
    }

    public List<MusicDto> makeWalkMix(){
        List<MusicDto> walkList = musicDao.getMusicByTags(null, null, null, null, null, null, null, null, null, true, null, null, null);

        if(walkList.size() <= 30){
            Collections.shuffle(walkList);
            return walkList;
        }else {
            return buildMixedPlaylist(walkList);
        }
    }

    public List<MusicDto> makeDriveeMix(){
        List<MusicDto> driveList = musicDao.getMusicByTags(null, null, null, null, null, null, null, null, null, null, true, null, null);

        if(driveList.size() <= 30){
            Collections.shuffle(driveList);
            return driveList;
        }else {
            return buildMixedPlaylist(driveList);
        }
    }

    public List<MusicDto> makeWorkMix(){
        List<MusicDto> workList = musicDao.getMusicByTags(null, null, null, null, null, null, null, null, null, null, null, true, null);

        if(workList.size() <= 30){
            Collections.shuffle(workList);
            return workList;
        }else {
            return buildMixedPlaylist(workList);
        }
    }

    public List<MusicDto> makeMindMix(){
        List<MusicDto> mindList = musicDao.getMusicByTags(null, null, null, null, null, null, null, null, null, null, null, null, true);

        if(mindList.size() <= 30){
            Collections.shuffle(mindList);
            return mindList;
        }else {
            return buildMixedPlaylist(mindList);
        }
    }
}
