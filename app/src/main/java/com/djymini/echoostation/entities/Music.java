package com.djymini.echoostation.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "music",
        foreignKeys = {
                @ForeignKey(entity = Album.class,
                        parentColumns = "id",
                        childColumns = "id_album",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(entity = Genre.class,
                        parentColumns = "id",
                        childColumns = "id_genre",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(entity = Statistic.class,
                        parentColumns = "id",
                        childColumns = "id_statistic",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"path"}, unique = true),
                @Index(value = {"title"}),
                @Index(value = {"id_album"}),
                @Index(value = {"id_genre"}),
                @Index(value = {"id_statistic"})
        }
)
public class Music {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "path")
    public String path;

    @NonNull
    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "duration")
    public long duration;

    @ColumnInfo(name = "track")
    public int track;

    @ColumnInfo(name = "isFavorite")
    public boolean isFavorite;

    @ColumnInfo(name = "id_album")
    public long idAlbum;

    @ColumnInfo(name = "id_genre")
    public long idGenre;

    @ColumnInfo(name = "id_statistic")
    public long idStatistic;

    public Music(@NonNull String path, @NonNull String title, long duration, int track, boolean isFavorite, long idAlbum, long idGenre, long idStatistic) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.track = track;
        this.isFavorite = isFavorite;
        this.idAlbum = idAlbum;
        this.idGenre = idGenre;
        this.idStatistic = idStatistic;
    }

    @Ignore
    public Music(long id, @NonNull String path, @NonNull String title, long duration, int track, boolean isFavorite, long idAlbum, long idGenre, long idStatistic) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.track = track;
        this.isFavorite = isFavorite;
        this.idAlbum = idAlbum;
        this.idGenre = idGenre;
        this.idStatistic = idStatistic;
    }
}
