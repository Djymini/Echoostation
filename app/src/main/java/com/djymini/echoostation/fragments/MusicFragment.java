package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
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
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.GenreDao;
import com.djymini.echoostation.daos.MoodDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.PlaylistDao;
import com.djymini.echoostation.daos.StatisticDao;
import com.djymini.echoostation.dataBase.DatabaseClient;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;

import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends EchoostationFragment {
    private RecyclerView recyclerView;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        EchooStationDatabase db = DatabaseClient.getInstance(requireContext()).getDatabase();
        setupDaoAndService(db);

        recyclerView = view.findViewById(R.id.recycler_view_song);
        spinner = view.findViewById(R.id.spinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter(artistService, albumService);
        recyclerView.setAdapter(adapter);

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , sortCategories);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Appelle une méthode de tri selon le choix
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
        musicDao.getAllLive().observe(getViewLifecycleOwner(), musics -> {
            adapter.submitList(musics);
        });
    }
}
