package com.djymini.echoostation.ui;

import static com.djymini.echoostation.utilities.HomeFragmentContants.homeImageButtonListMix;
import static com.djymini.echoostation.utilities.HomeFragmentContants.homeImageButtonListPrimary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.HomeImageButtonAdapter;
import com.djymini.echoostation.adapters.TagAdapter;
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.MusicTagDao;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class MusicPlayerDialogManager {
    private final Activity activity;
    private final MusicDao musicDao;
    private final MusicTagDao musicTagDao;
    private final MusicService musicService;
    private final ExecutorService executor;
    private final Context context;

    public MusicPlayerDialogManager(Activity activity, MainActivity mainActivity, ExecutorService executor, Context context) {
        this.activity = activity;
        this.musicDao = mainActivity.dbService.getMusicDao();
        this.musicTagDao = mainActivity.dbService.getMusicTagDao();
        this.musicService = mainActivity.dbService.getMusicService();
        this.executor = executor;
        this.context = context;
    }

    public void showBottomDialog(MusicDto musicDto, MainActivity mainActivity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout_tag_playlist);

        TextView title = dialog.findViewById(R.id.title_dialog);
        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_view_tag);

        title.setText(musicDto.title);

        Log.d("MusicDialogManager", musicDto.toString());
        setupRecyclerTag(recyclerView, musicDto, mainActivity);

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            window.setGravity(Gravity.BOTTOM);
        }

    }

    private void setupRecyclerTag(RecyclerView recyclerView, MusicDto musicDto, MainActivity mainActivity){
        TagAdapter tagAdapter = new TagAdapter(homeImageButtonListMix, musicDto, executor, mainActivity);
        RecyclerViewHelper.setupRecyclerViewGrid(recyclerView, context, tagAdapter, 4, false);
    }
}
