package com.djymini.echoostation.services;

import android.content.Context;
import android.util.Log;

import com.djymini.echoostation.R;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.helpers.StatisticHelper;
import com.djymini.echoostation.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArtistService {
    private static final String TAG = "ArtistService";
    private final ArtistDao artistDao;
    private final StatisticHelper<Artist> statisticHelper;
    private Map<String, String> nameAliases;

    public ArtistService(ArtistDao artistDao, StatisticDao statisticDao, StatisticService statisticService, Context context) {
        this.artistDao = artistDao;
        this.statisticHelper = new StatisticHelper<>(
                statisticDao,
                statisticService,
                artistDao::existsById,
                artist -> artist.statisticId
        );
        loadNameAliases(context);
    }

    private void loadNameAliases(Context context) {
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.artist_name_aliases);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String jsonString = reader.lines().collect(Collectors.joining("\n"));
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            nameAliases = gson.fromJson(jsonString, type);
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement des alias d'artistes", e);
            nameAliases = new HashMap<>();
        }
    }

    public List<Long> addAllArtist(String artistName, StatisticService statisticService){
        try {
            List<Long> listArtistId = new ArrayList<>();
            String[] artistArray;

            if (artistName != null) {
                artistArray = separateArtist(artistName);
            }
            else{
                artistArray = new String[] {Constants.EMPTY_STRING};
            }

            for (String artist : artistArray) {
                String nameCheck = fixNameArtist(artist);
                if(!artistDao.existsByName(nameCheck)){
                    Artist artistForAddInDb = new Artist(nameCheck, Constants.EMPTY_STRING, Constants.EMPTY_STRING, statisticService.createStatistic());
                    listArtistId.add(artistDao.insert(artistForAddInDb));
                }else {
                    listArtistId.add(artistDao.getByName(nameCheck).id);
                }
            }
            return listArtistId;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'ajout des artistes'", e);
        }
        return null;
    }

    private String[] separateArtist(String artistName){
        String regex = "(?<!/)(?<!/)(?:,\\s&\\s|,\\s|;\\s|\\s&\\s|\\sfeat\\.\\s|\\sand\\s|/)(?!/)(?!/)";
        return artistName.split(regex);
    }

    private String fixNameArtist(String artistName) {
        return nameAliases.getOrDefault(artistName, artistName);
    }

    public String getArtistsNameOfMusic(long musicId){
        try {
            List<Artist> artistList = artistDao.getAllByMusic(musicId);
            List<String> nameList = new ArrayList<>();

            if (artistList != null){
                for (Artist artist : artistList){
                    nameList.add(artist.name);
                }
                return String.join(", ", nameList);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération des artistes", e);
        }
        return null;
    }

    public void incrementListeningNumberStatistic(Artist artist){
        statisticHelper.incrementListeningNumber(artist, artist.id);
    }

    public void incrementListeningTimeStatistic(Artist artist, long time){
        statisticHelper.incrementListeningTime(artist, artist.id, time);
    }

    public void incrementAllListeningStatistic(Artist artist, long time){
        statisticHelper.incrementAllListening(artist, artist.id, time);
    }

    public void reinitializeMonthValuesStatistic(Artist artist) {
        statisticHelper.reinitializeMonthValues(artist, artist.id);
    }
}
