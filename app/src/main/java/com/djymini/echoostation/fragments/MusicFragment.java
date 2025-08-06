package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.djymini.echoostation.EchooStationDatabase;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.MusicAdapter;
import com.djymini.echoostation.dataBase.DatabaseClient;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MusicFragment extends EchoostationFragment {
    private RecyclerView recyclerView;
    private List<MusicDto> currentMusicList = new ArrayList<>();
    private MusicAdapter adapter;
    private Spinner spinner;
    private final String[] sortCategories = new String[] {
            "Nom (A -> Z)",
            "Nom (Z -> A)",
            "Durée (Courte -> Longue)",
            "Durée (Longue -> Courte)",
            "Album (A -> Z)",
            "Album (Z -> A)",
            "Artiste (A -> Z)",
            "Artiste (Z -> A)",
            "Nombre d'écoutes (plus -> moins)",
            "Nombre d'écoutes (moins -> plus)",
            "Date d'ajout (récent -> ancien)",
            "Date d'ajout (ancien -> récent)"
    };
    private String search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        EchooStationDatabase db = DatabaseClient.getInstance(requireContext()).getDatabase();
        setupDaoAndService(db);

        recyclerView = view.findViewById(R.id.recycler_view_song);
        spinner = view.findViewById(R.id.spinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter();
        recyclerView.setAdapter(adapter);

        ShareSearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(ShareSearchViewModel.class);

        searchViewModel.getQuery().observe(getViewLifecycleOwner(), query -> {
            search = query;
            sortAndDisplayMusics(spinner.getSelectedItemPosition());
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , sortCategories);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortAndDisplayMusics(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optionnel : ne rien faire
            }
        });

        loadMusics();

        return view;
    }

    private void loadMusics() {
        musicDao.getAllMusicDetailLive().observe(getViewLifecycleOwner(), musics -> {
            currentMusicList = new ArrayList<>(musics); // copie pour éviter les effets de bord
            sortAndDisplayMusics(spinner.getSelectedItemPosition()); // trie selon l'option sélectionnée
        });
    }

    private void sortAndDisplayMusics(int position) {
        if (currentMusicList == null) return;

        List<MusicDto> sortedList = new ArrayList<>(fullTextSearchByLogicalOr(currentMusicList, search));

        switch (position) {
            case 0:
                sortedList.sort((m1, m2) -> m1.title.compareToIgnoreCase(m2.title));
                break;
            case 1:
                sortedList.sort((m1, m2) -> m2.title.compareToIgnoreCase(m1.title));
                break;
            case 2:
                sortedList.sort(Comparator.comparingLong(m -> m.duration));
                break;
            case 3:
                sortedList.sort((m1, m2) -> Long.compare(m2.duration, m1.duration));
                break;
            case 4: // Album A -> Z
                sortedList.sort((m1, m2) -> m1.nameAlbum.compareToIgnoreCase(m2.nameAlbum));
                break;
            case 5: // Album Z -> A
                sortedList.sort((m1, m2) -> m2.nameAlbum.compareToIgnoreCase(m1.nameAlbum));
                break;
            case 6: // Artiste A -> Z
                sortedList.sort((m1, m2) -> m1.nameArtist.compareToIgnoreCase(m2.nameArtist));
                break;
            case 7: // Artiste Z -> A
                sortedList.sort((m1, m2) -> m2.nameArtist.compareToIgnoreCase(m1.nameArtist));
                break;
            case 8: // Écoutes + -> -
                sortedList.sort((m1, m2) -> Integer.compare(m2.listeningNumber, m1.listeningNumber));
                break;
            case 9: // Écoutes - -> +
                sortedList.sort(Comparator.comparingInt(m -> m.listeningNumber));
                break;
            case 10: // Date ajout récent -> ancien
                sortedList.sort((m1, m2) -> Long.compare(m2.id, m1.id));
                break;
            case 11: // Date ajout ancien -> récent
                sortedList.sort(Comparator.comparingLong(m -> m.id));
                break;
        }

        adapter.submitList(sortedList);
    }

    private List<MusicDto> fullTextSearchByLogicalOr(List<MusicDto> musicDtoList, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return musicDtoList;

        return musicDtoList.stream()
                .filter(musicDto -> musicDto.title != null && musicDto.title.toLowerCase().contains(keyword.toLowerCase())
                || musicDto.nameAlbum != null && musicDto.nameAlbum.toLowerCase().contains(keyword.toLowerCase())
                || musicDto.nameArtist != null && musicDto.nameArtist.toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
