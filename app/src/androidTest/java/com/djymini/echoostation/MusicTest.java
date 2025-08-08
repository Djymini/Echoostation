package com.djymini.echoostation;
import android.util.Log;

import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.testUtilities.BaseTestUtil;
import com.djymini.echoostation.testUtilities.LiveDataTestUtil;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ApplicationProvider;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MusicTest extends EchoostationTest{
    @Test
    public void getAll(){
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

    @Test
    public void count(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        assertEquals(15, musicDao.count());
    }

    @Test
    public void getAllMusicDetail(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        try {
            List<MusicDto> result1 = LiveDataTestUtil.getOrAwaitValue(musicDao.getAllMusicDetailLive());
            assertEquals(15, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<MusicDto> result2 = musicDao.getAllMusicDetail();
        for(MusicDto music : result2){
            String string = String.format("id : %s, path : %s, title : %s, album : %s, artist : %s", music.id, music.path, music.title, music.albumName, music.artistName);
            Log.d(music.title, string);
        }

        assertEquals(15, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Morning Light", result2.get(0).title);
        assertEquals("track15.mp3", result2.get(14).title);
        assertEquals("City Nights", result2.get(0).albumName);
        assertEquals("1, 2", result2.get(0).artistId);
        assertEquals("Yoko Shimomura, Yoshitaka Suzuki", result2.get(0).artistName);
        assertEquals("Afrobeat", result2.get(0).genreName);
        assertEquals(0, result2.get(14).listeningNumber);
    }

    @Test
    public void modifyMusic(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        Music musicTest = musicDao.getById(15);
        musicService.modify(musicTest, "Nouveau titre", 100, 1, 1, "Kevin, Utada", artistService, statisticService);
        MusicDto musicResult = musicDao.getMusicDetailById(15);

        assertEquals(15, musicTest.id);
        assertEquals(musicTest.id, musicResult.id);

        System.out.println(musicResult.title);

        assertNotEquals(musicTest.title, musicResult.title);
        assertNotEquals(musicTest.track, musicResult.track);
        assertNotEquals(musicTest.albumId, musicResult.albumId);
        assertNotEquals(musicTest.genreId, musicResult.genreId);

        assertEquals("Nouveau titre", musicResult.title);
        assertEquals(100, musicResult.track);
        assertEquals("City Nights", musicResult.albumName);
        assertEquals("Afrobeat", musicResult.genreName);
        assertEquals("Kevin, Utada Hikaru", musicResult.artistName);
    }
}

