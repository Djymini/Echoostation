package com.djymini.echoostation.viewModels.playlistAndMixViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.utilities.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class PlaylistAndMixViewModel extends ViewModel {
    private final MusicDao musicDao;
    private final MusicService musicService;
    private final Executor executor;

    private final MutableLiveData<Map<String, List<MusicDto>>> mixMapLive = new MutableLiveData<>(new HashMap<>());
    private final Map<String, Supplier<List<MusicDto>>> remakeMap = new HashMap<>();

    public PlaylistAndMixViewModel(MusicDao musicDao, MusicService musicService, Executor executor) {
        this.musicDao = musicDao;
        this.musicService = musicService;
        this.executor = executor;

        initMixes();
    }

    public LiveData<List<MusicDto>> loadRecentlyList() {
        return musicDao.getMusicDetailRecentlyLsteningLive();
    }

    public LiveData<List<MusicDto>> loadFavorite() {
        return musicDao.getFavoriteLive(true);
    }

    public LiveData<List<MusicDto>> loadMostListening() {
        return musicDao.getMusicDetailMostListeningLive();
    }

    public LiveData<Map<String, List<MusicDto>>> getMixMapLive() {
        return mixMapLive;
    }

    public void remakeTheMix(String nameMix) {
        executor.execute(() -> {
            Map<String, List<MusicDto>> current = mixMapLive.getValue();
            if (current == null) {
                current = new HashMap<>();
            } else {
                current = new HashMap<>(current);
            }

            Supplier<List<MusicDto>> supplier = remakeMap.get(nameMix);
            if (supplier != null) {
                current.put(nameMix, supplier.get());
                mixMapLive.postValue(current);
            }
        });
    }

    private void initMixes() {
        executor.execute(() -> {
            Map<String, List<MusicDto>> map = new HashMap<>();

            map.put(Constants.HAPPY, musicService.makeHappyMix());
            map.put(Constants.MOTIVATED, musicService.makeMotivatedMix());
            map.put(Constants.SAD, musicService.makeSadMix());
            map.put(Constants.RELAXING, musicService.makeRelaxingMix());
            map.put(Constants.WORK, musicService.makeWorkMix());
            map.put(Constants.PARTY, musicService.makePartyMix());
            map.put(Constants.RIDE, musicService.makeRideMix());
            map.put(Constants.EPIC, musicService.makeTopMonthMix());


            remakeMap.put(Constants.HAPPY, musicService::makeHappyMix);
            remakeMap.put(Constants.MOTIVATED, musicService::makeMotivatedMix);
            remakeMap.put(Constants.SAD, musicService::makeSadMix);
            remakeMap.put(Constants.RELAXING, musicService::makeRelaxingMix);
            remakeMap.put(Constants.WORK, musicService::makeWorkMix);
            remakeMap.put(Constants.PARTY, musicService::makePartyMix);
            remakeMap.put(Constants.RIDE, musicService::makeRideMix);
            remakeMap.put(Constants.EPIC, musicService::makeTopMonthMix);

            mixMapLive.postValue(map);
        });
    }
}
