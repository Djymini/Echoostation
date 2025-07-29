package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.djymini.echoostation.entities.Playlist;
import com.djymini.echoostation.entities.Statistic;

import java.util.List;

@Dao
public interface StatisticDao {
    @Query("SELECT * FROM statistic")
    List<Statistic> getAll();

    @Insert
    void insertAll(Statistic... statistics);

    @Delete
    void delete(Statistic statistic);
}
