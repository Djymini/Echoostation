package com.djymini.echoostation.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "statistic")
public class Statistic {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "listening_number")
    public int listeningNumber;

    @ColumnInfo(name = "month_listening_number")
    public int monthListeningNumber;

    @ColumnInfo(name = "listening_time")
    public long listeningTime;

    @ColumnInfo(name = "month_listening_time")
    public long monthListeningTime;
}
