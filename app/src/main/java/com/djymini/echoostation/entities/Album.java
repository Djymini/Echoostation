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
    public final String name;

    @ColumnInfo(name = "cover_path")
    public final String coverPath;

    @ColumnInfo(name = "year")
    public final int year;

    @ColumnInfo(name = "id_artist")
    public final long artistId;

    @ColumnInfo(name = "id_statistic")
    public final long statisticId;

    @ColumnInfo(name = "created_at")
    public final long createdAt;

    public Album(@NonNull String name, String coverPath, int year, long artistId, long statisticId) {
        this.name = name;
        this.coverPath = coverPath;
        this.year = year;
        this.artistId = artistId;
        this.statisticId = statisticId;
        this.createdAt = TimeUtilities.currentTimeMillis();
    }

    @Ignore
    public Album(long id, @NonNull String name, String coverPath, int year, long artistId, long statisticId) {
        this(name, coverPath, year, artistId, statisticId);
        this.id = id;
    }
}
