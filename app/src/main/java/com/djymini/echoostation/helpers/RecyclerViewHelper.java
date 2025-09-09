package com.djymini.echoostation.helpers;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djymini.echoostation.adapters.HomeImageButtonAdapter;

public class RecyclerViewHelper<T> {
    public static <T extends RecyclerView.Adapter<?>> void setupRecyclerViewGrid(RecyclerView recyclerView, Context context, T adapter, int columnNumber, boolean notScrollable) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, columnNumber));
        if(notScrollable){
            recyclerView.setClipToPadding(false);
            recyclerView.setClipChildren(false);
        }
        recyclerView.setAdapter(adapter);
    }

    public static <T extends RecyclerView.Adapter<?>> void setupRecyclerViewLinear(RecyclerView recyclerView, Context context, T adapter, int direction, boolean notScrollable) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, direction, false);
        recyclerView.setLayoutManager(layoutManager);
        if(notScrollable){
            recyclerView.setClipToPadding(false);
            recyclerView.setClipChildren(false);
        }
        recyclerView.setAdapter(adapter);
    }

}
