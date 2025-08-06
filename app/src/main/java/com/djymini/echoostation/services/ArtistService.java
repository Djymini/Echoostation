package com.djymini.echoostation.services;

import android.content.Context;

import com.djymini.echoostation.R;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Artist;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArtistService {
    private ArtistDao artistDao;
    private StatisticDao statisticDao;

    public ArtistService(ArtistDao artistDao, StatisticDao statisticDao) {
        this.artistDao = artistDao;
        this.statisticDao = statisticDao;
    }

    public List<Long> addAllArtist(String artistName, StatisticService statisticService, Context context){
        List<Long> listIdArtist = new ArrayList<Long>();
        String[] artistArray;
        if (artistName != null) {
            artistArray = separateArtist(artistName);
        }
        else{
            artistArray = new String[] {""};
        }

        for (String artist : artistArray) {
            String nameCheck = fixNameArtist(artist, context);
            if(!artistDao.existsByName(nameCheck)){
                Artist artistForAddInDb = new Artist(nameCheck, "", "", statisticService.createStatistic());
                listIdArtist.add(artistDao.insert(artistForAddInDb));
            }else {
                ;
                listIdArtist.add(artistDao.getByName(nameCheck).id);
            }
        }

        return listIdArtist;
    }

    private String[] separateArtist(String artistName){
        String regex = "(?<!/)(?<!/)(?:,\\s&\\s|,\\s|;\\s|\\s&\\s|\\sfeat\\.\\s|\\sand\\s|/)(?!/)(?!/)";
        return artistName.split(regex);
    }

    private String fixNameArtist(String artistName, Context context){
        InputStream inputStream = context.getResources().openRawResource(R.raw.artist_name_aliases);
        String jsonString = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> nameAliases = gson.fromJson(jsonString, type);

        if(nameAliases.get(artistName) != null){
            return nameAliases.get(artistName);
        }
        else {
            return artistName;
        }
    }

    public String getArtistsNameOfMusic(long idMusic){
        List<Artist> artistList = artistDao.getAllByMusic(idMusic);
        List<String> nameList = new ArrayList<>();

        for (Artist artist : artistList){
            nameList.add(artist.nameArtist);
        }

        return String.join(", ", nameList);
    }

    /*
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

    public void incrementListeningNumberStatistic(Artist artist, StatisticService statisticService){
        long idArtist = artist.id;
        long idStatistic = artist.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(artistDao.existsById(idArtist)){
            statisticService.incrementListeningNumber(statistic);
        }
    }

    public void incrementListeningTimeStatistic(Artist artist, StatisticService statisticService, long time){
        long idArtist = artist.id;
        long idStatistic = artist.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(artistDao.existsById(idArtist)){
            statisticService.incrementListeningTime(statistic, time);
        }
    }

    public void incrementAllListeningStatistic(Artist artist, StatisticService statisticService, long time){
        long idArtist = artist.id;
        long idStatistic = artist.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(artistDao.existsById(idArtist)){
            statisticService.incrementAllListening(statistic, time);
        }
    }

    public void reinitializeMonthValuesStatistic(Artist artist, StatisticService statisticService){
        long idArtist = artist.id;
        long idStatistic = artist.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(artistDao.existsById(idArtist)){
            statisticService.reinitializeMonthValues(statistic);
        }
    }*/
}
