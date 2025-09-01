package com.djymini.echoostation.utilities;

import androidx.annotation.NonNull;

import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.MusicDto;

import java.util.Comparator;

public enum SortOptionAlbum {
    ALBUM_ASC("Album (A-Z)", Comparator.comparing(al -> al.name, String.CASE_INSENSITIVE_ORDER)),
    ALBUM_DESC("Album (Z-A)", Comparator.comparing((AlbumDto al) -> al.name, String.CASE_INSENSITIVE_ORDER).reversed()),
    ARTIST_ASC("Artiste (A-Z)", Comparator.comparing(al -> al.artistName, String.CASE_INSENSITIVE_ORDER)),
    ARTIST_DESC("Artiste (Z-A)", Comparator.comparing((AlbumDto al) -> al.artistName, String.CASE_INSENSITIVE_ORDER).reversed());

    private final String displayName;
    private final Comparator<AlbumDto> comparator;

    SortOptionAlbum(String displayName, Comparator<AlbumDto> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Comparator<AlbumDto> getComparator() {
        return comparator;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}
