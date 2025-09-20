package com.djymini.echoostation.helpers;

import androidx.lifecycle.LifecycleOwner;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class MixManager {
    public List<MusicDto> recentlyList;
    public List<MusicDto> favorite;
    public List<MusicDto> mostListening;

    public Map<String, List<MusicDto>> mixMap = new HashMap<>();
    public Map<String, Supplier<List<MusicDto>>> remakeMap = new HashMap<>();

    public MixManager(MainActivity main, LifecycleOwner lifecycleOwner, Executor executor) {
        main.dbService.getMusicDao().getMusicDetailRecentlyLsteningLive().observe(lifecycleOwner,
                musics -> recentlyList = new ArrayList<>(musics));

        main.dbService.getMusicDao().getFavoriteLive(true).observe(lifecycleOwner,
                musics -> favorite = new ArrayList<>(musics));

        main.dbService.getMusicDao().getMusicDetailMostListeningLive().observe(lifecycleOwner,
                musics -> mostListening = new ArrayList<>(musics));

        executor.execute(() ->{
            mixMap.put(Constants.HAPPY, main.dbService.getMusicService().makeHappyMix());
            mixMap.put(Constants.MOTIVATED, main.dbService.getMusicService().makeMotivatedMix());
            mixMap.put(Constants.SAD, main.dbService.getMusicService().makeSadMix());
            mixMap.put(Constants.RELAXING, main.dbService.getMusicService().makeRelaxingMix());
            mixMap.put(Constants.INTROSPECTIVE, main.dbService.getMusicService().makeIntrospectiveMix());
            mixMap.put(Constants.EPIC, main.dbService.getMusicService().makeEpicMix());
            mixMap.put(Constants.WORK, main.dbService.getMusicService().makeWorkMix());
            mixMap.put(Constants.PARTY, main.dbService.getMusicService().makePartyMix());
            mixMap.put(Constants.RIDE, main.dbService.getMusicService().makeRideMix());
            mixMap.put(Constants.WAKE, main.dbService.getMusicService().makeWakeMix());
            mixMap.put(Constants.SLEEP, main.dbService.getMusicService().makeSleepMix());
            mixMap.put(Constants.WASH, main.dbService.getMusicService().makeWashMix());

            remakeMap.put(Constants.HAPPY, () -> main.dbService.getMusicService().makeHappyMix());
            remakeMap.put(Constants.MOTIVATED, () -> main.dbService.getMusicService().makeMotivatedMix());
            remakeMap.put(Constants.SAD, () -> main.dbService.getMusicService().makeSadMix());
            remakeMap.put(Constants.RELAXING, () -> main.dbService.getMusicService().makeRelaxingMix());
            remakeMap.put(Constants.INTROSPECTIVE, () -> main.dbService.getMusicService().makeIntrospectiveMix());
            remakeMap.put(Constants.EPIC, () -> main.dbService.getMusicService().makeEpicMix());
            remakeMap.put(Constants.WORK, () -> main.dbService.getMusicService().makeWorkMix());
            remakeMap.put(Constants.PARTY, () -> main.dbService.getMusicService().makePartyMix());
            remakeMap.put(Constants.RIDE, () -> main.dbService.getMusicService().makeRideMix());
            remakeMap.put(Constants.WAKE, () -> main.dbService.getMusicService().makeWakeMix());
            remakeMap.put(Constants.SLEEP, () -> main.dbService.getMusicService().makeSleepMix());
            remakeMap.put(Constants.WASH, () -> main.dbService.getMusicService().makeWashMix());
        });
    }

    public List<MusicDto> remakeTheMix(String nameMix){
        mixMap.put(nameMix, remakeMap.get(nameMix).get());
        return mixMap.get(nameMix);
    }
}
