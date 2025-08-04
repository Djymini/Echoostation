package com.djymini.echoostation.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djymini.echoostation.entities.Genre;

import java.util.List;

@Dao
public interface GenreDao {
    @Insert
    long insert(Genre genre);

    @Delete
    void delete(Genre genre);

    @Update
    void update(Genre genre);

    @Query("SELECT EXISTS(SELECT 1 FROM genre WHERE id = :id)")
    boolean existsById(long id);

    @Query("SELECT EXISTS(SELECT 1 FROM genre WHERE name = :name)")
    boolean existsByName(String name);

    @Query("SELECT * FROM genre")
    LiveData<List<Genre>> getAllLive();

    @Query("SELECT * FROM genre")
    List<Genre> getAll();

    @Query("SELECT * FROM genre WHERE id = :id")
    Genre getById(int id);

    @Query("SELECT * FROM genre WHERE name = :name")
    Genre getByName(String name);

    @Query("SELECT COUNT(*) FROM genre")
    long count();
}