package com.djymini.echoostation;

import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.testUtilities.BaseTestUtil;
import com.djymini.echoostation.testUtilities.LiveDataTestUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ArtistTest extends EchoostationTest{
    @Test
    public void GetAll(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        try {
            List<Artist> result1 = LiveDataTestUtil.getOrAwaitValue(artistDao.getAllLive());
            assertEquals(21, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Artist> result2 = artistDao.getAll();
        for(Artist artist : result2){
            String string = String.format("id : %s, name : %s", artist.id, artist.name);
            Log.d("artist", string);
        }

        assertEquals(21, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Yoko Shimomura", result2.get(0).name);
        assertEquals("Artiste inconnu", result2.get(20).name);
    }

    @Test
    public void getArtistsNameOfMusicTest(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        List<Music> result = musicDao.getAll();
        String finalResult = artistService.getArtistsNameOfMusic(result.get(0).id);

        assertEquals("Yoko Shimomura, Yoshitaka Suzuki", finalResult);
    }
}