package com.djymini.echoostation.helpers;

import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.djymini.echoostation.R;
import com.djymini.echoostation.fragments.MusicPlayerFragment;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Navigator {
    private final FragmentManager fragmentManager;
    private final FragmentInitializer fragmentInitializer;
    private final Toolbar toolbar;
    private final BottomNavigationView bottomNavMenu;
    private final FrameLayout miniPlayerContainer;
    private final MusicPlayerViewModel playerViewModel;

    private Fragment activeFragment;

    public Navigator(FragmentManager fragmentManager,
                     Toolbar toolbar,
                     BottomNavigationView bottomNavMenu,
                     FrameLayout miniPlayerContainer,
                     MusicPlayerViewModel playerViewModel) {
        this.fragmentManager = fragmentManager;
        this.toolbar = toolbar;
        this.bottomNavMenu = bottomNavMenu;
        this.miniPlayerContainer = miniPlayerContainer;
        this.playerViewModel = playerViewModel;
        this.fragmentInitializer = new FragmentInitializer();
    }

    public void initFragments() {
        Fragment homeFragment = fragmentInitializer.getFragment(R.id.home);
        fragmentManager.beginTransaction()
                .add(R.id.frame_layout, homeFragment, Constants.FRAGMENTS_TAGS[0])
                .commit();

        modifyTitle(fragmentInitializer.getTitle(homeFragment));
        activeFragment = homeFragment;
    }

    public void showFragment(Fragment fragment) {
        if (fragment == activeFragment) return;

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (!fragment.isAdded()) {
            transaction.add(R.id.frame_layout, fragment);
        } else {
            transaction.show(fragment);
        }

        transaction.hide(activeFragment).commit();

        modifyTitle(fragmentInitializer.getTitle(fragment));
        activeFragment = fragment;
        updateMiniPlayerVisibility(fragment);
    }

    public void setupBottomNav() {
        bottomNavMenu.setOnItemSelectedListener(item -> {
            Fragment fragmentForLoad = fragmentInitializer.getFragment(item.getItemId());
            if (fragmentForLoad != null) {
                showFragment(fragmentForLoad);
                return true;
            }
            return false;
        });
    }

    public void openLibraryTab() {
        bottomNavMenu.setSelectedItemId(R.id.library);
    }

    public void modifyTitle(String newText) {
        toolbar.setTitle(newText);
    }

    public void updateMiniPlayerVisibility(Fragment fragment) {
        if (miniPlayerContainer == null) return;

        boolean isMusicPlayer = fragment instanceof MusicPlayerFragment;
        toolbar.setVisibility(isMusicPlayer ? View.GONE : View.VISIBLE);
        bottomNavMenu.setVisibility(isMusicPlayer ? View.GONE : View.VISIBLE);

        if (!isMusicPlayer) {
            boolean hasCurrentTrack = playerViewModel.getCurrentItem().getValue() != null;
            miniPlayerContainer.setVisibility(hasCurrentTrack ? View.VISIBLE : View.GONE);
        } else {
            miniPlayerContainer.setVisibility(View.GONE);
        }
    }
}
