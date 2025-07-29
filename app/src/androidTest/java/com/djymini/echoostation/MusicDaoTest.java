package com.djymini.echoostation;

import android.content.Context;

import androidx.room.Room;

import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.entities.Music;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ApplicationProvider;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MusicDaoTest {
    private EchooStationDatabase db;
    private MusicDao musicDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, EchooStationDatabase.class)
                .allowMainThreadQueries()
                .build();
        musicDao = db.musicDao();
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

        musicDao.insertAll(music);

        List<Music> result = musicDao.getAll();
        assertEquals(1, result.size());
        assertEquals("Titre test", result.get(0).title);
    }
}

