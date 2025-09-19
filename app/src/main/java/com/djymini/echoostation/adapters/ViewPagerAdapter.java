package com.djymini.echoostation.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.djymini.echoostation.fragments.mediaFragments.AlbumFragment;
import com.djymini.echoostation.fragments.mediaFragments.ArtistFragment;
import com.djymini.echoostation.fragments.mediaFragments.GenreFragment;
import com.djymini.echoostation.fragments.mediaFragments.MusicFragment;
import com.djymini.echoostation.fragments.mediaFragments.PlaylistFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1: return new AlbumFragment();
            case 2: return new ArtistFragment();
            case 3: return new GenreFragment();
            case 4: return new PlaylistFragment();
            default: return new MusicFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
