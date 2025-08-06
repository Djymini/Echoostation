package com.djymini.echoostation;

import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.djymini.echoostation.entities.Genre;
import com.djymini.echoostation.testUtilities.BaseTestUtil;
import com.djymini.echoostation.testUtilities.LiveDataTestUtil;

import org.junit.Test;

import java.util.List;

public class GenreTest extends EchoostationTest{
    @Test
    public void GetAll(){
        BaseTestUtil.addContentInDb(genreService, albumService, artistService, musicService, statisticService, ApplicationProvider.getApplicationContext());

        try {
            List<Genre> result1 = LiveDataTestUtil.getOrAwaitValue(genreDao.getAllLive());
            assertEquals(11, result1.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Genre> result2 = genreDao.getAll();
        for(Genre genre : result2){
            String string = String.format("id : %s, name : %s", genre.id, genre.nameGenre);
            Log.d("genre", string);
        }

        assertEquals(11, result2.size());
        assertEquals(1, result2.get(0).id);
        assertEquals("Afrobeat", result2.get(0).nameGenre);
        assertEquals("Genre inconnu", result2.get(result2.size() - 1).nameGenre);
    }
}
