package com.djymini.echoostation.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;

public class PlaylistFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO: Make the playlist fragment for display playlist (features/recupGenre)
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).modifyTitle("Playlist");
        }
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }
}