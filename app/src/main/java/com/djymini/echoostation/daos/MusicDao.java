package com.djymini.echoostation.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djymini.echoostation.MusicDetail;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.entities.Mood;
import com.djymini.echoostation.entities.Music;

import java.util.List;

@Dao
public interface MusicDao {
    @Insert
    long insert(Music music);

    @Query("INSERT INTO artist_music (id_artist, id_music) VALUES (:idArtist, :idMusic)")
    void insertArtistMusic(long idArtist, long idMusic);

    @Delete
    void delete(Music music);

    @Update
    void update(Music music);

    @Query("SELECT EXISTS(SELECT 1 FROM music WHERE id = :id)")
    boolean existsById(long id);

    @Query("SELECT EXISTS(SELECT 1 FROM music WHERE path = :path)")
    boolean existsByPath(String path);

    @Query("SELECT EXISTS(SELECT 1 FROM artist_music WHERE id_artist = :idArtist AND id_music = :idMusic)")
    boolean artistMusicExists(long idArtist, long idMusic);

    @Query("SELECT * FROM music")
    LiveData<List<Music>> getAllLive();

    @Query("SELECT m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.isFavorite AS isFavorite, " +
            "al.id AS idAlbum, al.name AS nameAlbum, al.cover_path AS coverPath,  al.year AS year, " +
            "GROUP_CONCAT(a.id, ', ') AS idArtist, GROUP_CONCAT(a.name, ', ') AS nameArtist, g.id AS idGenre, g.name AS nameGenre, " +
            "s.id AS idStatistic, s.listening_number AS listeningNumber, s.month_listening_number AS monthListeningNumber, s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "JOIN artist_music ON m.id = artist_music.id_music JOIN artist a ON a.id = artist_music.id_artist " +
            "GROUP BY m.id")
    LiveData<List<MusicDetail>> getAllMusicDetailLive();

    @Query("SELECT * FROM music")
    List<Music> getAll();

    @Query("SELECT m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.isFavorite AS isFavorite, " +
            "al.id AS idAlbum, al.name AS nameAlbum, al.cover_path AS coverPath,  al.year AS year, " +
            "GROUP_CONCAT(a.id, ', ') AS idArtist, GROUP_CONCAT(a.name, ', ') AS nameArtist, g.id AS idGenre, g.name AS nameGenre, " +
            "s.id AS idStatistic, s.listening_number AS listeningNumber, s.month_listening_number AS monthListeningNumber, s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "JOIN artist_music ON m.id = artist_music.id_music JOIN artist a ON a.id = artist_music.id_artist " +
            "GROUP BY m.id")
    List<MusicDetail> getAllMusicDetail();

    @Query("SELECT * FROM music WHERE id = :id")
    Music getById(long id);

    @Query("SELECT * FROM music WHERE path = :path")
    Music getByPath(String path);

    @Query("SELECT COUNT(*) FROM music")
    long count();

    @Query("SELECT * FROM music WHERE title LIKE '%' || :query || '%'")
    List<Music> search(String query);

    @Query("SELECT * FROM music WHERE id_album = :idAlbum")
    List<Music> getAllByAlbum(long idAlbum);

    @Query("SELECT music.* FROM music JOIN artist_music ON music.id = artist_music.id_music JOIN artist ON artist.id = artist_music.id_artist WHERE artist.id = :idArtist;")
    List<Music> getAllByArtist(long idArtist);

}
