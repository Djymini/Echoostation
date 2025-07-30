package com.djymini.echoostation.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "artist",
        foreignKeys = {
                @ForeignKey(entity = Statistic.class,
                        parentColumns = "id",
                        childColumns = "id_statistic",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index(value = {"name"})}
)
public class Artist {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "path_photo")
    public String pathPhoto;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "id_statistic")
    public long idStatistic;

    public Artist(@NonNull String name,String pathPhoto, String description, long idStatistic) {
        this.name = name;
        this.pathPhoto = pathPhoto;
        this.description = description;
        this.idStatistic = idStatistic;
    }

    @Ignore
    public Artist(long id, @NonNull String name, String pathPhoto, String description, long idStatistic) {
        this.id = id;
        this.name = name;
        this.pathPhoto = pathPhoto;
        this.description = description;
        this.idStatistic = idStatistic;
    }
}
