package com.djymini.echoostation.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "music_mood",
        primaryKeys = {"id_music", "id_mood"},
        foreignKeys = {
                @ForeignKey(
                        entity = Music.class,
                        parentColumns = "id",
                        childColumns = "id_music",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Mood.class,
                        parentColumns = "id",
                        childColumns = "id_mood",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("id_music"),
                @Index("id_mood")
        }
)
public class MusicMood {
    @ColumnInfo(name = "id_music")
    public final long musicId;

    @ColumnInfo(name = "id_mood")
    public final long moodId;

    public MusicMood(long musicId, long moodId) {
        this.musicId = musicId;
        this.moodId = moodId;
    }
}

