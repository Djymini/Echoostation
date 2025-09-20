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

    @Query("INSERT INTO music_playlist (id_playlist, id_music) VALUES (:playlistId, :musicId)")
    void insertMusicPlaylist(long playlistId, long musicId);

    @Delete
    void delete(Music music);

    @Query("DELETE FROM music WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM music WHERE path = :path")
    void deleteByPath(String path);

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
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
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
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
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
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
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

    @Query("SELECT COUNT(*) " +
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
            "WHERE s.listening_number = 0")
    LiveData<Long> countNeverPlayed();

    @Query("SELECT SUM(s.listening_number) " +
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
            ") AS artist_data ON artist_data.musicId = m.id ")
    LiveData<Long> countMusicPlayed();

    @Query("SELECT SUM(s.month_listening_number) " +
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
            ") AS artist_data ON artist_data.musicId = m.id ")
    LiveData<Long> countMonthMusicPlayed();

    @Query("SELECT SUM(s.listening_time) " +
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
            ") AS artist_data ON artist_data.musicId = m.id ")
    LiveData<Long> countMusicPlayedTime();

    @Query("SELECT SUM(s.month_listening_time) " +
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
            ") AS artist_data ON artist_data.musicId = m.id ")
    LiveData<Long> countMonthMusicPlayedTime();

    @Query("SELECT * FROM music WHERE title LIKE '%' || :query || '%'")
    List<Music> search(String query);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "WHERE m.id_album = :albumId")
    List<MusicDto> getMusicDetailByAlbum(long albumId);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "WHERE m.id_genre = :genreId")
    List<MusicDto> getMusicDetailByGenre(long genreId);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "WHERE m.id_album = :albumId")
    LiveData<List<MusicDto>> getMusicDetailByAlbumLive(long albumId);

    @Query("SELECT * FROM music WHERE id_album = :albumId")
    List<Music> getAllByAlbum(long albumId);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "WHERE (',' || artist_data.artistId || ',') LIKE ('%,' || :artistId || ',%')")
    LiveData<List<MusicDto>> getMusicDetailByArtistLive(String artistId);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "JOIN music_playlist mp ON m.id = mp.id_music JOIN playlist p ON mp.id_playlist = p.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "WHERE p.id = :idPlaylist")
    List<MusicDto> getMusicDetailByPlaylist(long idPlaylist);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "WHERE (',' || artist_data.artistId || ',') LIKE ('%,' || :artistId || ',%')")
    List<MusicDto> getMusicDetailByArtist(String artistId);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist) AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
                    "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
                    "   JOIN artist a ON a.id = am_sorted.id_artist " +
                    "   GROUP BY am_sorted.id_music " +
                    ") AS artist_data ON artist_data.musicId = m.id " +
                    "WHERE (',' || artist_data.artistId || ',') LIKE ('%,' || :artistId || ',%') " +
                    "ORDER BY s.listening_number DESC LIMIT 5")
    LiveData<List<MusicDto>> getMusicDetailByArtistLiveBest5(String artistId);


    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "ORDER BY m.last_played DESC LIMIT 50")
    List<MusicDto> getMusicDetailRecentlyLstening();

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "ORDER BY m.last_played DESC LIMIT 50")
    LiveData<List<MusicDto>> getMusicDetailRecentlyLsteningLive();

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "WHERE " +
            "(:favorite IS NULL OR mt.favorite_music = :favorite) AND (:happy IS NULL OR mt.happy_music = :happy) AND " +
            "(:motivated IS NULL OR mt.motivated_music = :motivated) AND (:sad IS NULL OR mt.sad_music = :sad) AND " +
            "(:relaxing IS NULL OR mt.relaxing_music = :relaxing) AND (:introspective IS NULL OR mt.introspective_music = :introspective) AND " +
            "(:epic IS NULL OR mt.epic_music = :epic) AND (:work IS NULL OR mt.work_music = :work) AND " +
            "(:party IS NULL OR mt.party_music = :party) AND (:ride IS NULL OR mt.ride_music = :ride) AND " +
            "(:wake IS NULL OR mt.wake_music = :wake) AND (:sleep IS NULL OR mt.sleep_music = :sleep) AND " +
            "(:wash IS NULL OR mt.wash_music = :wash)")
    List<MusicDto> getMusicByTags(Boolean favorite, Boolean happy, Boolean motivated, Boolean sad, Boolean relaxing, Boolean introspective, Boolean epic, Boolean work, Boolean party, Boolean ride, Boolean wake, Boolean sleep, Boolean wash);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "WHERE " +
            "(:favorite IS NULL OR mt.favorite_music = :favorite)")
    LiveData<List<MusicDto>> getFavoriteLive(Boolean favorite);

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "ORDER BY s.listening_number DESC LIMIT 50")
    List<MusicDto> getMusicDetailMostListening();

    @Query("SELECT " +
            "m.id AS id, m.path AS path, m.title AS title, " +
            "m.duration AS duration, m.track AS track, m.created_at AS createdAt, m.last_played AS lastPlayed, " +
            "al.id AS albumId, al.name AS albumName, al.cover_path AS coverPath, al.year AS year, " +
            "g.id AS genreId, g.name AS genreName, " +
            "mt.id AS musicTagId, mt.favorite_music AS favoriteMusic, mt.happy_music AS happyMusic, mt.motivated_music AS motivatedMusic, mt.sad_music AS sadMusic, mt.relaxing_music AS relaxingMusic, mt.introspective_music AS introspectiveMusic, mt.epic_music AS epicMusic, mt.work_music AS workMusic, mt.party_music AS partyMusic, mt.ride_music AS rideMusic, mt.wake_music AS wakeMusic, mt.sleep_music AS sleepMusic, mt.wash_music AS washMusic, " +
            "s.id AS statisticId, s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, s.month_listening_time AS monthListeningTime, " +
            "artist_data.artistId, artist_data.artistName " +
            "FROM music m " +
            "JOIN album al ON m.id_album = al.id " +
            "JOIN genre g ON m.id_genre = g.id " +
            "JOIN music_tag mt ON m.id_music_tag = mt.id " +
            "JOIN statistic s ON m.id_statistic = s.id " +
            "LEFT JOIN ( " +
            "   SELECT am_sorted.id_music AS musicId, " +
            "          GROUP_CONCAT(am_sorted.id_artist, ', ') AS artistId, " +
            "          GROUP_CONCAT(a.name, ', ') AS artistName " +
            "   FROM (SELECT * FROM artist_music ORDER BY position ASC) AS am_sorted " +
            "   JOIN artist a ON a.id = am_sorted.id_artist " +
            "   GROUP BY am_sorted.id_music " +
            ") AS artist_data ON artist_data.musicId = m.id " +
            "ORDER BY s.listening_number DESC LIMIT 50")
    LiveData<List<MusicDto>> getMusicDetailMostListeningLive();

    @Query("SELECT music.* FROM music JOIN artist_music ON music.id = artist_music.id_music JOIN artist ON artist.id = artist_music.id_artist WHERE artist.id = :artistId;")
    List<Music> getAllByArtist(long artistId);

    @Query("SELECT path FROM music")
    List<String> getAllPath();

    @Query("UPDATE music SET last_played = :time WHERE id = :id")
    void updateLastPlay(long id, long time);

}
