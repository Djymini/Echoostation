package com.djymini.echoostation.fragments.mediaDetailFragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.MusicAlbumAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.utilities.UiUtilities;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumInfoFragment extends MediaDetailFragment<AlbumDto, MusicAlbumAdapter> {
    private static final String ARG_ALBUM = "album";
    private static final String ARG_FRAGMENT_BACK = "fragment back id";

    private int fragmentId;

    private ImageView albumImage, artistImage;
    private TextView albumName, albumDate;


    public AlbumInfoFragment() {}

    public static AlbumInfoFragment newInstance(AlbumDto newAlbum, int fragmentId) {
        AlbumInfoFragment fragment = new AlbumInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ALBUM, newAlbum);
        args.putInt(ARG_FRAGMENT_BACK, fragmentId);
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
                main.deleteManager.confirmAndDeleteSelectedMedia(selectedCopy, requireContext(), AlbumInfoFragment.this, executor);
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
            media = getArguments().getParcelable(ARG_ALBUM);
            fragmentId = getArguments().getInt(ARG_FRAGMENT_BACK);
            main = (MainActivity) getActivity();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> musicList = main.dbService.getMusicDao().getMusicDetailByAlbum(media.id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_info, container, false);
        bindView(view);
        setupRecyclerView(new MusicAlbumAdapter());
        loadMusics();
        setupUi(media.artistPhotoCover, adapter.getCurrentList());
        main.navigator.backButtonManager(fragmentId, getResources(), main, getViewLifecycleOwner(), requireContext());
        return view;
    }

    @Override
    public void bindView(View view){
        super.bindView(view);

        albumImage = view.findViewById(R.id.album_image);
        artistImage = view.findViewById(R.id.artist_image);
        albumName = view.findViewById(R.id.playlist_name);
        albumDate = view.findViewById(R.id.album_date);
    }

    @Override
    public void setupUi(String imagePath, List<MusicDto> adapterMusicList){
        super.setupUi(imagePath, adapterMusicList);
        setAlbumImage(backgroundImage);
        setAlbumImage(albumImage);
    }

    @Override
    public void setText() {
        albumName.setText(media.name);
        artistName.setText(media.artistName);
        albumDate.setText(String.valueOf(media.year));
        setNumberTrackAndDuration(
                UiUtilities.displayCounterText(musicList.size() > 1, musicList.size(), Constants.TRACK_COUNTER_PLURAL, Constants.TRACK_COUNTER),
                TimeUtilities.durationTotalWithText(musicList)
        );
    }

    private void setAlbumImage(ImageView imageView){
        UiUtilities.displayImageWithGlide(media.getCover(), R.drawable.echoostation_placeholder_album_3x, imageView, requireContext());
    }

    @Override
    public void setupRecyclerView(MusicAlbumAdapter newAdapter) {
        super.setupRecyclerView(newAdapter);
        adapter.setOnMusicMenuClickListener((music, anchorView) -> main.appInitializer.getMusicDialogManager().showBottomDialog(music));

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
                MusicDto music = adapter.getCurrentList().get(position);
                MediaItem item = MediaItemHelper.toMediaItem(music);

                main.playerViewModel.playPlaylist(requireContext(), item);
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

    private void loadMusics() {
        sortAndDisplayMusics();
    }

    private void sortAndDisplayMusics() {
        if (musicList == null) return;
        executor.execute(() -> {
            List<MusicDto> filtered = musicList;
            filtered.sort(Comparator.comparingInt(music -> music.track));

            List<MediaItem> globalPlaylist = MediaItemHelper.loadPlaylist(filtered);
            main.playerViewModel.setPlaylist(globalPlaylist);

            requireActivity().runOnUiThread(() -> adapter.submitList(filtered));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            activity.getSupportActionBar().setHomeAsUpIndicator(null);
        }
    }
}