package com.djymini.echoostation.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "music_playlist",
        primaryKeys = {"id_music", "id_playlist"},
        foreignKeys = {
                @ForeignKey(
                        entity = Music.class,
                        parentColumns = "id",
                        childColumns = "id_music",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Playlist.class,
                        parentColumns = "id",
                        childColumns = "id_playlist",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("id_music"),
                @Index("id_playlist")
        }
)
public class MusicPlaylist {
    @ColumnInfo(name = "id_music")
    public final long musicId;

    @ColumnInfo(name = "id_playlist")
    public final long playlistId;

    public MusicPlaylist(long musicId, long playlistId) {
        this.musicId = musicId;
        this.playlistId = playlistId;
    }
}
