package com.djymini.echoostation.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "music_tag",
        indices = {
                @Index(value = {"id"})
        }
)
public class MusicTag {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "favorite_music")
    public  boolean favoriteMusic;

    @ColumnInfo(name = "good_vibe_music")
    public  boolean goodVibeMusic;

    @ColumnInfo(name = "motivation_music")
    public  boolean motivationMusic;

    @ColumnInfo(name = "party_music")
    public  boolean partyMusic;

    @ColumnInfo(name = "chill_music")
    public  boolean chillMusic;

    @ColumnInfo(name = "night_music")
    public  boolean nightMusic;

    @ColumnInfo(name = "sad_music")
    public  boolean sadMusic;

    @ColumnInfo(name = "gaming_music")
    public  boolean gamingMusic;

    @ColumnInfo(name = "morning_music")
    public  boolean morningMusic;

    @ColumnInfo(name = "walk_music")
    public  boolean walkMusic;

    @ColumnInfo(name = "drive_music")
    public  boolean driveMusic;

    @ColumnInfo(name = "work_music")
    public  boolean workMusic;

    @ColumnInfo(name = "mind_music")
    public  boolean mindMusic;

    public MusicTag() {
        this.favoriteMusic = false;
        this.goodVibeMusic = false;
        this.motivationMusic = false;
        this.partyMusic = false;
        this.chillMusic = false;
        this.nightMusic = false;
        this.sadMusic = false;
        this.gamingMusic = false;
        this.morningMusic = false;
        this.walkMusic = false;
        this.driveMusic = false;
        this.workMusic = false;
        this.mindMusic = false;
    }

    @Ignore
    public MusicTag(long id, boolean favoriteMusic, boolean goodVibeMusic, boolean motivationMusic, boolean partyMusic, boolean chillMusic, boolean nightMusic, boolean sadMusic, boolean gamingMusic, boolean morningMusic, boolean walkMusic, boolean driveMusic, boolean workMusic, boolean mindMusic) {
        this();
        this.id = id;

    }
}
