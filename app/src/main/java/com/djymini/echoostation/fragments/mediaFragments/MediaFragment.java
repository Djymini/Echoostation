package com.djymini.echoostation.fragments.mediaFragments;

import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MediaFragment <E, A> extends Fragment{
    public MainActivity main;
    public ExecutorService executor;

    public List<E> mediaList;
    public A adapter;
    public boolean changeTheTitle = false;

    public FastScrollRecyclerView recyclerView;
    public TextView counterView;
    public Spinner spinner;
    public String search;
    public ImageButton shuffleButton;
    public ActionMode actionMode;

    public void initializeFragment(){
        main = (MainActivity) getActivity();
        executor = Executors.newSingleThreadExecutor();
    }

    public void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_media);
        counterView = view.findViewById(R.id.number_media);
        spinner = view.findViewById(R.id.spinner);
        shuffleButton = view.findViewById(R.id.shuffle_button);
    }

    public void setupRecyclerView(){}

    public void loadMedias(){
        main.navigator.modifyTitle(getString(R.string.library_fragment));
    }

    public void sortAndDisplayMedias(int position) {}

    public void displayMedias() {}

    public static <E> List<E> fullTextSearchByLogicalOr(List<E> list, String keyword, List<Function<E, String>> mappers) {
        if (keyword == null || keyword.trim().isEmpty()) return list;

        String lowerKeyword = keyword.toLowerCase();

        return list.stream().filter(e -> mappers.stream().anyMatch(
                        mapper -> containsIgnoreCase(mapper.apply(e), lowerKeyword)
                ))
                .collect(Collectors.toList());
    }

    private static boolean containsIgnoreCase(String text, String keyword) {
        return text != null && keyword != null && text.toLowerCase().contains(keyword);
    }

    public void updateActionModeTitle(int count) {
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(count + getString(R.string.item_selected));
        }
    }
}
