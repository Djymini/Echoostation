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
    public final boolean isFavorite;
    public final boolean isGood;
    public final boolean isMotived;
    public final boolean isMusicParty;
    public final boolean isChill;
    public final boolean isNight;
    public final boolean isSad;
    public final boolean isWorkMusic;
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

    public MusicDto(long id, String path, String title, long duration, int track, boolean isFavorite, boolean isGood, boolean isMotived, boolean isMusicParty, boolean isChill, boolean isNight, boolean isSad, boolean isWorkMusic, long albumId, String albumName, String coverPath, int year, String artistId, String artistName, long genreId, String genreName, long statisticId, int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime, long createdAt, long lastPlayed) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.track = track;
        this.isFavorite = isFavorite;
        this.isGood = isGood;
        this.isMotived = isMotived;
        this.isMusicParty = isMusicParty;
        this.isChill = isChill;
        this.isNight = isNight;
        this.isSad = isSad;
        this.isWorkMusic = isWorkMusic;
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
        isFavorite = in.readByte() != 0;
        isGood = in.readByte() != 0;
        isMotived = in.readByte() != 0;
        isMusicParty = in.readByte() != 0;
        isChill = in.readByte() != 0;
        isNight = in.readByte() != 0;
        isSad = in.readByte() != 0;
        isWorkMusic = in.readByte() != 0;
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
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeByte((byte) (isGood ? 1 : 0));
        dest.writeByte((byte) (isMotived ? 1 : 0));
        dest.writeByte((byte) (isMusicParty ? 1 : 0));
        dest.writeByte((byte) (isChill ? 1 : 0));
        dest.writeByte((byte) (isNight ? 1 : 0));
        dest.writeByte((byte) (isSad ? 1 : 0));
        dest.writeByte((byte) (isWorkMusic ? 1 : 0));
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
