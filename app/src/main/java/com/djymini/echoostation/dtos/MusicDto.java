package com.djymini.echoostation.dtos;

import android.net.Uri;

public class MusicDto {
    public long id;
    public String path;
    public String title;
    public long duration;
    public int track;
    public boolean isFavorite;
    public long idAlbum;
    public String nameAlbum;
    public String coverPath;
    public int year;
    public String idArtist;
    public String nameArtist;
    public long idGenre;
    public String nameGenre;
    public long idStatistic;
    public int listeningNumber;
    public int monthListeningNumber;
    public long listeningTime;
    public long monthListeningTime;
    public long createdAt;

    public MusicDto(long id, String path, String title, long duration, int track, boolean isFavorite, long idAlbum, String nameAlbum, String coverPath, int year, String idArtist, String nameArtist, long idGenre, String nameGenre, long idStatistic, int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime, long createdAt) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.track = track;
        this.isFavorite = isFavorite;
        this.idAlbum = idAlbum;
        this.nameAlbum = nameAlbum;
        this.coverPath = coverPath;
        this.year = year;
        this.idArtist = idArtist;
        this.nameArtist = nameArtist;
        this.idGenre = idGenre;
        this.nameGenre = nameGenre;
        this.idStatistic = idStatistic;
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
