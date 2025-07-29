package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.djymini.echoostation.entities.Mood;
import com.djymini.echoostation.entities.Music;

import java.util.List;

@Dao
public interface MusicDao {
    @Query("SELECT * FROM music")
    List<Music> getAll();

    @Query("SELECT * FROM music WHERE title LIKE :query ORDER BY 'ASC'")
    Music findByName(String query);

    @Insert
    void insertAll(Music... musics);

    @Delete
    void delete(Music music);
}
