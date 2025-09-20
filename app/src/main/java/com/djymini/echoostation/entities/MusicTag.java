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

    @ColumnInfo(name = "happy_music")
    public  boolean happyMusic;

    @ColumnInfo(name = "motivated_music")
    public  boolean motivatedMusic;

    @ColumnInfo(name = "sad_music")
    public  boolean sadMusic;

    @ColumnInfo(name = "relaxing_music")
    public  boolean relaxingMusic;

    @ColumnInfo(name = "introspective_music")
    public  boolean instropectiveMusic;

    @ColumnInfo(name = "epic_music")
    public  boolean epicMusic;

    @ColumnInfo(name = "work_music")
    public  boolean workMusic;

    @ColumnInfo(name = "party_music")
    public  boolean partyMusic;

    @ColumnInfo(name = "ride_music")
    public  boolean rideMusic;

    @ColumnInfo(name = "wake_music")
    public  boolean wakeMusic;

    @ColumnInfo(name = "sleep_music")
    public  boolean sleepMusic;

    @ColumnInfo(name = "wash_music")
    public  boolean washMusic;

    public MusicTag() {
        this.favoriteMusic = false;
        this.happyMusic = false;
        this.motivatedMusic = false;
        this.sadMusic = false;
        this.relaxingMusic = false;
        this.instropectiveMusic = false;
        this.epicMusic = false;
        this.workMusic = false;
        this.partyMusic = false;
        this.rideMusic = false;
        this.wakeMusic = false;
        this.sleepMusic = false;
        this.washMusic = false;
    }

    @Ignore
    public MusicTag(long id, boolean favoriteMusic, boolean happyMusic, boolean motivatedMusic, boolean sadMusic, boolean relaxingMusic, boolean instropectiveMusic, boolean epicMusic, boolean workMusic, boolean partyMusic, boolean rideMusic, boolean wakeMusic, boolean sleepMusic, boolean washMusic) {
        this();
        this.id = id;

    }
}
