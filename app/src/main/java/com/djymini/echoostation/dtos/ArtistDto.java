package com.djymini.echoostation.dtos;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import com.djymini.echoostation.entities.Artist;

public class ArtistDto implements Parcelable {
    public long id;
    public String name;
    public String photoPath;
    public String description;
    public long statisticId;
    public int listeningNumber;
    public int monthListeningNumber;
    public long listeningTime;
    public long monthListeningTime;
    public long createdAt;
    public long lastPlayed;

    public ArtistDto(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.photoPath = in.readString();
        this.description = in.readString();
        this.statisticId = in.readLong();
        this.listeningNumber = in.readInt();
        this.monthListeningNumber = in.readInt();
        this.listeningTime = in.readLong();
        this.monthListeningTime = in.readLong();
        this.createdAt = in.readLong();
        this.lastPlayed = in.readLong();
    }

    public ArtistDto(long id, String name, String photoPath, String description, long statisticId, int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime, long createdAt, long lastPlayed) {
        this.id = id;
        this.name = name;
        this.photoPath = photoPath;
        this.description = description;
        this.statisticId = statisticId;
        this.listeningNumber = listeningNumber;
        this.monthListeningNumber = monthListeningNumber;
        this.listeningTime = listeningTime;
        this.monthListeningTime = monthListeningTime;
        this.createdAt = createdAt;
        this.lastPlayed = lastPlayed;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(photoPath);
        dest.writeString(description);
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

    public static final Creator<ArtistDto> CREATOR = new Creator<ArtistDto>() {
        @Override
        public ArtistDto createFromParcel(Parcel in) {
            return new ArtistDto(in);
        }

        @Override
        public ArtistDto[] newArray(int size) {
            return new ArtistDto[size];
        }
    };
}
