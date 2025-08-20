package com.djymini.echoostation.dtos;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MusicDto implements Parcelable {
    public long id;
    public String path;
    public String title;
    public long duration;
    public int track;
    public boolean isFavorite;
    public long albumId;
    public String albumName;
    public String coverPath;
    public int year;
    public String artistId;
    public String artistName;
    public long genreId;
    public String genreName;
    public long statisticId;
    public int listeningNumber;
    public int monthListeningNumber;
    public long listeningTime;
    public long monthListeningTime;
    public long createdAt;

    public MusicDto(long id, String path, String title, long duration, int track, boolean isFavorite, long albumId, String albumName, String coverPath, int year, String artistId, String artistName, long genreId, String genreName, long statisticId, int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime, long createdAt) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.track = track;
        this.isFavorite = isFavorite;
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
    }

    protected MusicDto(Parcel in) {
        id = in.readLong();
        path = in.readString();
        title = in.readString();
        duration = in.readLong();
        track = in.readInt();
        isFavorite = in.readByte() != 0;
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(path);
        dest.writeString(title);
        dest.writeLong(duration);
        dest.writeInt(track);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
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
