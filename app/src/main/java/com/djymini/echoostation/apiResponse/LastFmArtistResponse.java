package com.djymini.echoostation.apiResponse;

public class LastFmArtistResponse {
    public Artist artist;

    public static class Artist {
        public String name;
        public Bio bio;
    }

    public static class Bio {
        public String summary;
        public String content;
    }
}
