package com.djymini.echoostation.utilities;

import androidx.annotation.NonNull;

import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.entities.Artist;

import java.util.Comparator;

public enum SortOptionArtist {
    ARTIST_ASC("Artiste (A-Z)", Comparator.comparing(ar -> ar.name, String.CASE_INSENSITIVE_ORDER)),
    ARTIST_DESC("Artiste (Z-A)", Comparator.comparing((ArtistDto ar) -> ar.name, String.CASE_INSENSITIVE_ORDER).reversed());

    private final String displayName;
    private final Comparator<ArtistDto> comparator;

    SortOptionArtist(String displayName, Comparator<ArtistDto> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Comparator<ArtistDto> getComparator() {
        return comparator;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}
