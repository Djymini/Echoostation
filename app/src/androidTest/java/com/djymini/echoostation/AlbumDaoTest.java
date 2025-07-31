package com.djymini.echoostation;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.StatisticService;
import com.djymini.echoostation.testUtilities.BaseTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.util.List;
import java.util.Map;

public class AlbumDaoTest {
    private EchooStationDatabase db;
    private AlbumDao albumDao;
    private ArtistDao artistDao;
    private StatisticDao statisticDao;
    private AlbumService albumService;
    private ArtistService artistService;
    private StatisticService statisticService;

    private List<Map<String, Object>> fakeMediaStoreData;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, EchooStationDatabase.class)
                .allowMainThreadQueries()
                .build();

        albumDao = db.albumDao();
        artistDao = db.artistDao();
        statisticDao = db.statisticDao();
        albumService = new AlbumService(albumDao, statisticDao);
        artistService = new ArtistService(artistDao, statisticDao);
        statisticService = new StatisticService(statisticDao);

        fakeMediaStoreData = BaseTestUtil.createFakeData();
    }

    @After
    public void closeDb() {
        db.close();
    }

    //TODO: Make the test of Dao and service album
}
