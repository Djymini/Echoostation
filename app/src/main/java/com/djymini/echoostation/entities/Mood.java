package com.djymini.echoostation.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "mood",
        foreignKeys = {
                @ForeignKey(entity = Statistic.class,
                        parentColumns = "id",
                        childColumns = "id_statistic",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index(value = {"name"})}
)
public class Mood {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "name")
    public String nameMood;

    @ColumnInfo(name = "id_statistic")
    public long idStatistic;
}
