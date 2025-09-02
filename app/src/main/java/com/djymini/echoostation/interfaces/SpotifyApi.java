package com.djymini.echoostation.interfaces;

import com.djymini.echoostation.apiResponse.SpotifySearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SpotifyApi {
    @GET("v1/search")
    Call<SpotifySearchResponse> searchArtist(
            @Header("Authorization") String auth,
            @Query("q") String artistName,
            @Query("type") String type,
            @Query("limit") int limit
    );
}
