package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.djymini.echoostation.entities.Mood;

import java.util.List;

@Dao
public interface MoodDao {
    @Query("SELECT * FROM mood")
    List<Mood> getAll();

    @Insert
    void insertAll(Mood... moods);

    @Delete
    void delete(Mood mood);

    @Query("SELECT * FROM mood WHERE name LIKE '%' || :query || '%'")
    List<Mood> search(String query);
}