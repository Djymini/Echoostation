package com.djymini.echoostation.utilities;

import com.djymini.echoostation.dtos.MusicDto;

import java.util.Comparator;

public enum SortOption {
    TITLE_ASC("Titre (A-Z)", Comparator.comparing(m -> m.title, String.CASE_INSENSITIVE_ORDER)),
    TITLE_DESC("Titre (Z-A)", Comparator.comparing((MusicDto m) -> m.title, String.CASE_INSENSITIVE_ORDER).reversed()),
    ARTIST_ASC("Artiste (A-Z)", Comparator.comparing(m -> m.artistName, String.CASE_INSENSITIVE_ORDER)),
    ARTIST_DESC("Artiste (Z-A)", Comparator.comparing((MusicDto m) -> m.artistName, String.CASE_INSENSITIVE_ORDER).reversed()),
    ALBUM_ASC("Album (A-Z)", Comparator.comparing(m -> m.albumName, String.CASE_INSENSITIVE_ORDER)),
    ALBUM_DESC("Album (Z-A)", Comparator.comparing((MusicDto m) -> m.albumName, String.CASE_INSENSITIVE_ORDER).reversed()),
    DURATION_ASC("Durée (courte-longue)", Comparator.comparingLong(m -> m.duration)),
    DURATION_DESC("Durée (longue-courte)", Comparator.comparingLong((MusicDto m) -> m.duration).reversed());

    private final String displayName;
    private final Comparator<MusicDto> comparator;

    SortOption(String displayName, Comparator<MusicDto> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Comparator<MusicDto> getComparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
