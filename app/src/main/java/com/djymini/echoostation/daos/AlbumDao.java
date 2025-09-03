package com.djymini.echoostation.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Album;

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

    @Query("SELECT EXISTS(SELECT 1 FROM album WHERE name = :name AND id_artist = :artistId)")
    boolean existsByNameAndArtist(String name, long artistId);

    @Query("SELECT * FROM album")
    LiveData<List<Album>> getAllLive();

    @Query("SELECT " +
            "al.id AS id, al.name AS name, al.cover_path AS coverPath, al.year AS year, al.id_artist AS artistId, al.created_at AS createdAt, " +
            "ar.id AS artistId, ar.name AS artistName, ar.photo_path AS artistPhotoCover, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime " + // <-- pas de virgule
            "FROM album al " +
            "JOIN artist ar ON al.id_artist = ar.id " +
            "JOIN statistic s ON al.id_statistic = s.id")
    LiveData<List<AlbumDto>> getAllAlbumDetailLive();

    @Query("SELECT * FROM album")
    List<Album> getAll();

    @Query("SELECT " +
            "al.id AS id, al.name AS name, al.cover_path AS coverPath, al.year AS year, al.id_artist AS artistId, al.created_at AS createdAt, " +
            "ar.id AS artistId, ar.name AS artistName, ar.photo_path AS artistPhotoCover, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime " + // <-- pas de virgule
            "FROM album al " +
            "JOIN artist ar ON al.id_artist = ar.id " +
            "JOIN statistic s ON al.id_statistic = s.id")
    List<AlbumDto> getAllAlbumDetail();

    @Query("SELECT * FROM album WHERE id = :id")
    Album getById(long id);

    @Query("SELECT * FROM album WHERE name = :name AND id_artist = :artistId")
    Album getByNameAndArtist(String name, long artistId);

    @Query("SELECT COUNT(*) FROM album")
    long count();

    @Query("SELECT * FROM album WHERE name LIKE '%' || :query || '%'")
    List<Album> search(String query);

    @Query("SELECT " +
            "al.id AS id, al.name AS name, al.cover_path AS coverPath, al.year AS year, al.id_artist AS artistId, al.created_at AS createdAt, " +
            "ar.id AS artistId, ar.name AS artistName, ar.photo_path AS artistPhotoCover, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime " + // <-- pas de virgule
            "FROM album al " +
            "JOIN artist ar ON al.id_artist = ar.id " +
            "JOIN statistic s ON al.id_statistic = s.id " +
            "WHERE ar.id = :artistId")
    LiveData<List<AlbumDto>> getAllByArtistDetailLive(long artistId);

    @Query("SELECT " +
            "al.id AS id, al.name AS name, al.cover_path AS coverPath, al.year AS year, al.id_artist AS artistId, al.created_at AS createdAt, " +
            "ar.id AS artistId, ar.name AS artistName, ar.photo_path AS artistPhotoCover, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime " + // <-- pas de virgule
            "FROM album al " +
            "JOIN artist ar ON al.id_artist = ar.id " +
            "JOIN statistic s ON al.id_statistic = s.id " +
            "WHERE al.id = :albumId")
    AlbumDto getAlbumDetail(long albumId);

    @Query("SELECT * FROM album WHERE id_artist = :artistId")
    List<Album> getAllByArtist(long artistId);
}
