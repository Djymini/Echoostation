package com.djymini.echoostation.dataBase;

import android.content.Context;

import androidx.room.Room;

import com.djymini.echoostation.EchooStationDatabase;

public class DatabaseClient {
    private static DatabaseClient instance;
    private final EchooStationDatabase database;

    private DatabaseClient(Context context) {
        database = Room.databaseBuilder(
                context.getApplicationContext(),
                EchooStationDatabase.class,
                "echoo_station_db"
        ).fallbackToDestructiveMigration().build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public EchooStationDatabase getDatabase() {
        return database;
    }
}

