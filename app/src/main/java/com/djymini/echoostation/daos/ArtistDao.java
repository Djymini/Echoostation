package com.djymini.echoostation.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Statistic;

import java.util.List;

@Dao
public interface ArtistDao {
    @Insert
    void insert(Artist artist);

    @Delete
    void delete(Artist artist);

    @Update
    void update(Artist artist);

    @Query("SELECT EXISTS(SELECT 1 FROM artist WHERE id = :id)")
    boolean existsById(long id);

    @Query("SELECT EXISTS(SELECT 1 FROM artist WHERE name = :name)")
    boolean existsByName(String name);

    @Query("SELECT * FROM artist")
    LiveData<List<Artist>> getAllArtistLive();

    @Query("SELECT * FROM artist")
    List<Artist> getAllArtist();

    @Query("SELECT * FROM artist WHERE id = :id")
    Artist getById(int id);

    @Query("SELECT * FROM artist WHERE name LIKE '%' || :query || '%'")
    List<Artist> searchArtist(String query);
}
