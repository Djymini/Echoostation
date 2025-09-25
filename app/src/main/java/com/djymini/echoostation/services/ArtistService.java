package com.djymini.echoostation.services;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.djymini.echoostation.R;
import com.djymini.echoostation.apiResponse.LastFmArtistResponse;
import com.djymini.echoostation.apiResponse.SpotifySearchResponse;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.helpers.StatisticHelper;
import com.djymini.echoostation.interfaces.LastFmApi;
import com.djymini.echoostation.interfaces.SpotifyApi;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArtistService {
    private static final String TAG = "ArtistService";
    private final ArtistDao artistDao;
    private final StatisticHelper<Artist> statisticHelper;
    private Map<String, String> nameAliases;
    private Context context;
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();


    public ArtistService(ArtistDao artistDao, StatisticDao statisticDao, StatisticService statisticService, Context context) {
        this.artistDao = artistDao;
        this.context = context;
        this.statisticHelper = new StatisticHelper<>(
                statisticDao,
                statisticService,
                artistDao::existsById,
                artist -> artist.statisticId
        );
        loadNameAliases(this.context);
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
                    long artistId = artistDao.insert(artistForAddInDb);
                    listArtistId.add(artistId);
                    addArtistInfoInArtist(artistId, nameCheck);
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

    private static final List<String> EXCEPTION_ARTISTS = Arrays.asList(
            "Lous and The Yakuza"
    );

    private String[] separateArtist(String artistName) {
        if (artistName == null || artistName.isEmpty()) return new String[]{};

        // --- 1. Extraire les featurings dans le titre ---
        List<String> featuredArtists = new ArrayList<>();
        Pattern featPattern = Pattern.compile("(?i)[(\\[]?feat\\.? ([^)\\]]+)[)\\]]?");
        Matcher matcher = featPattern.matcher(artistName);
        String mainArtistPart = artistName;

        if (matcher.find()) {
            String featPart = matcher.group(1); // extrait le texte après feat
            featuredArtists.addAll(Arrays.asList(featPart.split("\\s*,\\s*|\\s*&\\s*")));
            mainArtistPart = matcher.replaceAll("");
        }

        // --- 2. Protéger les exceptions ---
        Map<String, String> placeholders = new HashMap<>();
        String modifiedName = mainArtistPart;
        int counter = 0;
        for (String exception : EXCEPTION_ARTISTS) {
            if (modifiedName.contains(exception)) {
                String placeholder = "___ARTIST_" + counter + "___";
                modifiedName = modifiedName.replace(exception, placeholder);
                placeholders.put(placeholder, exception);
                counter++;
            }
        }

        // --- 3. Split sur la regex normale ---
        String regex = "(?<!/)(?<!/)(?:,\\s&\\s|,\\s|;\\s|\\s&\\s|\\sand\\s|/)(?!/)(?!/)";
        String[] splitArtists = modifiedName.split(regex);

        // --- 4. Restaurer les exceptions ---
        List<String> finalArtists = new ArrayList<>();
        for (String s : splitArtists) {
            s = s.trim();
            if (placeholders.containsKey(s)) s = placeholders.get(s);
            if (!s.isEmpty()) finalArtists.add(s);
        }

        // --- 5. Ajouter les featurings ---
        for (String feat : featuredArtists) {
            feat = feat.trim();
            if (!feat.isEmpty()) finalArtists.add(feat);
        }

        return finalArtists.toArray(new String[0]);
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

    private static final Pattern FEAT_PATTERN = Pattern.compile(
            "(?i)\\bfeat\\.?[\\s:.-]*([^\\[\\]()]+)"
    );


    public List<String> extractFeaturedArtists(String title) {
        List<String> result = new ArrayList<>();
        if (title == null) return result;

        Matcher matcher = FEAT_PATTERN.matcher(title);
        if (matcher.find()) {
            String artistsPart = matcher.group(1);
            String[] splitArtists = separateArtist(artistsPart);
            result.addAll(Arrays.asList(splitArtists));
        }
        return result;
    }


    public void incrementListeningNumberStatistic(Artist artist){
        statisticHelper.incrementListeningNumber(artist, artist.id);
        artistDao.updateLastPlay(artist.id, TimeUtilities.currentTimeMillis());
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

    public void addArtistInfoInArtist(long artistId, String nameArtist){
        SpotifyAuthService.getAccessToken(token -> {
            Retrofit spotifyRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api.spotify.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SpotifyApi spotifyApi = spotifyRetrofit.create(SpotifyApi.class);

            spotifyApi.searchArtist("Bearer " + token, nameArtist, "artist", 1)
                    .enqueue(new Callback<SpotifySearchResponse>() {
                        @Override
                        public void onResponse(Call<SpotifySearchResponse> call, Response<SpotifySearchResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                               if (response.body().artists.items != null && !response.body().artists.items.isEmpty()) {
                                    SpotifySearchResponse.Item artistItem = response.body().artists.items.get(0);

                                    if (artistItem.images != null && !artistItem.images.isEmpty()) {
                                        String imageUrl = artistItem.images.get(0).url;

                                        SpotifyAuthService.downloadImageAndSavePermanent(context, imageUrl, artistId, new SpotifyAuthService.ImageDownloadCallback() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                dbExecutor.execute(() -> artistDao.modifyPhoto(artistId, uri.getPath()));
                                                Log.d("Spotify", "Image téléchargée et stockée en permanence : " + uri.getPath());
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.e("Spotify", "Erreur téléchargement image", e);
                                            }
                                        });




                                    } else {
                                        Log.w(TAG, "Aucune image trouvée pour l'artiste : " + nameArtist);
                                    }
                                } else {
                                    Log.w(TAG, "Aucun artiste trouvé sur Spotify pour : " + nameArtist);
                                }

                                Retrofit lastFmRetrofit = new Retrofit.Builder()
                                        .baseUrl("https://ws.audioscrobbler.com/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                LastFmApi lastFmApi = lastFmRetrofit.create(LastFmApi.class);
                                lastFmApi.getArtistInfo("artist.getinfo", nameArtist, Constants.CLE_LASTFM, "json", "fr")
                                        .enqueue(new Callback<LastFmArtistResponse>() {
                                            @Override
                                            public void onResponse(Call<LastFmArtistResponse> call, Response<LastFmArtistResponse> response) {
                                                if (response.isSuccessful() && response.body() != null) {
                                                    LastFmArtistResponse.Artist lastFmArtist = response.body().artist;
                                                    if (lastFmArtist != null) {
                                                        if (lastFmArtist.bio != null && lastFmArtist.bio.summary != null) {
                                                            dbExecutor.execute(() -> artistDao.modifyDescription(artistId, lastFmArtist.bio.summary));
                                                        } else {
                                                            Log.w(TAG, "Pas de bio disponible pour l'artiste : " + nameArtist);
                                                        }
                                                    } else {
                                                        Log.w(TAG, "Réponse Last.fm sans artiste pour : " + nameArtist);
                                                    }
                                                } else {
                                                    Log.w(TAG, "Réponse Last.fm invalide pour l'artiste : " + nameArtist);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<LastFmArtistResponse> call, Throwable t) { t.printStackTrace(); }
                                        });
                            }
                        }
                        @Override
                        public void onFailure(Call<SpotifySearchResponse> call, Throwable t) { t.printStackTrace(); }
                    });
        });
    }
}
