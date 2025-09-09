package com.djymini.echoostation.utilities;

import com.djymini.echoostation.R;
import com.djymini.echoostation.ui.HomeImageButton;

import java.util.List;
import java.util.Map;

public class HomeFragmentContants {
    public static final List<HomeImageButton> homeImageButtonListPrimary = List.of(
            new HomeImageButton("Récemment écoutés", R.color.accentColor, R.drawable.round_history_24),
            new HomeImageButton("Favoris", R.color.accentColor, R.drawable.round_favorite_border_24),
            new HomeImageButton("Les plus écoutés", R.color.accentColor, R.drawable.round_trending_up_24)
    );

    public static final List<HomeImageButton> homeImageButtonListMix = List.of(
            new HomeImageButton("Good vibe", R.color.good_vibe_color, R.drawable.round_sentiment_very_satisfied_24),
            new HomeImageButton("Motivation", R.color.motivation_color, R.drawable.round_fitness_center_24),
            new HomeImageButton("Fête", R.color.fete_color, R.drawable.outline_celebration_24),
            new HomeImageButton("Détente", R.color.detente_color, R.drawable.outline_spa_24),
            new HomeImageButton("Nuit", R.color.nuit_color, R.drawable.outline_bedtime_24),
            new HomeImageButton("Tristesse", R.color.tristesse_color, R.drawable.round_sentiment_dissatisfied_24),
            new HomeImageButton("Gaming", R.color.gaming_color, R.drawable.outline_sports_esports_24),
            new HomeImageButton("Matin", R.color.matin_color, R.drawable.outline_wb_sunny_24),
            new HomeImageButton("Marche", R.color.marche_color, R.drawable.outline_directions_walk_24),
            new HomeImageButton("Conduite", R.color.conduite_color, R.drawable.outline_drive_eta_24),
            new HomeImageButton("Travail", R.color.travail_color, R.drawable.outline_work_outline_24),
            new HomeImageButton("Réflexion", R.color.reflexion_color, R.drawable.outline_school_24)
    );

    public static final Map<Integer, SectionLibrary> sections = Map.of(
            0, new SectionLibrary(R.id.data_music, Constants.LIBRARY_TAB_TITLE[0].toUpperCase()),
            1, new SectionLibrary(R.id.data_album, Constants.LIBRARY_TAB_TITLE[1].toUpperCase()),
            2, new SectionLibrary(R.id.data_artist, Constants.LIBRARY_TAB_TITLE[2].toUpperCase()),
            3, new SectionLibrary(R.id.data_genre, Constants.LIBRARY_TAB_TITLE[3].toUpperCase())
    );
}
