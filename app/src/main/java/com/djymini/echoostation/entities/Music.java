package com.djymini.echoostation.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
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
                @Index(value = {"title"}, unique = true),
                @Index(value = {"path"}, unique = true)
        }
)
public class Music {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "path")
    public String path;

    @NonNull
    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "duration")
    public int duration;

    @ColumnInfo(name = "track")
    public int track;

    @ColumnInfo(name = "isFavorite")
    public boolean isFavorite;

    @ColumnInfo(name = "id_album")
    public int idAlbum;

    @ColumnInfo(name = "id_genre")
    public int idGenre;

    @ColumnInfo(name = "id_statistic")
    public int idStatistic;
}
