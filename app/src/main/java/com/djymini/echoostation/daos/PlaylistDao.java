package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.djymini.echoostation.dtos.PlaylistDto;
import com.djymini.echoostation.entities.Playlist;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    List<Playlist> getAll();

    @Query("SELECT " +
            "p.id AS id, " +
            "p.name AS name, " +
            "GROUP_CONCAT(DISTINCT al.cover_path) AS coverList, " +
            "COUNT(mp.id_music) AS tracksNumber, " +
            "s.id AS statisticId, " +
            "s.listening_number AS listeningNumber, " +
            "s.month_listening_number AS monthListeningNumber, " +
            "s.listening_time AS listeningTime, " +
            "s.month_listening_time AS monthListeningTime, " +
            "p.created_at AS createdAt, " +
            "p.last_played AS lastPlayed " +
            "FROM playlist p " +
            "LEFT JOIN music_playlist mp ON mp.id_playlist = p.id " +
            "LEFT JOIN music m ON m.id = mp.id_music " +
            "LEFT JOIN album al ON al.id = m.id_album " +
            "JOIN statistic s ON p.id_statistic = s.id " +
            "GROUP BY p.id")
    List<PlaylistDto> getAllDto();

    @Query("SELECT * FROM playlist WHERE name LIKE '%' || :query || '%'")
    List<Playlist> search(String query);

    @Insert
    void insertAll(Playlist... playlists);

    @Delete
    void delete(Playlist playlist);

    @Query("SELECT COUNT(*) FROM playlist")
    long count();
}
