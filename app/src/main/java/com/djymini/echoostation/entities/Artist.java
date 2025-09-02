package com.djymini.echoostation.entities;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.djymini.echoostation.utilities.TimeUtilities;

@Entity(
        tableName = "artist",
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
public class Artist {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "name")
    public  String name;

    @ColumnInfo(name = "photo_path")
    public  String photoPath;

    @ColumnInfo(name = "description")
    public  String description;

    @ColumnInfo(name = "id_statistic")
    public  long statisticId;

    @ColumnInfo(name = "created_at")
    public  long createdAt;

    public Artist(@NonNull String name, String photoPath, String description, long statisticId) {
        this.name = name;
        this.photoPath = photoPath;
        this.description = description;
        this.statisticId = statisticId;
        this.createdAt = TimeUtilities.currentTimeMillis();
    }

    @Ignore
    public Artist(long id, @NonNull String name, String photoPath, String description, long statisticId) {
        this(name, photoPath, description, statisticId);
        this.id = id;
    }

    @Ignore
    public Artist(Artist artist, String photoPath, String description) {
        this.id = artist.id;
        this.name = artist.name;
        this.photoPath = photoPath;
        this.description = description;
        this.statisticId = artist.statisticId;
        this.createdAt = artist.createdAt;
    }

    public Uri getPhoto(){
        return Uri.parse(photoPath);
    }
}
