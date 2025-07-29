package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Genre;

import java.util.List;

@Dao
public interface GenreDao {
    @Query("SELECT * FROM genre")
    List<Genre> getAll();

    @Query("SELECT * FROM genre WHERE name LIKE :query ORDER BY 'ASC'")
    Genre findByName(String query);

    @Insert
    void insertAll(Genre... genres);

    @Delete
    void delete(Genre genre);
}