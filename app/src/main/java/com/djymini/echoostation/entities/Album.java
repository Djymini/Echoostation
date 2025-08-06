package com.djymini.echoostation.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "album",
        foreignKeys = {
                @ForeignKey(entity = Artist.class,
                        parentColumns = "id",
                        childColumns = "id_artist"
                ),
                @ForeignKey(entity = Statistic.class,
                        parentColumns = "id",
                        childColumns = "id_statistic",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"name"}),
                @Index(value = {"id_artist"}),
                @Index(value = {"id_statistic"})
        }
)
public class Album {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "cover_path")
    public String coverPath;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "id_artist")
    public long idArtist;

    @ColumnInfo(name = "id_statistic")
    public long idStatistic;

    public Album(@NonNull String name, String coverPath, int year, long idArtist, long idStatistic) {
        this.name = name;
        this.coverPath = coverPath;
        this.year = year;
        this.idArtist = idArtist;
        this.idStatistic = idStatistic;
    }

    @Ignore
    public Album(long id, @NonNull String name, String coverPath, int year, long idArtist, long idStatistic) {
        this.id = id;
        this.name = name;
        this.coverPath = coverPath;
        this.year = year;
        this.idArtist = idArtist;
        this.idStatistic = idStatistic;
    }
}
