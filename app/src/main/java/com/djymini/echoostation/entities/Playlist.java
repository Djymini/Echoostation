package com.djymini.echoostation.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.djymini.echoostation.utilities.TimeUtilities;

@Entity(
        tableName = "playlist",
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
public class Playlist {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "name")
    public  String name;

    @ColumnInfo(name = "id_statistic")
    public  long statisticId;

    @ColumnInfo(name = "created_at")
    public  long createdAt;

    @ColumnInfo(name = "last_played")
    public  long lastPlayed;

    public Playlist(@NonNull String name, long statisticId) {
        this.name = name;
        this.statisticId = statisticId;
        this.createdAt = TimeUtilities.currentTimeMillis();
        this.lastPlayed = 0;
    }

    @Ignore

    public Playlist(long id, @NonNull String name, long statisticId) {
        this(name, statisticId);
        this.id = id;
    }
}
