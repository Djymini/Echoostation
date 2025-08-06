package com.djymini.echoostation.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "genre",
        foreignKeys = {
                @ForeignKey(entity = Statistic.class,
                        parentColumns = "id",
                        childColumns = "id_statistic",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"name"}),
                @Index(value = {"id_statistic"})
        }
)
public class Genre {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "id_statistic")
    public long idStatistic;

    public Genre(@NonNull String name, long idStatistic) {
        this.name = name;
        this.idStatistic = idStatistic;
    }

    @Ignore
    public Genre(long id, @NonNull String name, long idStatistic) {
        this.id = id;
        this.name = name;
        this.idStatistic = idStatistic;
    }
}