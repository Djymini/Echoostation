package com.djymini.echoostation.utilities;

import com.djymini.echoostation.R;
import com.djymini.echoostation.ui.HomeImageButton;

import java.util.List;
import java.util.Map;

public class HomeFragmentContants {
    public static final List<HomeImageButton> homeImageButtonListPrimary = List.of(
            new HomeImageButton(Constants.RECENTLY_LISTENING, R.color.accentColor, R.color.colorThird, R.drawable.ic_echoostation_recent_listening),
            new HomeImageButton(Constants.FAVORITE, R.color.accentColor, R.color.colorThird, R.drawable.ic_echoostation_favorite),
            new HomeImageButton(Constants.MOST_LISTENING, R.color.accentColor, R.color.colorThird, R.drawable.ic_echoostation_most_listening)
    );

    public static final List<HomeImageButton> homeImageButtonListMix = List.of(
            new HomeImageButton(Constants.HAPPY, R.color.happy_color, R.color.happy_background, R.drawable.ic_echoostation_happy),
            new HomeImageButton(Constants.MOTIVATED, R.color.motivated_color, R.color.motivated_background, R.drawable.ic_echoostation_motivated),
            new HomeImageButton(Constants.SAD, R.color.sad_color, R.color.sad_background, R.drawable.ic_echoostation_sad),
            new HomeImageButton(Constants.RELAXING, R.color.relaxing_color, R.color.relaxing_background, R.drawable.ic_echoostation_relaxing),
            new HomeImageButton(Constants.INTROSPECTIVE, R.color.introspective_color, R.color.introspective_background, R.drawable.ic_echoostation_introspective),
            new HomeImageButton(Constants.EPIC, R.color.epic_color, R.color.epic_background, R.drawable.ic_echoostation_epic),
            new HomeImageButton(Constants.WORK, R.color.work_color, R.color.work_background, R.drawable.ic_echoostation_work),
            new HomeImageButton(Constants.PARTY, R.color.party_color, R.color.party_background, R.drawable.ic_echoostation_party),
            new HomeImageButton(Constants.RIDE, R.color.ride_color, R.color.ride_background, R.drawable.ic_echoostation_ride),
            new HomeImageButton(Constants.WAKE, R.color.wake_color, R.color.wake_background, R.drawable.ic_echoostation_wake),
            new HomeImageButton(Constants.SLEEP, R.color.sleep_color, R.color.sleep_background, R.drawable.ic_echoostation_sleep),
            new HomeImageButton(Constants.WASH, R.color.wash_color, R.color.wash_background, R.drawable.ic_echoostation_wash)
    );

    public static final Map<Integer, SectionLibrary> sections = Map.of(
            0, new SectionLibrary(R.id.data_music, Constants.LIBRARY_TAB_TITLE[0].toUpperCase()),
            1, new SectionLibrary(R.id.data_album, Constants.LIBRARY_TAB_TITLE[1].toUpperCase()),
            2, new SectionLibrary(R.id.data_artist, Constants.LIBRARY_TAB_TITLE[2].toUpperCase()),
            3, new SectionLibrary(R.id.data_genre, Constants.LIBRARY_TAB_TITLE[3].toUpperCase())
    );
}
