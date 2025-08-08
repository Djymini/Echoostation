package com.djymini.echoostation.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Music;

import java.util.List;

@Dao
public interface MusicDao {
    @Insert
    long insert(Music music);

    @Query("INSERT INTO artist_music (id_artist, id_music, position) VALUES (:artistId, :musicId, :position)")
    void insertArtistMusic(long artistId, long musicId, int position);

    @Delete
    void delete(Music music);

    @Query("DELETE FROM artist_music WHERE id_music = :musicId")
    void deleteArtistMusicByMusicId(long musicId);

    @Update
    void update(Music music);

    @Query("SELECT EXISTS(SELECT 1 FROM music WHERE id = :id)")
    boolean existsById(long id);

    @Query("SELECT EXISTS(SELECT 1 FROM music WHERE path = :path)")
    boolean existsByPath(String path);

    @Query("SELECT EXISTS(SELECT 1 FROM artist_music WHERE id_artist = :artistId AND id_music = :musicId)")
    boolean artistMusicExists(long artistId, long musicId);

    @Query("SELECT * FROM music")
    LiveData<List<Music>> getAllLive();

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.is_favorite AS isFavorite, m.created_at AS createdAt, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id")
    LiveData<List<MusicDto>> getAllMusicDetailLive();

    @Query("SELECT * FROM music")
    List<Music> getAll();

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.is_favorite AS isFavorite, m.created_at AS createdAt, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id")
    List<MusicDto> getAllMusicDetail();

    @Query("SELECT * FROM music WHERE id = :id")
    Music getById(long id);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.is_favorite AS isFavorite, m.created_at AS createdAt, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "WHERE m.id = :id")
    MusicDto getMusicDetailById(long id);


    @Query("SELECT * FROM music WHERE path = :path")
    Music getByPath(String path);

    @Query("SELECT COUNT(*) FROM music")
    long count();

    @Query("SELECT * FROM music WHERE title LIKE '%' || :query || '%'")
    List<Music> search(String query);

    @Query("SELECT * FROM music WHERE id_album = :albumId")
    List<Music> getAllByAlbum(long albumId);

    @Query("SELECT music.* FROM music JOIN artist_music ON music.id = artist_music.id_music JOIN artist ON artist.id = artist_music.id_artist WHERE artist.id = :artistId;")
    List<Music> getAllByArtist(long artistId);

}
