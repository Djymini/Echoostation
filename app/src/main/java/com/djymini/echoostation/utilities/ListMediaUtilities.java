package com.djymini.echoostation.utilities;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ListMediaUtilities {
    public static <T> void displayList(List<T> list, ExecutorService executor, FragmentActivity activity, Runnable runnable) {
        if (list == null) return;
        executor.execute(() -> activity.runOnUiThread(runnable));
    }
}
