package com.djymini.echoostation.ui;

import static com.djymini.echoostation.utilities.HomeFragmentContants.homeImageButtonListMix;
import static com.djymini.echoostation.utilities.HomeFragmentContants.homeImageButtonListMix2;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.HomeImageButtonAdapter;
import com.djymini.echoostation.adapters.PlaylistAdapter2;
import com.djymini.echoostation.adapters.TagAdapter;
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.daos.MusicDao;
import com.djymini.echoostation.daos.MusicTagDao;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.dtos.PlaylistDto;
import com.djymini.echoostation.entities.Playlist;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;
import com.djymini.echoostation.services.GenreService;
import com.djymini.echoostation.services.MusicService;
import com.djymini.echoostation.services.StatisticService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class MusicPlayerDialogManager {
    List<PlaylistDto> playlistDtoList;
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
        ImageButton addPlaylistButton = dialog.findViewById(R.id.add_playlist);
        RecyclerView recyclerView2 = dialog.findViewById(R.id.recycler_view_playlist);

        title.setText(musicDto.title);

        setupRecyclerTag(recyclerView, musicDto, mainActivity);
        mainActivity.loaderMediaViewModel.loadPlaylists().observe(mainActivity, playlists -> {
            playlistDtoList = new ArrayList<>(playlists);
            setupRecyclerPlaylist(playlistDtoList, recyclerView2, musicDto, mainActivity);
        });

        addPlaylistButton.setOnClickListener(v -> showAddPlaylistDialog());

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
        TagAdapter tagAdapter = new TagAdapter(homeImageButtonListMix2, musicDto, executor, mainActivity);
        RecyclerViewHelper.setupRecyclerViewGrid(recyclerView, context, tagAdapter, 5, false);
    }

    private void setupRecyclerPlaylist(List<PlaylistDto> playlistDtoList, RecyclerView recyclerView, MusicDto musicDto, MainActivity mainActivity){
        PlaylistAdapter2 playlistAdapter2 = new PlaylistAdapter2(playlistDtoList, musicDto, mainActivity, executor);
        RecyclerViewHelper.setupRecyclerViewLinear(recyclerView, context, playlistAdapter2, LinearLayoutManager.VERTICAL, true);
    }

    public void showAddPlaylistDialog() {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_create_playlist);

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        EditText editName = dialog.findViewById(R.id.edit_name);
        Button cancelButton = dialog.findViewById(R.id.btn_cancel);
        Button createButton = dialog.findViewById(R.id.btn_save);

        dialogTitle.setText("Créer une playlist");

        createButton.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();

            executor.execute(() -> {
                MainActivity mainActivity = (MainActivity)activity;
                long statisticId = mainActivity.dbService.getStatisticService().createStatistic();
                Playlist newPlaylist = new Playlist(newName, statisticId);
                mainActivity.dbService.getPlaylistDao().insertAll(newPlaylist);

                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Playlist crée", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
