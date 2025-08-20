package com.djymini.echoostation.interfaces;

import android.view.View;

import com.djymini.echoostation.dtos.MusicDto;

public interface OnMusicMenuClickListener {
    void onMenuClick(MusicDto music, View anchorView);
}

