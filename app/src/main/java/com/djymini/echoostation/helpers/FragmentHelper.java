package com.djymini.echoostation.helpers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;

public class FragmentHelper {
    public static void fragmentManager(MainActivity main, Fragment fragment, String fragmentName){
        FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();

        if (!fragment.isAdded()) {
            transaction.add(R.id.frame_layout, fragment);
        } else {
            transaction.show(fragment);
        }
        transaction.hide(main.navigator.getActiveFragment()).commit();

        main.navigator.modifyTitle(fragmentName);
        main.navigator.setActiveFragment(fragment);
        main.navigator.updateToolbarMenu(fragment);
    }
}
