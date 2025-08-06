package com.djymini.echoostation;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class MusicDetail {
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
}
