package com.djymini.echoostation.utilities;

import com.djymini.echoostation.R;
import com.djymini.echoostation.ui.HomeImageButton;

import java.util.List;
import java.util.Map;

public class HomeFragmentContants {
    public static final List<HomeImageButton> homeImageButtonListPrimary = List.of(
            new HomeImageButton("Récemment écoutés", R.color.accentColor, R.color.colorThird, R.drawable.round_history_24),
            new HomeImageButton("Favoris", R.color.accentColor, R.color.colorThird, R.drawable.round_favorite_border_24),
            new HomeImageButton("Les plus écoutés", R.color.accentColor, R.color.colorThird, R.drawable.round_trending_up_24)
    );

    public static final List<HomeImageButton> homeImageButtonListMix = List.of(
            new HomeImageButton("Good vibe", R.color.good_vibe_color, R.color.good_vibe_background, R.drawable.round_sentiment_very_satisfied_24),
            new HomeImageButton("Motivation", R.color.motivation_color, R.color.motivation_background, R.drawable.round_fitness_center_24),
            new HomeImageButton("Fête", R.color.party_color, R.color.party_background, R.drawable.outline_celebration_24),
            new HomeImageButton("Détente", R.color.cleaning_color, R.color.cleaning_background, R.drawable.outline_spa_24),
            new HomeImageButton("Nuit", R.color.night_color, R.color.night_background, R.drawable.outline_bedtime_24),
            new HomeImageButton("Tristesse", R.color.sad_color, R.color.sad_background, R.drawable.round_sentiment_dissatisfied_24),
            new HomeImageButton("Gaming", R.color.gaming_color, R.color.gaming_background, R.drawable.outline_sports_esports_24),
            new HomeImageButton("Matin", R.color.morning_color, R.color.morning_background, R.drawable.outline_wb_sunny_24),
            new HomeImageButton("Ménage", R.color.wash_color, R.color.wash_background, R.drawable.outline_cleaning_services_24),
            new HomeImageButton("Conduite", R.color.drive_color, R.color.drive_background, R.drawable.outline_drive_eta_24),
            new HomeImageButton("Travail", R.color.work_color, R.color.work_background, R.drawable.outline_work_outline_24),
            new HomeImageButton("Réflexion", R.color.mind_color, R.color.mind_background, R.drawable.outline_school_24)
    );

    public static final Map<Integer, SectionLibrary> sections = Map.of(
            0, new SectionLibrary(R.id.data_music, Constants.LIBRARY_TAB_TITLE[0].toUpperCase()),
            1, new SectionLibrary(R.id.data_album, Constants.LIBRARY_TAB_TITLE[1].toUpperCase()),
            2, new SectionLibrary(R.id.data_artist, Constants.LIBRARY_TAB_TITLE[2].toUpperCase()),
            3, new SectionLibrary(R.id.data_genre, Constants.LIBRARY_TAB_TITLE[3].toUpperCase())
    );
}
