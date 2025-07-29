package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.djymini.echoostation.entities.Album;

import java.util.List;

@Dao
public interface AlbumDao {
    @Query("SELECT * FROM album")
    List<Album> getAll();

    @Query("SELECT * FROM album WHERE name LIKE :query ORDER BY 'ASC'")
    Album findByName(String query);

    @Insert
    void insertAll(Album... albums);

    @Delete
    void delete(Album album);
}
