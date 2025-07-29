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
public class musicPlaylist {
    @ColumnInfo(name = "id_music")
    public int idMusic;

    @ColumnInfo(name = "id_playlist")
    public int idPlaylist;
}
