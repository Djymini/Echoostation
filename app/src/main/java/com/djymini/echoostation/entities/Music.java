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
    public final String path;

    @NonNull
    @ColumnInfo(name = "title")
    public final String title;

    @ColumnInfo(name = "duration")
    public final long duration;

    @ColumnInfo(name = "track")
    public final int track;

    @ColumnInfo(name = "is_favorite")
    public final boolean isFavorite;

    @ColumnInfo(name = "id_album")
    public final long albumId;

    @ColumnInfo(name = "id_genre")
    public final long genreId;

    @ColumnInfo(name = "id_statistic")
    public final long statisticId;

    @ColumnInfo(name = "created_at")
    public final long createdAt;

    public Music(@NonNull String path, @NonNull String title, long duration, int track, boolean isFavorite, long albumId, long genreId, long statisticId) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.track = track;
        this.isFavorite = isFavorite;
        this.albumId = albumId;
        this.genreId = genreId;
        this.statisticId = statisticId;
        this.createdAt = TimeUtilities.currentTimeMillis();
    }

    @Ignore
    public Music(long id, @NonNull String path, @NonNull String title, long duration, int track, boolean isFavorite, long albumId, long genreId, long statisticId) {
        this(path, title, duration, track, isFavorite, albumId, genreId, statisticId);
        this.id = id;
    }

    @Ignore
    public Music(Music music, @NonNull String title, int track, long albumId, long genreId) {
        this.id = music.id;
        this.path = music.path;
        this.title = title;
        this.duration = music.duration;
        this.track = track;
        this.isFavorite = music.isFavorite;
        this.albumId = albumId;
        this.genreId = genreId;
        this.statisticId = music.statisticId;
        this.createdAt = music.createdAt;
    }
}
