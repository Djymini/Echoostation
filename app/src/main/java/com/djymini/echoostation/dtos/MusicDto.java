package com.djymini.echoostation.dtos;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class MusicDto implements Parcelable {
    public final long id;
    public final String path;
    public final String title;
    public final long duration;
    public final int track;
    public long musicTagId;
    public  boolean favoriteMusic;
    public  boolean goodVibeMusic;
    public  boolean motivationMusic;
    public  boolean partyMusic;
    public  boolean chillMusic;
    public  boolean nightMusic;
    public  boolean sadMusic;
    public  boolean gamingMusic;
    public  boolean morningMusic;
    public  boolean walkMusic;
    public  boolean driveMusic;
    public  boolean workMusic;
    public  boolean mindMusic;
    public final long albumId;
    public final String albumName;
    public final String coverPath;
    public final int year;
    public final String artistId;
    public final String artistName;
    public final long genreId;
    public final String genreName;
    public final long statisticId;
    public final int listeningNumber;
    public final int monthListeningNumber;
    public final long listeningTime;
    public final long monthListeningTime;
    public final long createdAt;
    public final long lastPlayed;

    public MusicDto(long id, String path, String title, long duration, int track, long musicTagId, boolean favoriteMusic, boolean goodVibeMusic, boolean motivationMusic, boolean partyMusic, boolean chillMusic, boolean nightMusic, boolean sadMusic, boolean gamingMusic, boolean morningMusic, boolean walkMusic, boolean driveMusic, boolean workMusic, boolean mindMusic, long albumId, String albumName, String coverPath, int year, String artistId, String artistName, long genreId, String genreName, long statisticId, int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime, long createdAt, long lastPlayed) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.track = track;
        this.musicTagId = musicTagId;
        this.favoriteMusic = favoriteMusic;
        this.goodVibeMusic = goodVibeMusic;
        this.motivationMusic = motivationMusic;
        this.partyMusic = partyMusic;
        this.chillMusic = chillMusic;
        this.nightMusic = nightMusic;
        this.sadMusic = sadMusic;
        this.gamingMusic = gamingMusic;
        this.morningMusic = morningMusic;
        this.walkMusic = walkMusic;
        this.driveMusic = driveMusic;
        this.workMusic = workMusic;
        this.mindMusic = mindMusic;
        this.albumId = albumId;
        this.albumName = albumName;
        this.coverPath = coverPath;
        this.year = year;
        this.artistId = artistId;
        this.artistName = artistName;
        this.genreId = genreId;
        this.genreName = genreName;
        this.statisticId = statisticId;
        this.listeningNumber = listeningNumber;
        this.monthListeningNumber = monthListeningNumber;
        this.listeningTime = listeningTime;
        this.monthListeningTime = monthListeningTime;
        this.createdAt = createdAt;
        this.lastPlayed = lastPlayed;
    }

    protected MusicDto(Parcel in) {
        id = in.readLong();
        path = in.readString();
        title = in.readString();
        duration = in.readLong();
        track = in.readInt();
        musicTagId = in.readLong();
        favoriteMusic = in.readByte() != 0;
        goodVibeMusic = in.readByte() != 0;
        motivationMusic = in.readByte() != 0;
        partyMusic = in.readByte() != 0;
        chillMusic = in.readByte() != 0;
        nightMusic = in.readByte() != 0;
        sadMusic = in.readByte() != 0;
        gamingMusic = in.readByte() != 0;
        morningMusic = in.readByte() != 0;
        walkMusic = in.readByte() != 0;
        driveMusic = in.readByte() != 0;
        workMusic = in.readByte() != 0;
        mindMusic = in.readByte() != 0;
        albumId = in.readLong();
        albumName = in.readString();
        coverPath = in.readString();
        year = in.readInt();
        artistId = in.readString();
        artistName = in.readString();
        genreId = in.readLong();
        genreName = in.readString();
        statisticId = in.readLong();
        listeningNumber = in.readInt();
        monthListeningNumber = in.readInt();
        listeningTime = in.readLong();
        monthListeningTime = in.readLong();
        createdAt = in.readLong();
        lastPlayed = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(path);
        dest.writeString(title);
        dest.writeLong(duration);
        dest.writeInt(track);
        dest.writeLong(musicTagId);
        dest.writeByte((byte) (favoriteMusic ? 1 : 0));
        dest.writeByte((byte) (goodVibeMusic ? 1 : 0));
        dest.writeByte((byte) (motivationMusic ? 1 : 0));
        dest.writeByte((byte) (partyMusic ? 1 : 0));
        dest.writeByte((byte) (chillMusic ? 1 : 0));
        dest.writeByte((byte) (nightMusic ? 1 : 0));
        dest.writeByte((byte) (sadMusic ? 1 : 0));
        dest.writeByte((byte) (gamingMusic ? 1 : 0));
        dest.writeByte((byte) (morningMusic ? 1 : 0));
        dest.writeByte((byte) (walkMusic ? 1 : 0));
        dest.writeByte((byte) (mindMusic ? 1 : 0));
        dest.writeLong(albumId);
        dest.writeString(albumName);
        dest.writeString(coverPath);
        dest.writeInt(year);
        dest.writeString(artistId);
        dest.writeString(artistName);
        dest.writeLong(genreId);
        dest.writeString(genreName);
        dest.writeLong(statisticId);
        dest.writeInt(listeningNumber);
        dest.writeInt(monthListeningNumber);
        dest.writeLong(listeningTime);
        dest.writeLong(monthListeningTime);
        dest.writeLong(createdAt);
        dest.writeLong(lastPlayed);
    }

    public String getTitle() {
        return title;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MusicDto> CREATOR = new Creator<MusicDto>() {
        @Override
        public MusicDto createFromParcel(Parcel in) {
            return new MusicDto(in);
        }

        @Override
        public MusicDto[] newArray(int size) {
            return new MusicDto[size];
        }
    };

    public Uri getCover(){
        return Uri.parse(coverPath);
    }
}
