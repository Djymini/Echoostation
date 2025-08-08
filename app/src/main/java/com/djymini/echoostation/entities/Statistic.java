package com.djymini.echoostation.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "statistic")
public class Statistic {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "listening_number")
    public int listeningNumber;

    @ColumnInfo(name = "month_listening_number")
    public int monthListeningNumber;

    @ColumnInfo(name = "listening_time")
    public long listeningTime;

    @ColumnInfo(name = "month_listening_time")
    public long monthListeningTime;

    public Statistic(int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime) {
        this.listeningNumber = listeningNumber;
        this.monthListeningNumber = monthListeningNumber;
        this.listeningTime = listeningTime;
        this.monthListeningTime = monthListeningTime;
    }

    @Ignore
    public Statistic(long id, int listeningNumber, int monthListeningNumber, long listeningTime, long monthListeningTime) {
        this(listeningNumber, monthListeningNumber, listeningTime, monthListeningTime);
        this.id = id;
    }
}
