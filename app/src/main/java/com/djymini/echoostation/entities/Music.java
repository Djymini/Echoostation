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
    public  String path;

    @NonNull
    @ColumnInfo(name = "title")
    public  String title;

    @ColumnInfo(name = "duration")
    public  long duration;

    @ColumnInfo(name = "track")
    public  int track;

    @ColumnInfo(name = "is_favorite")
    public  boolean isFavorite;

    @ColumnInfo(name = "is_good")
    public  boolean isGood;

    @ColumnInfo(name = "is_motived")
    public  boolean isMotived;

    @ColumnInfo(name = "is_music_party")
    public  boolean isMusicParty;

    @ColumnInfo(name = "is_chill")
    public  boolean isChill;

    @ColumnInfo(name = "is_night")
    public  boolean isNight;

    @ColumnInfo(name = "is_sad")
    public  boolean isSad;

    @ColumnInfo(name = "is_work_music")
    public  boolean isWorkMusic;

    @ColumnInfo(name = "id_album")
    public  long albumId;

    @ColumnInfo(name = "id_genre")
    public  long genreId;

    @ColumnInfo(name = "id_statistic")
    public  long statisticId;

    @ColumnInfo(name = "created_at")
    public  long createdAt;

    @ColumnInfo(name = "last_played")
    public  long lastPlayed;

    public Music(@NonNull String path, @NonNull String title, long duration, int track, long albumId, long genreId, long statisticId) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.track = track;
        this.isFavorite = false;
        this.isGood = false;
        this.isMotived = false;
        this.isMusicParty = false;
        this.isChill = false;
        this.isNight = false;
        this.isSad = false;
        this.isWorkMusic = false;
        this.albumId = albumId;
        this.genreId = genreId;
        this.statisticId = statisticId;
        this.createdAt = TimeUtilities.currentTimeMillis();
        this.lastPlayed = 0;
    }

    @Ignore
    public Music(long id, @NonNull String path, @NonNull String title, long duration, int track, long albumId, long genreId, long statisticId) {
        this(path, title, duration, track, albumId, genreId, statisticId);
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
        this.isGood = music.isGood;
        this.isMotived = music.isMotived;
        this.isMusicParty = music.isMusicParty;
        this.isChill = music.isChill;
        this.isNight = music.isNight;
        this.isSad = music.isSad;
        this.isWorkMusic = music.isWorkMusic;
        this.albumId = albumId;
        this.genreId = genreId;
        this.statisticId = music.statisticId;
        this.createdAt = music.createdAt;
        this.lastPlayed = music.lastPlayed;
    }
}
