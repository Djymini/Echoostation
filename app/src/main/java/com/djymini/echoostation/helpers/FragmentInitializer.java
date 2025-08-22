package com.djymini.echoostation.helpers;

import static com.djymini.echoostation.utilities.Constants.FRAGMENTS_NAMES;

import androidx.fragment.app.Fragment;

import com.djymini.echoostation.R;
import com.djymini.echoostation.fragments.EqualizerFragment;
import com.djymini.echoostation.fragments.HomeFragment;
import com.djymini.echoostation.fragments.LibraryFragment;
import com.djymini.echoostation.fragments.SettingsFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FragmentInitializer {
    private static class FragmentConfig {
        final Supplier<Fragment> fragmentSupplier;
        final String title;
        Fragment instance;

        FragmentConfig(Supplier<Fragment> fragmentSupplier, String title) {
            this.fragmentSupplier = fragmentSupplier;
            this.title = title;
        }

        Fragment getInstance() {
            if (instance == null) {
                instance = fragmentSupplier.get();
            }
            return instance;
        }
    }

    private final Map<Integer, FragmentConfig> fragmentConfigs = new HashMap<>();

    public FragmentInitializer() {
        fragmentConfigs.put(R.id.home, new FragmentConfig(HomeFragment::new, FRAGMENTS_NAMES[0]));
        fragmentConfigs.put(R.id.library, new FragmentConfig(LibraryFragment::new, FRAGMENTS_NAMES[1]));
        fragmentConfigs.put(R.id.equalizer, new FragmentConfig(EqualizerFragment::new, FRAGMENTS_NAMES[2]));
        fragmentConfigs.put(R.id.settings, new FragmentConfig(SettingsFragment::new, FRAGMENTS_NAMES[3]));
    }

    public Fragment getFragment(int itemId) {
        FragmentConfig config = fragmentConfigs.get(itemId);
        return (config != null) ? config.getInstance() : null;
    }

    public String getTitle(Fragment fragment) {
        for (FragmentConfig config : fragmentConfigs.values()) {
            if (config.instance == fragment) {
                return config.title;
            }
        }
        return null;
    }
}