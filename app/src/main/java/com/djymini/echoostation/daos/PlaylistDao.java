package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.djymini.echoostation.entities.Playlist;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    List<Playlist> getAll();

    @Query("SELECT * FROM playlist WHERE name LIKE '%' || :query || '%'")
    List<Playlist> search(String query);

    @Insert
    void insertAll(Playlist... playlists);

    @Delete
    void delete(Playlist playlist);

    @Query("SELECT COUNT(*) FROM playlist")
    long count();
}
