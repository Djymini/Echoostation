package com.djymini.echoostation.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.fragments.mainFragments.LibraryFragment;
import com.djymini.echoostation.fragments.TrueMusicPlayer;
import com.djymini.echoostation.fragments.playlistMusicFragment.PlaylistPersoFragment;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class Navigator {
    private final FragmentManager fragmentManager;
    private final FragmentInitializer fragmentInitializer;
    private final Toolbar toolbar;
    private final BottomNavigationView bottomNavMenu;
    private final MusicPlayerViewModel playerViewModel;
    public final TrueMusicPlayer trueMusicPlayer;
    private final Context context;

    private Fragment activeFragment;

    public Navigator(FragmentManager fragmentManager, Toolbar toolbar, BottomNavigationView bottomNavMenu,
                     MusicPlayerViewModel playerViewModel, View view, LifecycleOwner lifecycleOwner, ViewModelStoreOwner storeOwner, Context context, Activity activity) {
        this.fragmentManager = fragmentManager;
        this.toolbar = toolbar;
        this.bottomNavMenu = bottomNavMenu;
        this.playerViewModel = playerViewModel;
        this.fragmentInitializer = new FragmentInitializer();
        this.trueMusicPlayer = new TrueMusicPlayer(view, lifecycleOwner, storeOwner, context, activity);
        this.context = context;

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavMenu, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });
    }

    public void initFragments() {
        Fragment homeFragment = fragmentInitializer.getFragment(R.id.home);
        fragmentManager.beginTransaction()
                .add(R.id.frame_layout, homeFragment, Constants.FRAGMENTS_TAGS[0])
                .commit();

        modifyTitle(fragmentInitializer.getTitle(homeFragment));
        activeFragment = homeFragment;
    }

    public void initMusicPlayer() {
        if (playerViewModel.getIsPlaying().getValue()){
            trueMusicPlayer.getMainContent().setVisibility(View.VISIBLE);
        } else{
            trueMusicPlayer.getMainContent().setVisibility(View.GONE);
        }
    }

    public void showFragment(Fragment fragment, boolean changeTheTitle) {
        if (fragment == activeFragment) return;

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (!fragment.isAdded()) {
            transaction.add(R.id.frame_layout, fragment);
        } else {
            transaction.show(fragment);
        }

        transaction.hide(activeFragment).commit();
        if(changeTheTitle)
            modifyTitle(fragmentInitializer.getTitle(fragment));
        setActiveFragment(fragment);
        updateToolbarMenu(fragment);
    }

    public void goBackToLibrary(int idFragment) {
        Fragment fragment = fragmentInitializer.getFragment(idFragment);
        if (fragment == null) return;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (activeFragment != null) {
            transaction.remove(activeFragment);
        }

        if (!fragment.isAdded()) {
            transaction.add(R.id.frame_layout, fragment);
        } else {
            transaction.show(fragment);
        }

        transaction.commit();
        activeFragment = fragment;
        updateToolbarMenu(fragment);
    }


    public void setupTrueMusicPlayer(View playerBottomSheet) {
        trueMusicPlayer.setMainContent((MotionLayout) playerBottomSheet);
        trueMusicPlayer.setBottomSheetBehavior(BottomSheetBehavior.from(playerBottomSheet));
        trueMusicPlayer.getBottomSheetBehavior().setPeekHeight(trueMusicPlayer.dpToPx(100, context));
        trueMusicPlayer.getBottomSheetBehavior().setHideable(false);

        bottomNavMenu.post(() -> {
            int navHeight = bottomNavMenu.getHeight();
            trueMusicPlayer.getBottomSheetBehavior()
                    .setPeekHeight(trueMusicPlayer.dpToPx(84, context) + navHeight);
        });

        MotionLayout motionLayout = (MotionLayout) playerBottomSheet;

        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {}

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                TextView titleView = motionLayout.findViewById(R.id.player_title);
                TextView artistView = motionLayout.findViewById(R.id.player_artist);

                if (titleView != null) {
                    float minSize = 16f;
                    float maxSize = 20f;
                    float newSize = minSize + (maxSize - minSize) * progress;
                    titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
                }

                if (artistView != null) {
                    float minSize = 14f;
                    float maxSize = 16f;
                    float newSize = minSize + (maxSize - minSize) * progress;
                    artistView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
                }
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {}

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {}
        });

        trueMusicPlayer.getBottomSheetBehavior().addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset >= 0) {
                    motionLayout.setProgress(slideOffset);

                    bottomNavMenu.setTranslationY(bottomNavMenu.getHeight() * slideOffset);
                }
            }
        });

        playerBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trueMusicPlayer.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    public void setupBottomNav() {
        bottomNavMenu.setOnItemSelectedListener(item -> {
            Fragment fragmentForLoad = fragmentInitializer.getFragment(item.getItemId());
            String tag = String.valueOf(item.getItemId());
            if (fragmentForLoad != null) {
                showFragment(fragmentForLoad, true);
                return true;
            }
            return false;
        });
    }

    public void openLibraryTab(int tabIndex) {
        bottomNavMenu.setSelectedItemId(R.id.library);
        Fragment libraryFragment = fragmentInitializer.getFragment(R.id.library);
        LibraryFragment libraryFragment1 = (LibraryFragment)libraryFragment;

        libraryFragment1.setTabIndex(tabIndex);
        showFragment(libraryFragment, true);
    }

    public void modifyTitle(String newText) {
        toolbar.setTitle(newText);
    }

    public void updateToolbarMenu(Fragment fragment) {
        toolbar.getMenu().clear();

        if (fragment instanceof LibraryFragment) {
            toolbar.inflateMenu(R.menu.action_bar);
        }
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public Fragment getActiveFragment() {
        return activeFragment;
    }

    public void setActiveFragment(Fragment activeFragment) {
        this.activeFragment = activeFragment;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
