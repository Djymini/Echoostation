package com.djymini.echoostation.interfaces;

import com.djymini.echoostation.apiResponse.LastFmArtistResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LastFmApi {
    @GET("2.0/")
    Call<LastFmArtistResponse> getArtistInfo(
            @Query("method") String method,
            @Query("artist") String artistName,
            @Query("api_key") String apiKey,
            @Query("format") String format,
            @Query("lang") String lang
    );
}
