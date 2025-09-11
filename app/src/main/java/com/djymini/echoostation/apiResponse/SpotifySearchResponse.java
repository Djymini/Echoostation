package com.djymini.echoostation.apiResponse;

import java.util.List;

public class SpotifySearchResponse {
    public Artists artists;

    public static class Artists {
        public List<Item> items;
    }

    public static class Item {
        public String id;
        public String name;
        public List<Image> images;
        public List<String> genres;
        public int popularity;
    }

    public static class Image {
        public String url;
        public int height;
        public int width;
    }
}
