package com.djymini.echoostation.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djymini.echoostation.entities.Artist;

import java.util.List;

@Dao
public interface ArtistDao {
    @Insert
    long insert(Artist artist);

    @Delete
    void delete(Artist artist);

    @Update
    void update(Artist artist);

    @Query("SELECT EXISTS(SELECT 1 FROM artist WHERE id = :id)")
    boolean existsById(long id);

    @Query("SELECT EXISTS(SELECT 1 FROM artist WHERE name = :name)")
    boolean existsByName(String name);

    @Query("SELECT * FROM artist")
    LiveData<List<Artist>> getAllLive();

    @Query("SELECT * FROM artist")
    List<Artist> getAll();

    @Query("SELECT * FROM artist WHERE id = :id")
    Artist getById(long id);

    @Query("SELECT * FROM artist WHERE name = :name")
    Artist getByName(String name);

    @Query("SELECT * FROM artist WHERE name LIKE '%' || :query || '%'")
    List<Artist> search(String query);

    @Query("SELECT artist.* FROM artist JOIN artist_music ON artist.id = artist_music.id_artist JOIN music ON music.id = artist_music.id_music WHERE music.id = :musicId")
    List<Artist> getAllByMusic(long musicId);

    @Query("SELECT COUNT(*) FROM artist")
    long count();
}
