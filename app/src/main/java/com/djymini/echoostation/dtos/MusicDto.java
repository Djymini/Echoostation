package com.djymini.echoostation.dtos;

import android.net.Uri;

public class MusicDto {
    public long id;
    public String path;
    public String title;
    public long duration;
    public int track;
    public boolean isFavorite;
    public long albumId;
    public String albumName;
    public String coverPath;
    public int year;
    public String artistId;
    public String artistName;
    public long genreId;
    public String genreName;
    public long statisticId;
    public int listeningNumber;
    public int monthListeningNumber;
    public long listeningTime;
    public long monthListeningTime;
    public long createdAt;

    public MusicDto(long id, String path, String title, long duration, int track, boolean isFavorite, long albumId, String albumName, String coverPath, int year, String artistId, String artistName, long genreId, String genreName, long statisticId, int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime, long createdAt) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.track = track;
        this.isFavorite = isFavorite;
        this.albumId = albumId;
        this.albumName = albumName;
        this.coverPath = coverPath;
        this.year = year;
        this.artistId = artistId;
        this.artistName = artistName;
        this.genreId = genreId;
        this.genreName = genreName;
        this.statisticId = statisticId;
        this.listeningNumber = listeningNumber;
        this.monthListeningNumber = monthListeningNumber;
        this.listeningTime = listeningTime;
        this.monthListeningTime = monthListeningTime;
        this.createdAt = createdAt;
    }

    public Uri getCover(){
        return Uri.parse(coverPath);
    }
}
