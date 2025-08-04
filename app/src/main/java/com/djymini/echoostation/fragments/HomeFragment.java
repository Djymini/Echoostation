package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djymini.echoostation.R;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO: Make the home fragment for display best album, best artist, best song, never listening and other features (features/home)
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}