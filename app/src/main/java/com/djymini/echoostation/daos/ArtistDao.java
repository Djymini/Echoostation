package com.djymini.echoostation.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djymini.echoostation.dtos.ArtistDto;
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

    @Query("UPDATE artist SET photo_path = :photoPath WHERE id = :id;")
    void modifyPhoto(long id, String photoPath);

    @Query("UPDATE artist SET description = :description WHERE id = :id;")
    void modifyDescription(long id, String description);

    @Query("SELECT EXISTS(SELECT 1 FROM artist WHERE id = :id)")
    boolean existsById(long id);

    @Query("SELECT EXISTS(SELECT 1 FROM artist WHERE name = :name)")
    boolean existsByName(String name);

    @Query("SELECT * FROM artist")
    LiveData<List<Artist>> getAllLive();

    @Query("SELECT " +
            "ar.id AS id, ar.name AS name, ar.photo_path AS photoPath, ar.description AS description, ar.created_at AS createdAt, ar.last_played AS lastPlayed, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime " + // <-- pas de virgule
            "FROM artist ar " +
            "JOIN statistic s ON ar.id_statistic = s.id")
    LiveData<List<ArtistDto>> getAllDetailLive();

    @Query("SELECT " +
            "ar.id AS id, ar.name AS name, ar.photo_path AS photoPath, ar.description AS description, ar.created_at AS createdAt, ar.last_played AS lastPlayed, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime " + // <-- pas de virgule
            "FROM artist ar " +
            "JOIN statistic s ON ar.id_statistic = s.id " +
            "ORDER BY s.month_listening_number DESC LIMIT 6")
    LiveData<List<ArtistDto>> getTopDetailLive();

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

    @Query("UPDATE artist SET last_played = :time WHERE id = :id")
    void updateLastPlay(long id, long time);
}
