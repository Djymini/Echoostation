package com.djymini.echoostation.services;

import android.content.Context;
import android.util.Log;

import com.djymini.echoostation.R;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Genre;
import com.djymini.echoostation.helpers.StatisticHelper;
import com.djymini.echoostation.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GenreService {
    private static final String TAG = "GenreService";
    private final GenreDao genreDao;
    private final StatisticHelper<Genre> statisticHelper;
    private Map<String, String> genreAliases;

    public GenreService(GenreDao genreDao, StatisticDao statisticDao,StatisticService statisticService, Context context) {
        this.genreDao = genreDao;
        this.statisticHelper = new StatisticHelper<>(
                statisticDao,
                statisticService,
                genreDao::existsById,
                genre -> genre.statisticId
        );
        loadGenreAliases(context);
    }

    private void loadGenreAliases(Context context) {
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.genre_aliases);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String jsonString = reader.lines().collect(Collectors.joining("\n"));
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            genreAliases = gson.fromJson(jsonString, type);
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement des alias de genre", e);
            genreAliases = new HashMap<>();
        }
    }

    public long add(String genreName, StatisticService statisticService){
        try{
            String nameCheck = checkName(genreName);

            if(!genreDao.existsByName(nameCheck)){
                Genre genreForAddInDb = new Genre(nameCheck, statisticService.createStatistic());
                return genreDao.insert(genreForAddInDb);
            }else {
                return genreDao.getByName(nameCheck).id;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'ajout des genres'", e);
        }
        return -1;
    }

    private String checkName(String name){
        if(name != null){
            return fixNameGenre(name);
        }
        else {
           return fixNameGenre(Constants.EMPTY_STRING);
        }
    }

    private String fixNameGenre(String genreName){
        return genreAliases.getOrDefault(genreName, genreName);
    }

    public void incrementListeningNumberStatistic(Genre genre){
        statisticHelper.incrementListeningNumber(genre, genre.id);
    }

    public void incrementListeningTimeStatistic(Genre genre, long time){
        statisticHelper.incrementListeningTime(genre, genre.id, time);
    }

    public void incrementAllListeningStatistic(Genre genre, long time){
        statisticHelper.incrementAllListening(genre, genre.id, time);
    }

    public void reinitializeMonthValuesStatistic(Genre genre) {
        statisticHelper.reinitializeMonthValues(genre, genre.id);
    }
}
