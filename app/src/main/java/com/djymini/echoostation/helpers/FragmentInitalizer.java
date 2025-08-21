package com.djymini.echoostation.helpers;

import static com.djymini.echoostation.utilities.Constants.FRAGEMENTS_NAMES;

import androidx.fragment.app.Fragment;

import com.djymini.echoostation.R;
import com.djymini.echoostation.fragments.EqualizerFragment;
import com.djymini.echoostation.fragments.HomeFragment;
import com.djymini.echoostation.fragments.LibraryFragment;
import com.djymini.echoostation.fragments.SettingsFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentInitalizer {
    private Fragment homeFragment;
    private Fragment libraryFragment;
    private Fragment equalizerFragment;
    private Fragment settingsFragment;

    public static final Map<Fragment, String> fragmentTitle = new HashMap<>();
    public static final Map<Integer, Fragment> layoutFragment = new HashMap<>();
    public static final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    public FragmentInitalizer() {
        this.homeFragment = new HomeFragment();
        this.libraryFragment = new LibraryFragment();
        this.equalizerFragment = new EqualizerFragment();
        this.settingsFragment = new SettingsFragment();

        fragmentTitle.put(this.homeFragment, FRAGEMENTS_NAMES[0]);
        fragmentTitle.put(this.libraryFragment, FRAGEMENTS_NAMES[1]);
        fragmentTitle.put(this.equalizerFragment, FRAGEMENTS_NAMES[2]);
        fragmentTitle.put(this.settingsFragment, FRAGEMENTS_NAMES[3]);

        layoutFragment.put(R.id.home, this.homeFragment);
        layoutFragment.put(R.id.library, this.libraryFragment);
        layoutFragment.put(R.id.equalizer, this.equalizerFragment);
        layoutFragment.put(R.id.settings, this.settingsFragment);

        fragmentMap.put(0, this.homeFragment);
        fragmentMap.put(1, this.libraryFragment);
        fragmentMap.put(2, this.equalizerFragment);
        fragmentMap.put(3, this.settingsFragment);
    }

    public Fragment getHomeFragment() {
        return homeFragment;
    }

    public Fragment getLibraryFragment() {
        return libraryFragment;
    }

    public Fragment getEqualizerFragment() {
        return equalizerFragment;
    }

    public Fragment getSettingsFragment() {
        return settingsFragment;
    }

    public String getTitleFragment(Fragment fragment){
        return fragmentTitle.get(fragment);
    }

    public Fragment getFragment(int index){
        return fragmentMap.get(index);
    }

    public Fragment getFragmentWithLayout(int index){
        return layoutFragment.get(index);
    }
}
