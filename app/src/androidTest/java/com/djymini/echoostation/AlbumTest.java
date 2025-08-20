package com.djymini.echoostation;

import static org.junit.Assert.assertEquals;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.testUtilities.BaseTestUtil;
import com.djymini.echoostation.testUtilities.LiveDataTestUtil;

import org.junit.Test;

import java.util.List;

public class AlbumTest extends EchoostationTest{
    @Test
    public void GetAll(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        try {
            List<Album> result1 = LiveDataTestUtil.getOrAwaitValue(albumDao.getAllLive());
            assertEquals(14, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Album> result2 = albumDao.getAll();
        for(Album album : result2){
            String string = String.format("id : %s, Artiste : %s", album.id, artistDao.getById(album.artistId).name);
            Log.d(album.name, string);
        }

        assertEquals(14, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("City Nights", result2.get(0).name);
        assertEquals("Album inconnu", result2.get(result2.size()-1).name);
    }

    @Test
    public void GetCover(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        assertEquals("album_cover_path", albumService.getCover(1).toString());
    }

    @Test
    public void count(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        assertEquals(14, albumDao.count());
    }
}
