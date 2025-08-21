package com.djymini.echoostation.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.ViewPagerAdapter;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class LibraryFragment extends Fragment {
    private static final String ARG_TAB_INDEX = "tab_index";
    private int selectedTabIndex = 0;
    private ShareSearchViewModel searchViewModel;

    public static LibraryFragment newInstance(int tabIndex) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_INDEX, tabIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        ViewPager2 viewPager2 = view.findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPager2.setAdapter(viewPagerAdapter);

        setHasOptionsMenu(true);
        searchViewModel = new ViewModelProvider(requireActivity()).get(ShareSearchViewModel.class);

        if (getArguments() != null) {
            selectedTabIndex = getArguments().getInt(ARG_TAB_INDEX, 0);
        }

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(Constants.MUSIC_TITLE.toUpperCase()); break;
                case 1: tab.setText(Constants.ALBUM_TITLE.toUpperCase()); break;
                case 2: tab.setText(Constants.ARTIST_TITLE.toUpperCase()); break;
                case 3: tab.setText(Constants.GENRE_TITLE.toUpperCase()); break;
                case 4: tab.setText(Constants.PLAYLIST_TITLE.toUpperCase()); break;
            }
        }).attach();

        String[] tabTitles = {
                Constants.MUSIC_TITLE,
                Constants.ALBUM_TITLE,
                Constants.ARTIST_TITLE,
                Constants.GENRE_TITLE,
                Constants.PLAYLIST_TITLE
        };

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                TextView customTab = (TextView) LayoutInflater.from(requireContext())
                        .inflate(R.layout.custom_tab, tabLayout, false);
                customTab.setText(tabTitles[i].toUpperCase());
                tab.setCustomView(customTab);
            }
        }

        viewPager2.setCurrentItem(selectedTabIndex, false);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0: changeTitle(Constants.MUSIC_TITLE); break;
                    case 1: changeTitle(Constants.ALBUM_TITLE); break;
                    case 2: changeTitle(Constants.ARTIST_TITLE); break;
                    case 3: changeTitle(Constants.GENRE_TITLE); break;
                    case 4: changeTitle(Constants.PLAYLIST_TITLE); break;
                }
            }
        });

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateMiniPlayerVisibility(this);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Recherche");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchViewModel.setQuery(s); // met à jour le ViewModel partagé
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void changeTitle(String newTitle){
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).modifyTitle(newTitle);
        };
    }
}