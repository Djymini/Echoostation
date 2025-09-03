package com.djymini.echoostation.fragments;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.AlbumAdapter;
import com.djymini.echoostation.adapters.MusicAlbumAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.ui.MusicDialogManager;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.viewModels.MusicPlayerViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumInfoFragment extends Fragment {
    private static final String ARG_ALBUM = "album";
    private AlbumDto album;
    private List<MusicDto> musicList;

    private ImageView backgroundImage, albumImage, artistImage;
    private TextView albumName, albumDate, artistName, numberTrack, durationTotal;
    private Button playButton, shuffleButton;

    private MusicPlayerViewModel playerViewModel;
    private RecyclerView recyclerView;
    private List<MusicDto> currentMusicList = new ArrayList<>();
    private MusicAlbumAdapter adapter;
    private TextView musicCounterView;
    private Spinner spinner;
    private String search;
    private ActionMode actionMode;
    private ActivityResultLauncher<IntentSenderRequest> deleteMultipleLauncher;
    private List<MusicDto> musicsPendingDeletion;
    private List<MediaItem> playlist;

    private MainActivity main;
    private ExecutorService executor;

    public AlbumInfoFragment() {
    }

    public static AlbumInfoFragment newInstance(AlbumDto newAlbum) {
        AlbumInfoFragment fragment = new AlbumInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ALBUM, newAlbum);
        fragment.setArguments(args);
        return fragment;
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selection_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                Set<MusicDto> selectedCopy = new HashSet<>(adapter.getSelectedItems());
                confirmAndDeleteSelectedMusics(selectedCopy);
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            AlbumInfoFragment.this.actionMode = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            album = getArguments().getParcelable(ARG_ALBUM);
            main = (MainActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_info, container, false);
        executor = Executors.newSingleThreadExecutor();
        bindView(view);
        setupUi();
        setupRecyclerView();

        loadMusics();

        return view;
    }

    private void bindView(View view){
        backgroundImage = view.findViewById(R.id.background_image);
        albumImage = view.findViewById(R.id.album_image);
        artistImage = view.findViewById(R.id.artist_image);
        albumName = view.findViewById(R.id.album_name);
        albumDate = view.findViewById(R.id.album_date);
        artistName = view.findViewById(R.id.artist_name);
        numberTrack = view.findViewById(R.id.number_tracks);
        durationTotal = view.findViewById(R.id.duration_total);
        playButton = view.findViewById(R.id.play_button);
        shuffleButton = view.findViewById(R.id.shuffle_button);
        recyclerView = view.findViewById(R.id.recycler_view_song_album);
    }

    private void setupUi(){
        setText();
        setAlbumImage(backgroundImage);
        setAlbumImage(albumImage);
        setArtistImage();
    }

    private void setAlbumImage(ImageView imageView){
        Glide.with(requireContext())
                .load(album.getCover())
                .placeholder(R.drawable.echoostation_placeholder_music_3x)
                .error(R.drawable.echoostation_placeholder_music_3x)
                .into(imageView);
    }

    private void setArtistImage(){
        String photoPath = album.artistPhotoCover;
        File file = null;

        if (photoPath != null && !photoPath.isEmpty()) {
            file = new File(photoPath);
            if (!file.exists()) file = null; // fallback si fichier supprimé
        }
        Glide.with(requireContext())
                .load(file != null ? file : R.drawable.echoostation_placeholder_music_3x)
                .placeholder(R.drawable.echoostation_placeholder_music_3x)
                .error(R.drawable.echoostation_placeholder_music_3x)
                .into(artistImage);
    }

    private void setText(){
        albumName.setText(album.name);
        artistName.setText(album.artistName);
        albumDate.setText(String.valueOf(album.year));
        setNumberTrackAndDuration();

    }

    private void setNumberTrackAndDuration(){
        executor.execute(() -> {
            musicList = main.dbService.getMusicDao().getMusicDetailByAlbum(album.id);

            String totalMusic = musicList.size() > 1 ? String.valueOf(musicList.size()) + " morceaux" : String.valueOf(musicList.size()) + " morceau";
            String duration = "Durée : " + durationTotal();

            numberTrack.setText(totalMusic);
            durationTotal.setText(duration);
        });
    }

    private String durationTotal(){
        long durationTotal = 0;
        for(MusicDto music : musicList){
            durationTotal += music.duration;
        }

        return TimeUtilities.formatDuration(durationTotal);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAlbumAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnMusicMenuClickListener((music, anchorView) -> {
            main.appInitializer.getMusicDialogManager().showBottomDialog(music);
        });

        adapter.setOnItemLongClickListener(position -> {
            if (actionMode == null) {
                actionMode = ((AppCompatActivity) requireActivity())
                        .startSupportActionMode(actionModeCallback);
            }
            MusicDto music = adapter.getCurrentList().get(position);
            adapter.toggleSelection(music);
            updateActionModeTitle();
        });

        adapter.setOnItemClickListener(position -> {
            if (actionMode != null) {
                MusicDto music = adapter.getCurrentList().get(position);
                adapter.toggleSelection(music);
                updateActionModeTitle();
            } else {
                playerViewModel.playPlaylist(requireContext(), playlist, position);
            }
        });
    }

    private void updateActionModeTitle() {
        int count = adapter.getSelectedItems().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(count + getString(R.string.item_selected));
        }
    }

    private void confirmAndDeleteSelectedMusics(Set<MusicDto> selected) {
        if (selected.isEmpty()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete_music))
                .setMessage(getString(R.string.delete_request1) + selected.size() + getString(R.string.delete_request2))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteSelectedMusics(selected))
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void deleteSelectedMusics(Set<MusicDto> selected) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            List<Uri> uris = new ArrayList<>();
            for (MusicDto music : selected) {
                long mediaStoreId = MusicDialogManager.getMediaStoreIdFromPath(requireActivity(), music.path);
                if (mediaStoreId != -1) {
                    uris.add(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaStoreId));
                }
            }

            if (!uris.isEmpty()) {
                musicsPendingDeletion = new ArrayList<>(selected);
                IntentSender sender = MediaStore.createDeleteRequest(
                        requireActivity().getContentResolver(), uris
                ).getIntentSender();
                deleteMultipleLauncher.launch(new IntentSenderRequest.Builder(sender).build());
            }
        } else {
            executor.execute(() -> {
                for (MusicDto music : selected) {
                    long mediaStoreId = MusicDialogManager.getMediaStoreIdFromPath(requireActivity(), music.path);
                    if (mediaStoreId != -1) {
                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaStoreId);
                        int deleted = requireActivity().getContentResolver().delete(uri, null, null);
                        if (deleted > 0) {
                            main.dbService.getMusicDao().deleteById(music.id);
                        }
                    }
                }
            });
        }
    }

    private void sortAndDisplayMusics() {
        if (currentMusicList == null) return;

        executor.execute(() -> {
            List<MusicDto> filtered = currentMusicList;
            Collections.sort(filtered, (music1, music2) -> music1.track - music2.track);

            //playlist = loadPlaylist(filtered);
            requireActivity().runOnUiThread(() -> adapter.submitList(filtered));
        });
    }

    private void loadMusics() {
        main.dbService.getMusicDao().getMusicDetailByAlbumLive(album.id).observe(getViewLifecycleOwner(), musics -> {
            currentMusicList = new ArrayList<>(musics);
            sortAndDisplayMusics();
        });
    }
}