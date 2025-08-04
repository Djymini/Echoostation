package com.djymini.echoostation.services;

import android.content.Context;

import com.djymini.echoostation.R;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Genre;
import com.djymini.echoostation.entities.Statistic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

public class GenreService {
    private GenreDao genreDao;
    private StatisticDao statisticDao;
    private static final Map<String, String> NAME_ALIASES = Map.of(
            "Afrobeat", "Hip-Hop",
            "hiphop", "Hip-Hop",
            "electro", "Electronic",
            "feat.", "ft."
    );

    public GenreService(GenreDao genreDao, StatisticDao statisticDao) {
        this.genreDao = genreDao;
        this.statisticDao = statisticDao;
    }

    public long addGenre(String genreName, StatisticService statisticService, Context context){
        long idGenre;
        String nameCheck;
        if(genreName != null){
            nameCheck = fixNameGenre(genreName, context);
        }
        else {
            nameCheck = "";
        }

        if(!genreDao.existsByName(nameCheck)){
            Genre genreForAddInDb = new Genre(nameCheck, statisticService.createStatistic());
            idGenre = genreDao.insert(genreForAddInDb);
        }else {
            idGenre = genreDao.getByName(nameCheck).id;
        }

        return idGenre;
    }

    private String fixNameGenre(String genreName, Context context){
        InputStream inputStream = context.getResources().openRawResource(R.raw.genre_aliases);
        String jsonString = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> nameAliases = gson.fromJson(jsonString, type);

        if(nameAliases.get(genreName) != null){
            return nameAliases.get(genreName);
        }
        else {
            return genreName;
        }
    }

    public void incrementListeningNumberStatistic(Genre genre, StatisticService statisticService){
        long idGenre = genre.id;
        long idStatistic = genre.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(genreDao.existsById(idGenre)){
            statisticService.incrementListeningNumber(statistic);
        }
    }

    public void incrementListeningTimeStatistic(Genre genre, StatisticService statisticService, long time){
        long idGenre = genre.id;
        long idStatistic = genre.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(genreDao.existsById(idGenre)){
            statisticService.incrementListeningTime(statistic, time);
        }
    }

    public void incrementAllListeningStatistic(Genre genre, StatisticService statisticService, long time){
        long idGenre = genre.id;
        long idStatistic = genre.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(genreDao.existsById(idGenre)){
            statisticService.incrementAllListening(statistic, time);
        }
    }

    public void reinitializeMonthValuesStatistic(Genre genre, StatisticService statisticService){
        long idGenre = genre.id;
        long idStatistic = genre.idStatistic;
        Statistic statistic = statisticDao.getById(idStatistic);

        if(genreDao.existsById(idGenre)){
            statisticService.reinitializeMonthValues(statistic);
        }
    }
}
