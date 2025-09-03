package com.djymini.echoostation.dtos;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class AlbumDto implements Parcelable {
    public long id;
    public String name;
    public String coverPath;
    public int year;
    public long artistId;
    public String artistName;
    public String artistPhotoCover;
    public long statisticId;
    public int listeningNumber;
    public int monthListeningNumber;
    public long listeningTime;
    public long monthListeningTime;
    public long createdAt;

    public AlbumDto(long id, String name, String coverPath, int year, long artistId, String artistName, String artistPhotoCover, long statisticId, int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime, long createdAt) {
        this.id = id;
        this.name = name;
        this.coverPath = coverPath;
        this.year = year;
        this.artistId = artistId;
        this.artistName = artistName;
        this.artistPhotoCover = artistPhotoCover;
        this.statisticId = statisticId;
        this.listeningNumber = listeningNumber;
        this.monthListeningNumber = monthListeningNumber;
        this.listeningTime = listeningTime;
        this.monthListeningTime = monthListeningTime;
        this.createdAt = createdAt;
    }

    protected AlbumDto(Parcel in) {
        id = in.readLong();
        name = in.readString();
        coverPath = in.readString();
        year = in.readInt();
        artistId = in.readLong();
        artistName = in.readString();
        artistPhotoCover = in.readString();
        statisticId = in.readLong();
        listeningNumber = in.readInt();
        monthListeningNumber = in.readInt();
        listeningTime = in.readLong();
        monthListeningTime = in.readLong();
        createdAt = in.readLong();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(coverPath);
        dest.writeInt(year);
        dest.writeLong(artistId);
        dest.writeString(artistName);
        dest.writeString(artistPhotoCover);
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

    public static final Creator<AlbumDto> CREATOR = new Creator<AlbumDto>() {
        @Override
        public AlbumDto createFromParcel(Parcel in) {
            return new AlbumDto(in);
        }

        @Override
        public AlbumDto[] newArray(int size) {
            return new AlbumDto[size];
        }
    };

    public Uri getCover(){
        return Uri.parse(coverPath);
    }
}
