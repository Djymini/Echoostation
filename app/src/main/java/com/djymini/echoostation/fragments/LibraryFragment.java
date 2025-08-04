package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LibraryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        ViewPager2 viewPager2 = view.findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Musiques"); break;
                case 1: tab.setText("Albums"); break;
                case 2: tab.setText("Artistes"); break;
                case 3: tab.setText("Genres"); break;
                case 4: tab.setText("Playlist"); break;
            }
        }).attach();

        return view;
    }

}