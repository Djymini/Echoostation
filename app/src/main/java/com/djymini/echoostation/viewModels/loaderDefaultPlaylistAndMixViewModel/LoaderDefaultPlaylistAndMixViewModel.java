package com.djymini.echoostation.viewModels.loaderDefaultPlaylistAndMixViewModel;

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

public class LoaderDefaultPlaylistAndMixViewModel extends ViewModel {
    private MusicDao musicDao;
    private MusicService musicService;
    private Executor executor;

    private MutableLiveData<Map<String, List<MusicDto>>> mixMapLive = new MutableLiveData<>(new HashMap<>());
    private Map<String, Supplier<List<MusicDto>>> remakeMap = new HashMap<>();

    public LoaderDefaultPlaylistAndMixViewModel(MusicDao musicDao, MusicService musicService, Executor executor) {
        this.musicDao = musicDao;
        this.musicService = musicService;
        this.executor = executor;

        initMixes();
    }

    // --- Expose DAO data ---
    public LiveData<List<MusicDto>> loadRecentlyList() {
        return musicDao.getMusicDetailRecentlyLsteningLive();
    }

    public LiveData<List<MusicDto>> loadFavorite() {
        return musicDao.getFavoriteLive(true);
    }

    public LiveData<List<MusicDto>> loadMostListening() {
        return musicDao.getMusicDetailMostListeningLive();
    }

    // --- Expose Mixes ---
    public LiveData<Map<String, List<MusicDto>>> getMixMapLive() {
        return mixMapLive;
    }

    public void remakeTheMix(String nameMix) {
        executor.execute(() -> {
            Map<String, List<MusicDto>> current = new HashMap<>(mixMapLive.getValue());
            current.put(nameMix, remakeMap.get(nameMix).get());
            mixMapLive.postValue(current);
        });
    }

    // --- Initialization ---
    private void initMixes() {
        executor.execute(() -> {
            Map<String, List<MusicDto>> map = new HashMap<>();

            map.put(Constants.HAPPY, musicService.makeHappyMix());
            map.put(Constants.MOTIVATED, musicService.makeMotivatedMix());
            map.put(Constants.SAD, musicService.makeSadMix());
            map.put(Constants.RELAXING, musicService.makeRelaxingMix());
            map.put(Constants.INTROSPECTIVE, musicService.makeIntrospectiveMix());
            map.put(Constants.EPIC, musicService.makeEpicMix());
            map.put(Constants.WORK, musicService.makeWorkMix());
            map.put(Constants.PARTY, musicService.makePartyMix());
            map.put(Constants.RIDE, musicService.makeRideMix());
            map.put(Constants.WAKE, musicService.makeWakeMix());
            map.put(Constants.SLEEP, musicService.makeSleepMix());
            map.put(Constants.WASH, musicService.makeWashMix());

            // Remakers
            remakeMap.put(Constants.HAPPY, () -> musicService.makeHappyMix());
            remakeMap.put(Constants.MOTIVATED, () -> musicService.makeMotivatedMix());
            remakeMap.put(Constants.SAD, () -> musicService.makeSadMix());
            remakeMap.put(Constants.RELAXING, () -> musicService.makeRelaxingMix());
            remakeMap.put(Constants.INTROSPECTIVE, () -> musicService.makeIntrospectiveMix());
            remakeMap.put(Constants.EPIC, () -> musicService.makeEpicMix());
            remakeMap.put(Constants.WORK, () -> musicService.makeWorkMix());
            remakeMap.put(Constants.PARTY, () -> musicService.makePartyMix());
            remakeMap.put(Constants.RIDE, () -> musicService.makeRideMix());
            remakeMap.put(Constants.WAKE, () -> musicService.makeWakeMix());
            remakeMap.put(Constants.SLEEP, () -> musicService.makeSleepMix());
            remakeMap.put(Constants.WASH, () -> musicService.makeWashMix());

            mixMapLive.postValue(map);
        });
    }
}
