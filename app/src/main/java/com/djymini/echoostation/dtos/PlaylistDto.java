package com.djymini.echoostation.dtos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class PlaylistDto implements Parcelable {
    public long id;
    public String name;
    public String coverList;
    public int tracksNumber;
    public final long statisticId;
    public final int listeningNumber;
    public final int monthListeningNumber;
    public final long listeningTime;
    public final long monthListeningTime;
    public final long createdAt;
    public final long lastPlayed;

    public PlaylistDto(long id, String name, String coverList, int tracksNumber, long statisticId, int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime, long createdAt, long lastPlayed) {
        this.id = id;
        this.name = name;
        this.coverList = coverList;
        this.tracksNumber = tracksNumber;
        this.statisticId = statisticId;
        this.listeningNumber = listeningNumber;
        this.monthListeningNumber = monthListeningNumber;
        this.listeningTime = listeningTime;
        this.monthListeningTime = monthListeningTime;
        this.createdAt = createdAt;
        this.lastPlayed = lastPlayed;
    }

    protected PlaylistDto(Parcel in){
        id = in.readLong();
        name = in.readString();
        coverList = in.readString();
        tracksNumber = in.readInt();
        statisticId = in.readLong();
        listeningNumber = in.readInt();
        monthListeningNumber = in.readInt();
        listeningTime = in.readLong();
        monthListeningTime = in.readLong();
        createdAt = in.readLong();
        lastPlayed = in.readLong();
    }


    public static final Creator<PlaylistDto> CREATOR = new Creator<PlaylistDto>() {
        @Override
        public PlaylistDto createFromParcel(Parcel in) {
            return new PlaylistDto(in);
        }

        @Override
        public PlaylistDto[] newArray(int size) {
            return new PlaylistDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(coverList);
        dest.writeInt(tracksNumber);
        dest.writeLong(statisticId);
        dest.writeInt(listeningNumber);
        dest.writeInt(monthListeningNumber);
        dest.writeLong(listeningTime);
        dest.writeLong(monthListeningTime);
        dest.writeLong(createdAt);
        dest.writeLong(lastPlayed);
    }
}
