package com.djymini.echoostation;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class MediaLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = false;

    public MediaLinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
