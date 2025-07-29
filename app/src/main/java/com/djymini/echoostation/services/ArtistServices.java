package com.djymini.echoostation.services;

import com.djymini.echoostation.daos.ArtistDao;

public class ArtistServices {
    private ArtistDao artistDao;

    public ArtistServices(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }
}
