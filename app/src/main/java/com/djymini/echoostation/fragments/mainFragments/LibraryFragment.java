package com.djymini.echoostation.fragments.mainFragments;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
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

import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.ViewPagerAdapter;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.viewModels.ShareSearchViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LibraryFragment extends Fragment {
    private static final String ARG_TAB_INDEX = "tab_index";
    private ShareSearchViewModel searchViewModel;
    private int tabIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) createArgument();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        ViewPager2 viewPager2 = view.findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPager2.setAdapter(viewPagerAdapter);

        searchViewModel = new ViewModelProvider(requireActivity()).get(ShareSearchViewModel.class);

        Bundle args = getArguments();
        int selectedTabIndex = (args != null) ? args.getInt(ARG_TAB_INDEX, tabIndex) : tabIndex;
        viewPager2.setCurrentItem(selectedTabIndex, false);

        setupTabs(tabLayout, viewPager2);
        setupPageChangeListener(viewPager2);
        setupMenu();

        return view;
    }

    private void setupTabs(TabLayout tabLayout, ViewPager2 viewPager2) {
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(Constants.LIBRARY_TAB_TITLE[position]))
                .attach();
    }

    private void setupPageChangeListener(ViewPager2 viewPager2) {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabIndex = position;
            }
        });
    }

    private void setupMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.action_bar, menu);

                MenuItem menuItem = menu.findItem(R.id.search);
                ColorStateList tint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorText));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    menuItem.setIconTintList(tint);
                }

                if (menuItem != null && menuItem.getActionView() instanceof SearchView) {
                    SearchView searchView = (SearchView) menuItem.getActionView();
                    searchView.setQueryHint(getString(R.string.search_bar_text));

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) { return false; }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            searchViewModel.setQuery(s);
                            return true;
                        }
                    });
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner());
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
        ViewPager2 viewPager2 = getView() != null ? getView().findViewById(R.id.view_pager) : null;
        if (viewPager2 != null) viewPager2.setCurrentItem(tabIndex, true);
    }

    private void createArgument() {
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_INDEX, tabIndex);
        setArguments(args);
    }
}