package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class LibraryFragment extends Fragment {
    private static final String ARG_TAB_INDEX = "tab_index";
    private int selectedTabIndex = 0;

    public static LibraryFragment newInstance(int tabIndex) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_INDEX, tabIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        ViewPager2 viewPager2 = view.findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPager2.setAdapter(viewPagerAdapter);

        if (getArguments() != null) {
            selectedTabIndex = getArguments().getInt(ARG_TAB_INDEX, 0);
        }

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("MUSIQUES"); break;
                case 1: tab.setText("ALBUMS"); break;
                case 2: tab.setText("ARTISTES"); break;
                case 3: tab.setText("GENRES"); break;
                case 4: tab.setText("PLAYLISTS"); break;
            }
        }).attach();

        String[] tabTitles = {"MUSIQUE", "ALBUM", "ARTISTE", "GENRE", "PLAYLIST"};
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                TextView customTab = (TextView) LayoutInflater.from(requireContext())
                        .inflate(R.layout.custom_tab, null);
                customTab.setText(tabTitles[i]);
                tab.setCustomView(customTab);
            }
        }

        viewPager2.setCurrentItem(selectedTabIndex, false);

        return view;
    }

}