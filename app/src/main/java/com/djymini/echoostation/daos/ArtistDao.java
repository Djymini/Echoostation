package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.djymini.echoostation.entities.Artist;

import java.util.List;

@Dao
public interface ArtistDao {
    @Query("SELECT * FROM artist")
    List<Artist> getAll();

    @Query("SELECT * FROM artist WHERE name LIKE :query ORDER BY 'ASC'")
    Artist findByName(String query);

    @Insert
    void insertAll(Artist... artists);

    @Delete
    void delete(Artist artist);
}
