package com.djymini.echoostation.utilities;

import androidx.annotation.NonNull;

import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.entities.Genre;

import java.util.Comparator;

public enum SortOptionGenre {
    GENRE_ASC("Genre (A-Z)",Comparator.comparing(gen -> gen.name, String.CASE_INSENSITIVE_ORDER)),
    GENRE_DESC("Genre (Z-A)", Comparator.comparing((Genre gen) -> gen.name, String.CASE_INSENSITIVE_ORDER).reversed());

    private final String displayName;
    private final Comparator<Genre> comparator;

    SortOptionGenre(String displayName, Comparator<Genre> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Comparator<Genre> getComparator() {
        return comparator;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}
