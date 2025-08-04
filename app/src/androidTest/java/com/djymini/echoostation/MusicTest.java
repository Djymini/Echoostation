package com.djymini.echoostation;
import android.util.Log;

import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.testUtilities.BaseTestUtil;
import com.djymini.echoostation.testUtilities.LiveDataTestUtil;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ApplicationProvider;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MusicTest extends EchoostationTest{
    @Test
    public void GetAll(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        try {
            List<Music> result1 = LiveDataTestUtil.getOrAwaitValue(musicDao.getAllLive());
            assertEquals(15, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Music> result2 = musicDao.getAll();
        for(Music music : result2){
            String string = String.format("id : %s, path : %s", music.id, music.path);
            Log.d(music.title, string);
        }

        assertEquals(15, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Morning Light", result2.get(0).title);
        assertEquals("track15.mp3", result2.get(14).title);
    }
}

