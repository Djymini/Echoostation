package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djymini.echoostation.entities.Playlist;
import com.djymini.echoostation.entities.Statistic;

import java.util.List;

@Dao
public interface StatisticDao {
    @Insert
    void insertAll(Statistic... statistics);

    @Delete
    void delete(Statistic statistic);

    @Update
    void update(Statistic statistic);

    @Query("SELECT EXISTS(SELECT 1 FROM statistic WHERE id = :id)")
    boolean existsById(int id);

    @Query("SELECT * FROM statistic")
    List<Statistic> getAll();

    @Query("SELECT * FROM statistic WHERE id = :id")
    Statistic getById(int id);
}
