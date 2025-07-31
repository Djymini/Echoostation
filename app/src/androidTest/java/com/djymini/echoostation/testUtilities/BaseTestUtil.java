package com.djymini.echoostation.testUtilities;

import android.content.Context;
import android.provider.MediaStore;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.djymini.echoostation.EchooStationDatabase;

import java.util.List;
import java.util.Map;

public class BaseTestUtil {
    public static void createDb(EchooStationDatabase db){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, EchooStationDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    public static List<Map<String, Object>> createFakeData(){
        return List.of(
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track1.mp3",
                        MediaStore.Audio.Media.TITLE, "Morning Light",
                        MediaStore.Audio.Media.ALBUM, "Sunrise Vibes",
                        MediaStore.Audio.Media.ARTIST, "Yoko Shimomura & Yoshitaka Suzuki",
                        MediaStore.Audio.Media.DURATION, 215000,
                        MediaStore.Audio.Media.GENRE, "Afrobeat",
                        MediaStore.Audio.Media.TRACK, 1,
                        MediaStore.Audio.Media.YEAR, 2022,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Yoko Shimomura"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track2.mp3",
                        MediaStore.Audio.Media.TITLE, "Neon Pulse",
                        MediaStore.Audio.Media.ALBUM, "City Nights",
                        MediaStore.Audio.Media.ARTIST, "Utada",
                        MediaStore.Audio.Media.DURATION, 198000,
                        MediaStore.Audio.Media.GENRE, "Alternative",
                        MediaStore.Audio.Media.TRACK, 2,
                        MediaStore.Audio.Media.YEAR, 2023,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Utada"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track3.mp3",
                        MediaStore.Audio.Media.TITLE, "Ocean Drift",
                        MediaStore.Audio.Media.ALBUM, "Blue Horizons",
                        MediaStore.Audio.Media.ARTIST, "TK from 凛として時雨",
                        MediaStore.Audio.Media.DURATION, 230000,
                        MediaStore.Audio.Media.GENRE, "AlternativeJRock",
                        MediaStore.Audio.Media.TRACK, 3,
                        MediaStore.Audio.Media.YEAR, 2021,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "TK from 凛として時雨"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track4.mp3",
                        MediaStore.Audio.Media.TITLE, "Jazzology",
                        MediaStore.Audio.Media.ALBUM, "Smooth Lines",
                        MediaStore.Audio.Media.ARTIST, "The Sax Bros/Aurora",
                        MediaStore.Audio.Media.DURATION, 245000,
                        MediaStore.Audio.Media.GENRE, "Anime Soundtrack",
                        MediaStore.Audio.Media.TRACK, 4,
                        MediaStore.Audio.Media.YEAR, 2020,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "The Sax Bros"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track5.mp3",
                        MediaStore.Audio.Media.TITLE, "Cosmic Dust",
                        MediaStore.Audio.Media.ALBUM, "Galaxy Ride",
                        MediaStore.Audio.Media.ARTIST, "Richard M. Sherman & Robert B. Sherman",
                        MediaStore.Audio.Media.DURATION, 210000,
                        MediaStore.Audio.Media.GENRE, "Arrangement, Orchestral",
                        MediaStore.Audio.Media.TRACK, 5,
                        MediaStore.Audio.Media.YEAR, 2024,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Richard M. Sherman"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track6.mp3",
                        MediaStore.Audio.Media.TITLE, "Blues at Dusk",
                        MediaStore.Audio.Media.ALBUM, "Twilight Songs",
                        MediaStore.Audio.Media.ARTIST, "Naoshi Mizuta, Masayoshi Soken, & Nobuo Uematsu",
                        MediaStore.Audio.Media.DURATION, 200000,
                        MediaStore.Audio.Media.GENRE, "Dance",
                        MediaStore.Audio.Media.TRACK, 6,
                        MediaStore.Audio.Media.YEAR, 2019,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Naoshi Mizuta"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track7.mp3",
                        MediaStore.Audio.Media.TITLE, "Dreambound",
                        MediaStore.Audio.Media.ALBUM, "Floating",
                        MediaStore.Audio.Media.ARTIST, "MOB CHOIR feat. sajou no hana",
                        MediaStore.Audio.Media.DURATION, 220000,
                        MediaStore.Audio.Media.GENRE, "Hip Hop/Rap",
                        MediaStore.Audio.Media.TRACK, 7,
                        MediaStore.Audio.Media.YEAR, 2021,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "MOB CHOIR"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track8.mp3",
                        MediaStore.Audio.Media.TITLE, "Turbo Drive",
                        MediaStore.Audio.Media.ALBUM, "HyperSpeed",
                        MediaStore.Audio.Media.ARTIST, "Lorien Testard, Alice Duport-Percier and Victor Borba",
                        MediaStore.Audio.Media.DURATION, 190000,
                        MediaStore.Audio.Media.GENRE, "Rap/Hip Hop",
                        MediaStore.Audio.Media.TRACK, 8,
                        MediaStore.Audio.Media.YEAR, 2022,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Lorien Testard"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track9.mp3",
                        MediaStore.Audio.Media.TITLE, "Forest Path",
                        MediaStore.Audio.Media.ALBUM, "Nature Spirit",
                        MediaStore.Audio.Media.ARTIST, "Lorien Testard and Alice Duport-Percier",
                        MediaStore.Audio.Media.DURATION, 205000,
                        MediaStore.Audio.Media.GENRE, "Score",
                        MediaStore.Audio.Media.TRACK, 9,
                        MediaStore.Audio.Media.YEAR, 2020,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Lorien Testard"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track10.mp3",
                        MediaStore.Audio.Media.TITLE, "Rainy Street",
                        MediaStore.Audio.Media.ALBUM, "Urban Mood",
                        MediaStore.Audio.Media.ARTIST, "KYOSUKE HIMURO and GERARD WAY",
                        MediaStore.Audio.Media.DURATION, 188000,
                        MediaStore.Audio.Media.GENRE, "Visual-kei",
                        MediaStore.Audio.Media.TRACK, 10,
                        MediaStore.Audio.Media.YEAR, 2018,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "KYOSUKE HIMURO"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track11.mp3",
                        MediaStore.Audio.Media.TITLE, "Sunken Memories",
                        MediaStore.Audio.Media.ALBUM, "Deep Blue",
                        MediaStore.Audio.Media.ARTIST, "Hikaru Utada",
                        MediaStore.Audio.Media.DURATION, 235000,
                        MediaStore.Audio.Media.GENRE, "Musique Africaine",
                        MediaStore.Audio.Media.TRACK, 11,
                        MediaStore.Audio.Media.YEAR, 2019,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Hikaru Utada"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track12.mp3",
                        MediaStore.Audio.Media.TITLE, "Ancient Echoes",
                        MediaStore.Audio.Media.ALBUM, "Mystic Rhythms",
                        MediaStore.Audio.Media.ARTIST, "Ling tosite sigureLing tosite sigure",
                        MediaStore.Audio.Media.DURATION, 260000,
                        MediaStore.Audio.Media.GENRE, "K-Pop",
                        MediaStore.Audio.Media.TRACK, 12,
                        MediaStore.Audio.Media.YEAR, 2017,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Ling tosite sigureLing tosite sigure"
                ),
                Map.of(
                        MediaStore.Audio.Media.DATA, "/storage/emulated/0/Music/track13.mp3",
                        MediaStore.Audio.Media.TITLE, "Pulse Theory",
                        MediaStore.Audio.Media.ALBUM, "Lab Experiments",
                        MediaStore.Audio.Media.ARTIST, "BeatLab",
                        MediaStore.Audio.Media.DURATION, 178000,
                        MediaStore.Audio.Media.GENRE, "J-POP",
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
                        MediaStore.Audio.Media.ARTIST, "",
                        MediaStore.Audio.Media.DURATION, 210000,
                        MediaStore.Audio.Media.GENRE, "",
                        MediaStore.Audio.Media.TRACK, 15,
                        MediaStore.Audio.Media.YEAR, 2025,
                        MediaStore.Audio.Media.ALBUM_ARTIST, "Stack Overflow"
                )
        );
    }
}
