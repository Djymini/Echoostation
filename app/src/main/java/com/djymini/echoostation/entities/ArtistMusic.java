package com.djymini.echoostation.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "artist_music",
        primaryKeys = {"id_music", "id_artist"},
        foreignKeys = {
                @ForeignKey(
                        entity = Music.class,
                        parentColumns = "id",
                        childColumns = "id_music",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Artist.class,
                        parentColumns = "id",
                        childColumns = "id_artist",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("id_music"),
                @Index("id_artist")
        }
)
public class ArtistMusic {
    @ColumnInfo(name = "id_music")
    public int musicId;

    @ColumnInfo(name = "id_artist")
    public int artistId;

    @ColumnInfo(name = "position")
    public int position;

    public ArtistMusic(int musicId, int artistId, int position) {
        this.musicId = musicId;
        this.artistId = artistId;
        this.position = position;
    }
}
