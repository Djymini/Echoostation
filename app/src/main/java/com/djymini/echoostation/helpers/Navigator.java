package com.djymini.echoostation.helpers;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

import com.djymini.echoostation.R;
import com.djymini.echoostation.fragments.LibraryFragment;
import com.djymini.echoostation.fragments.MusicPlayerFragment;
import com.djymini.echoostation.fragments.TrueMusicPlayer;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class Navigator {
    private final FragmentManager fragmentManager;
    private final FragmentInitializer fragmentInitializer;
    private final Toolbar toolbar;
    private final BottomNavigationView bottomNavMenu;
    //private final FrameLayout miniPlayerContainer;
    private final MusicPlayerViewModel playerViewModel;
    public final TrueMusicPlayer trueMusicPlayer;
    private final Context context;

    private Fragment activeFragment;

    public Navigator(FragmentManager fragmentManager, Toolbar toolbar, BottomNavigationView bottomNavMenu,
                     /*FrameLayout miniPlayerContainer,*/ MusicPlayerViewModel playerViewModel, View view, LifecycleOwner lifecycleOwner, ViewModelStoreOwner storeOwner, Context context) {
        this.fragmentManager = fragmentManager;
        this.toolbar = toolbar;
        this.bottomNavMenu = bottomNavMenu;
        //this.miniPlayerContainer = miniPlayerContainer;
        this.playerViewModel = playerViewModel;
        this.fragmentInitializer = new FragmentInitializer();
        this.trueMusicPlayer = new TrueMusicPlayer(view, lifecycleOwner, storeOwner, context);
        this.context = context;
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
        //updateMiniPlayerVisibility(fragment);

        updateToolbarMenu(fragment);
    }

    public void setupTrueMusicPlayer(View playerBottomSheet) {
        trueMusicPlayer.setMainContent((LinearLayout) playerBottomSheet);
        trueMusicPlayer.setBottomSheetBehavior(BottomSheetBehavior.from(playerBottomSheet));
        trueMusicPlayer.getBottomSheetBehavior().setPeekHeight(trueMusicPlayer.dpToPx(100, context));
        trueMusicPlayer.getBottomSheetBehavior().setHideable(false);

        trueMusicPlayer.getBottomSheetBehavior().addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    trueMusicPlayer.getFullContent().setVisibility(View.VISIBLE);
                    trueMusicPlayer.showFullContent();
                    toolbar.setVisibility(View.GONE);
                    bottomNavMenu.setVisibility(View.GONE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    trueMusicPlayer.getFullContent().setVisibility(View.GONE);
                    trueMusicPlayer.hideFullContent();
                    toolbar.setVisibility(View.VISIBLE);
                    bottomNavMenu.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                // slideOffset: 0 = mini, 1 = full
                float scale = 64f + (286f - 64f) * slideOffset;
                trueMusicPlayer.getPlayerCover().getLayoutParams().width = trueMusicPlayer.dpToPx((int) scale, context);
                trueMusicPlayer.getPlayerCover().getLayoutParams().height = trueMusicPlayer.dpToPx((int) scale, context);
                trueMusicPlayer.getPlayerCover().requestLayout();

                // Texte scaling (optionnel)
                trueMusicPlayer.getPlayerTitle().setTextSize(14 + 6 * slideOffset);  // 14sp -> 18sp
                trueMusicPlayer.getPlayerArtist().setTextSize(12 + 2 * slideOffset); // 12sp -> 14sp
                trueMusicPlayer.getPlayerTitle().setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        14 + (20 - 14) * slideOffset);
                trueMusicPlayer.getPlayerArtist().setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        12 + (14 - 12) * slideOffset);
            }
        });

        // Clique sur le player pour l'ouvrir
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
            if (fragmentForLoad != null) {
                showFragment(fragmentForLoad);
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
        showFragment(libraryFragment);
    }

    public void modifyTitle(String newText) {
        toolbar.setTitle(newText);
    }

    /*public void updateMiniPlayerVisibility(Fragment fragment) {
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
    }*/

    public void updateToolbarMenu(Fragment fragment) {
        toolbar.getMenu().clear();

        if (fragment instanceof LibraryFragment) {
            toolbar.inflateMenu(R.menu.action_bar); // ta searchbar
        }
    }

}
