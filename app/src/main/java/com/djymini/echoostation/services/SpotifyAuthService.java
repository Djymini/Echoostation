package com.djymini.echoostation.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.djymini.echoostation.interfaces.TokenCallback;
import com.djymini.echoostation.utilities.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class SpotifyAuthService {
    private static final String CLIENT_ID = Constants.CLIENT_ID;
    private static final String CLIENT_SECRET = Constants.CLIENT_SECRET;
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

    public static void getAccessToken(TokenCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://accounts.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpotifyTokenApi api = retrofit.create(SpotifyTokenApi.class);
        String credentials = Base64.encodeToString(
                (CLIENT_ID + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP);

        Call<SpotifyTokenResponse> call = api.getToken("client_credentials", "Basic " + credentials);
        call.enqueue(new Callback<SpotifyTokenResponse>() {
            @Override
            public void onResponse(Call<SpotifyTokenResponse> call, Response<SpotifyTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onTokenReceived(response.body().access_token);
                }
            }
            @Override
            public void onFailure(Call<SpotifyTokenResponse> call, Throwable t) { t.printStackTrace(); }
        });
    }

    public interface SpotifyTokenApi {
        @FormUrlEncoded
        @POST("api/token")
        Call<SpotifyTokenResponse> getToken(
                @Field("grant_type") String grantType,
                @Header("Authorization") String authHeader
        );
    }

    public interface ImageDownloadCallback {
        void onSuccess(Uri uri);
        void onError(Exception e);
    }

    public static class SpotifyTokenResponse {
        public String access_token;
        public String token_type;
        public int expires_in;
    }

    public static void downloadImageAndSavePermanent(Context context, String imageUrl, long artistId, ImageDownloadCallback callback) {
        Glide.with(context)
                .asFile()
                .load(imageUrl)
                .into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        try {
                            File filesDir = new File(context.getFilesDir(), "artist_images");
                            if (!filesDir.exists()) filesDir.mkdirs();

                            File permanentFile = new File(filesDir, "artist_" + artistId + ".png");
                            try (FileInputStream in = new FileInputStream(resource);
                                 FileOutputStream out = new FileOutputStream(permanentFile)) {

                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = in.read(buffer)) > 0) {
                                    out.write(buffer, 0, len);
                                }
                            }

                            callback.onSuccess(Uri.fromFile(permanentFile));

                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onError(e);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        callback.onError(new Exception("Échec du téléchargement de l'image via Glide"));
                    }
                });
    }





}
