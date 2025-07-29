package com.djymini.echoostation;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.provider.MediaStore;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Music;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ArtistDaoTest {
    private EchooStationDatabase db;
    private ArtistDao artistDao;

    private List<Map<String, Object>> fakeMediaStoreData;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, EchooStationDatabase.class)
                .allowMainThreadQueries()
                .build();

        artistDao = db.artistDao();

        fakeMediaStoreData = List.of(
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track1.mp3",
                        MediaStore.Audio.Media.TITLE, "Morning Light",
                        MediaStore.Audio.Media.ALBUM, "Sunrise Vibes",
                        MediaStore.Audio.Media.ARTIST, "Aurora",
                        MediaStore.Audio.Media.DURATION, 215000,
                        MediaStore.Audio.Media.GENRE, "Ambient",
                        MediaStore.Audio.Media.TRACK, 1,
                        MediaStore.Audio.Media.YEAR, 2022,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Aurora"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track2.mp3",
                        MediaStore.Audio.Media.TITLE, "Neon Pulse",
                        MediaStore.Audio.Media.ALBUM, "City Nights",
                        MediaStore.Audio.Media.ARTIST, "ElectroFox",
                        MediaStore.Audio.Media.DURATION, 198000,
                        MediaStore.Audio.Media.GENRE, "Synthwave",
                        MediaStore.Audio.Media.TRACK, 2,
                        MediaStore.Audio.Media.YEAR, 2023,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "ElectroFox"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track3.mp3",
                        MediaStore.Audio.Media.TITLE, "Ocean Drift",
                        MediaStore.Audio.Media.ALBUM, "Blue Horizons",
                        MediaStore.Audio.Media.ARTIST, "Waveform",
                        MediaStore.Audio.Media.DURATION, 230000,
                        MediaStore.Audio.Media.GENRE, "Chill",
                        MediaStore.Audio.Media.TRACK, 3,
                        MediaStore.Audio.Media.YEAR, 2021,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Waveform"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track4.mp3",
                        MediaStore.Audio.Media.TITLE, "Jazzology",
                        MediaStore.Audio.Media.ALBUM, "Smooth Lines",
                        MediaStore.Audio.Media.ARTIST, "The Sax Bros",
                        MediaStore.Audio.Media.DURATION, 245000,
                        MediaStore.Audio.Media.GENRE, "Jazz",
                        MediaStore.Audio.Media.TRACK, 4,
                        MediaStore.Audio.Media.YEAR, 2020,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "The Sax Bros"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track5.mp3",
                        MediaStore.Audio.Media.TITLE, "Cosmic Dust",
                        MediaStore.Audio.Media.ALBUM, "Galaxy Ride",
                        MediaStore.Audio.Media.ARTIST, "Starlight",
                        MediaStore.Audio.Media.DURATION, 210000,
                        MediaStore.Audio.Media.GENRE, "Electronic",
                        MediaStore.Audio.Media.TRACK, 5,
                        MediaStore.Audio.Media.YEAR, 2024,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Starlight"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track6.mp3",
                        MediaStore.Audio.Media.TITLE, "Blues at Dusk",
                        MediaStore.Audio.Media.ALBUM, "Twilight Songs",
                        MediaStore.Audio.Media.ARTIST, "Dusty Strings",
                        MediaStore.Audio.Media.DURATION, 200000,
                        MediaStore.Audio.Media.GENRE, "Blues",
                        MediaStore.Audio.Media.TRACK, 6,
                        MediaStore.Audio.Media.YEAR, 2019,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Dusty Strings"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track7.mp3",
                        MediaStore.Audio.Media.TITLE, "Dreambound",
                        MediaStore.Audio.Media.ALBUM, "Floating",
                        MediaStore.Audio.Media.ARTIST, "Echo Waves",
                        MediaStore.Audio.Media.DURATION, 220000,
                        MediaStore.Audio.Media.GENRE, "Lofi",
                        MediaStore.Audio.Media.TRACK, 7,
                        MediaStore.Audio.Media.YEAR, 2021,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Echo Waves"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track8.mp3",
                        MediaStore.Audio.Media.TITLE, "Turbo Drive",
                        MediaStore.Audio.Media.ALBUM, "HyperSpeed",
                        MediaStore.Audio.Media.ARTIST, "Volt",
                        MediaStore.Audio.Media.DURATION, 190000,
                        MediaStore.Audio.Media.GENRE, "Techno",
                        MediaStore.Audio.Media.TRACK, 8,
                        MediaStore.Audio.Media.YEAR, 2022,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Volt"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track9.mp3",
                        MediaStore.Audio.Media.TITLE, "Forest Path",
                        MediaStore.Audio.Media.ALBUM, "Nature Spirit",
                        MediaStore.Audio.Media.ARTIST, "Green Bloom",
                        MediaStore.Audio.Media.DURATION, 205000,
                        MediaStore.Audio.Media.GENRE, "New Age",
                        MediaStore.Audio.Media.TRACK, 9,
                        MediaStore.Audio.Media.YEAR, 2020,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Green Bloom"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track10.mp3",
                        MediaStore.Audio.Media.TITLE, "Rainy Street",
                        MediaStore.Audio.Media.ALBUM, "Urban Mood",
                        MediaStore.Audio.Media.ARTIST, "Sonic Shapes",
                        MediaStore.Audio.Media.DURATION, 188000,
                        MediaStore.Audio.Media.GENRE, "Trip-Hop",
                        MediaStore.Audio.Media.TRACK, 10,
                        MediaStore.Audio.Media.YEAR, 2018,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Sonic Shapes"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track11.mp3",
                        MediaStore.Audio.Media.TITLE, "Sunken Memories",
                        MediaStore.Audio.Media.ALBUM, "Deep Blue",
                        MediaStore.Audio.Media.ARTIST, "Nocturne",
                        MediaStore.Audio.Media.DURATION, 235000,
                        MediaStore.Audio.Media.GENRE, "Instrumental",
                        MediaStore.Audio.Media.TRACK, 11,
                        MediaStore.Audio.Media.YEAR, 2019,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Nocturne"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track12.mp3",
                        MediaStore.Audio.Media.TITLE, "Ancient Echoes",
                        MediaStore.Audio.Media.ALBUM, "Mystic Rhythms",
                        MediaStore.Audio.Media.ARTIST, "Tribal Code",
                        MediaStore.Audio.Media.DURATION, 260000,
                        MediaStore.Audio.Media.GENRE, "World",
                        MediaStore.Audio.Media.TRACK, 12,
                        MediaStore.Audio.Media.YEAR, 2017,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Tribal Code"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track13.mp3",
                        MediaStore.Audio.Media.TITLE, "Pulse Theory",
                        MediaStore.Audio.Media.ALBUM, "Lab Experiments",
                        MediaStore.Audio.Media.ARTIST, "BeatLab",
                        MediaStore.Audio.Media.DURATION, 178000,
                        MediaStore.Audio.Media.GENRE, "Experimental",
                        MediaStore.Audio.Media.TRACK, 13,
                        MediaStore.Audio.Media.YEAR, 2024,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "BeatLab"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track14.mp3",
                        MediaStore.Audio.Media.TITLE, "Glass Waves",
                        MediaStore.Audio.Media.ALBUM, "Reflect",
                        MediaStore.Audio.Media.ARTIST, "Crystal Sound",
                        MediaStore.Audio.Media.DURATION, 195000,
                        MediaStore.Audio.Media.GENRE, "Ambient",
                        MediaStore.Audio.Media.TRACK, 14,
                        MediaStore.Audio.Media.YEAR, 2023,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Crystal Sound"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track15.mp3",
                        MediaStore.Audio.Media.TITLE, "Infinite Loop",
                        MediaStore.Audio.Media.ALBUM, "Code Vibes",
                        MediaStore.Audio.Media.ARTIST, "Stack Overflow",
                        MediaStore.Audio.Media.DURATION, 210000,
                        MediaStore.Audio.Media.GENRE, "IDM",
                        MediaStore.Audio.Media.TRACK, 15,
                        MediaStore.Audio.Media.YEAR, 2025,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Stack Overflow"
                )
        );

    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndGetMusic() {
        Music music = new Music();
        music.path = "/music/test.mp3";
        music.title = "Titre test";
        music.duration = 123456;
        music.track = 1;
        music.isFavorite = true;
        music.idAlbum = 0;
        music.idGenre = 0;
        music.idStatistic = 0;


    }

    @Test
    public void insertAndGetArtist() {
        Music music = new Music();
        music.path = "/music/test.mp3";
        music.title = "Titre test";
        music.duration = 123456;
        music.track = 1;
        music.isFavorite = true;
        music.idAlbum = 0;
        music.idGenre = 0;
        music.idStatistic = 0;


    }
}