package com.djymini.echoostation.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.entities.Artist;

import java.util.List;

@Dao
public interface AlbumDao {
    @Insert
    long insert(Album album);

    @Delete
    void delete(Album album);

    @Update
    void update(Album album);

    @Query("SELECT EXISTS(SELECT 1 FROM album WHERE id = :id)")
    boolean existsById(long id);

    @Query("SELECT EXISTS(SELECT 1 FROM album WHERE name = :name)")
    boolean existsByName(String name);

    @Query("SELECT EXISTS(SELECT 1 FROM album WHERE name = :name AND id_artist = :idArtist)")
    boolean existsByNameAndArtist(String name, long idArtist);

    @Query("SELECT * FROM album")
    LiveData<List<Album>> getAllLive();

    @Query("SELECT * FROM album")
    List<Album> getAll();

    @Query("SELECT * FROM album WHERE id = :id")
    Album getById(long id);

    @Query("SELECT * FROM album WHERE name = :name AND id_artist = :idArtist")
    Album getByNameAndArtist(String name, long idArtist);

    @Query("SELECT COUNT(*) FROM album")
    long count();

    @Query("SELECT * FROM album WHERE name LIKE '%' || :query || '%'")
    List<Album> search(String query);

    @Query("SELECT * FROM album WHERE id_artist = :idArtist")
    List<Album> getAllByArtist(long idArtist);
}
