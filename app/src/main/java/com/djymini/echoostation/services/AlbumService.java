package com.djymini.echoostation.services;

import android.net.Uri;
import android.util.Log;

import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.helpers.StatisticHelper;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.utilities.TimeUtilities;

public class AlbumService {
    private static final String TAG = "AlbumService";
    private final AlbumDao albumDao;
    private final StatisticHelper<Album> statisticHelper;

    public AlbumService(AlbumDao albumDao, StatisticDao statisticDao, StatisticService statisticService) {
        this.albumDao = albumDao;
        this.statisticHelper = new StatisticHelper<>(
                statisticDao,
                statisticService,
                albumDao::existsById,
                album -> album.statisticId
        );
    }

    public long add(String albumName,String coverPath,int year, String artistName, ArtistService artistService, StatisticService statisticService){
        try {
            long artistId = artistService.addAllArtist(artistName, statisticService).get(0);

            if (albumName == null || albumName.isEmpty()) {
                albumName = Constants.UNKNOWN_ALBUM;
            }

            if (!albumDao.existsByNameAndArtist(albumName, artistId)) {
                long statisticId = statisticService.createStatistic();
                Album albumForAddInDb = new Album(albumName, coverPath, year, artistId, statisticId);
                return albumDao.insert(albumForAddInDb);
            } else {
                return albumDao.getByNameAndArtist(albumName, artistId).id;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'ajout de l'album", e);
            return -1;
        }
    }

    public Uri getCover(long albumId){
        try {
            Album album = albumDao.getById(albumId);
            if (album != null && album.coverPath != null && !album.coverPath.isEmpty()) {
                return Uri.parse(album.coverPath);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération de la couverture", e);
        }
        return null;
    }

    public void modifyCover(Album album, String newCoverPath){
        if (album == null) {
            Log.e(TAG, "album est null");
            return;
        }

        try {
            if (albumDao.existsById(album.id)) {
                Album updatedAlbum = new Album(album.id, album.name, newCoverPath, album.year, album.artistId, album.statisticId);
                albumDao.update(updatedAlbum);
            } else {
                Log.w(TAG, "L'album avec id " + album.id + " n'existe pas.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la modification de la couverture", e);
        }
    }

    public void incrementListeningNumberStatistic(Album album){
        statisticHelper.incrementListeningNumber(album, album.id);
        albumDao.updateLastPlay(album.id, TimeUtilities.currentTimeMillis());
    }

    public void incrementListeningTimeStatistic(Album album, long time){
        statisticHelper.incrementListeningTime(album, album.id, time);
    }

    public void incrementAllListeningStatistic(Album album, long time){
        statisticHelper.incrementAllListening(album, album.id, time);
    }

    public void reinitializeMonthValuesStatistic(Album album) {
        statisticHelper.reinitializeMonthValues(album, album.id);
    }
}
