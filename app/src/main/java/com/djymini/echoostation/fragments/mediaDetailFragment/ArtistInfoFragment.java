package com.djymini.echoostation.fragments.mediaDetailFragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.AlbumAdapter;
import com.djymini.echoostation.adapters.MusicAdapter;
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.utilities.UiUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ArtistInfoFragment extends MediaDetailFragment<ArtistDto, MusicAdapter> {
    private static final String ARG_ARTIST = "artist";
    private static final String ARG_FRAGEMENT_BACK = "fragment back id";

    private final MutableLiveData<Boolean> displayMusic = new MutableLiveData<>(false);

    private List<AlbumDto> albumList = new ArrayList<>();
    private List<AlbumDto> albumApparitionList = new ArrayList<>();

    private TextView titleRecyclerViewSong, buttonDisplay, artistNumberAlbum, artistDescription;

    private LinearLayout albumContainer, albumApparitionContainer, biographyContainer, buttonContainer;

    private RecyclerView recyclerViewAlbum, recyclerViewAlbumApparition, recyclerViewBestTracks;

    private MusicAdapter adapterMusicBest;
    private AlbumAdapter adapterAlbum, adapterAlbumApparition;
    private List<MediaItem> bestListeningMusicPlaylist;

    public ArtistInfoFragment() {
    }

    public static ArtistInfoFragment newInstance(ArtistDto newArtist, int fragmentId) {
        ArtistInfoFragment fragment = new ArtistInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ARTIST, newArtist);
        args.putInt(ARG_FRAGEMENT_BACK, fragmentId);
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
                main.deleteManager.confirmAndDeleteSelectedMedia(selectedCopy, requireContext(), ArtistInfoFragment.this, executor);
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            ArtistInfoFragment.this.actionMode = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            media = getArguments().getParcelable(ARG_ARTIST);
            fragmentId = getArguments().getInt(ARG_FRAGEMENT_BACK);
            main = (MainActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_info, container, false);
        executor = Executors.newSingleThreadExecutor();
        bindView(view);
        setupRecyclerView(new MusicAdapter());
        loadMusics();
        setupUi(media.photoPath, adapter.getCurrentList());
        main.navigator.backButtonManager(fragmentId, getResources(), main, getViewLifecycleOwner(), requireContext());
        displayMusic.observe(getViewLifecycleOwner(), aBoolean -> setMusicListDisplay(aBoolean));

        return view;
    }

    @Override
    public void bindView(View view){
        super.bindView(view);
        artistNumberAlbum = view.findViewById(R.id.artist_number_album);
        artistDescription = view.findViewById(R.id.artist_description);

        recyclerViewBestTracks = view.findViewById(R.id.recycler_view_best_song);
        recyclerViewAlbum = view.findViewById(R.id.recycler_view_album);
        recyclerViewAlbumApparition = view.findViewById(R.id.recycler_view_album_apparition);

        buttonDisplay = view.findViewById(R.id.button_display_song);
        buttonContainer = view.findViewById(R.id.button_container);
        titleRecyclerViewSong = view.findViewById(R.id.title_recycler_view_song);

        albumContainer = view.findViewById(R.id.album_container);
        albumApparitionContainer = view.findViewById(R.id.album_apparition_container);
        biographyContainer = view.findViewById(R.id.biographie_container);
    }

    @Override
    public void setText() {
        artistName.setText(media.name);
        displayBiography();
    }

    @Override
    public void setButton(List<MusicDto> adapterMusicList){
        super.setButton(adapterMusicList);
        buttonDisplay.setOnClickListener(v -> displayMusic.postValue(!displayMusic.getValue()));
    }

    @Override
    public void setupRecyclerView(MusicAdapter newAdapter) {
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

        setupRecyclerViewBestSongs();
        setupRecyclerViewAlbum();
        setupRecyclerViewAlbumApparition();
    }

    private void setupRecyclerViewBestSongs() {
        adapterMusicBest = new MusicAdapter();
        RecyclerViewHelper.setupRecyclerViewLinear(recyclerViewBestTracks, getContext(), adapterMusicBest, LinearLayoutManager.VERTICAL, false);

        adapterMusicBest.setOnMusicMenuClickListener((music, anchorView) -> main.appInitializer.getMusicDialogManager().showBottomDialog(music));

        adapterMusicBest.setOnItemClickListener(position -> {
            int index = adapter.getCurrentList().indexOf(adapterMusicBest.getCurrentList().get(position));
            MusicDto music = adapter.getCurrentList().get(index);
            MediaItem item = MediaItemHelper.toMediaItem(music);

            main.playerViewModel.playPlaylist(requireContext(), item);
        });
    }

    private void setupRecyclerViewAlbum() {
        adapterAlbum = new AlbumAdapter();
        RecyclerViewHelper.setupRecyclerViewLinear(recyclerViewAlbum, getContext(), adapterAlbum, LinearLayoutManager.HORIZONTAL, true);
        adapterAlbum.setOnMusicMenuClickListener((album, anchorView) -> main.appInitializer.getMusicDialogManager().showBottomDialog(album));
        adapterAlbum.setOnItemClickListener(position -> main.navigator.showFragment(AlbumInfoFragment.newInstance(adapterAlbum.getCurrentList().get(position), R.id.library), true));
    }

    private void setupRecyclerViewAlbumApparition() {
        adapterAlbumApparition = new AlbumAdapter();
        RecyclerViewHelper.setupRecyclerViewLinear(recyclerViewAlbumApparition, getContext(), adapterAlbumApparition, LinearLayoutManager.HORIZONTAL, true);
        adapterAlbumApparition.setOnMusicMenuClickListener((album, anchorView) -> main.appInitializer.getMusicDialogManager().showBottomDialog(album));
        adapterAlbumApparition.setOnItemClickListener(position -> main.navigator.showFragment(AlbumInfoFragment.newInstance(adapterAlbumApparition.getCurrentList().get(position), R.id.library), true));
    }

    private void loadMusics() {
        main.dbService.getAlbumDao().getAllByArtistDetailLive(media.id).observe(getViewLifecycleOwner(), albums -> {
            albumList = new ArrayList<>(albums);
            artistNumberAlbum.setText(UiUtilities.displayCounterText(albumList.size() > 1, albums.size(), Constants.ALBUM_COUNTER_PLURAL, Constants.ALBUM_COUNTER));
            sortAndDisplayAlbum();
            setMusicListDisplay(displayMusic.getValue());
        });

        main.dbService.getMusicDao().getMusicDetailByArtistMostListeningLive(String.valueOf(media.id)).observe(getViewLifecycleOwner(), musics -> {
            musicList = new ArrayList<>(musics);
            setNumberTrackAndDuration(
                    TimeUtilities.durationTotal(musics),
                    UiUtilities.displayCounterText(musicList.size() > 1, musics.size(), Constants.TRACK_COUNTER_PLURAL, Constants.TRACK_COUNTER)
            );
            sortAndDisplayMusics();
            setMusicListDisplay(displayMusic.getValue());

            for (MusicDto musicDto : musics) {
                executor.execute(() -> {
                    AlbumDto albumDto = main.dbService.getAlbumDao().getAlbumDetail(musicDto.albumId);
                    boolean alreadyInList = albumList.stream().anyMatch(a -> a.id == albumDto.id) || albumApparitionList.stream().anyMatch(a -> a.id == albumDto.id);

                    if (!alreadyInList) {
                        albumApparitionList.add(albumDto);
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        setMusicListDisplay(displayMusic.getValue());
                    });
                    sortAndDisplayAlbumApparition();
                });
            }
        });
    }

    private void setMusicListDisplay(boolean displayMusic){
        if (displayMusic){
            buttonDisplay.setText("Cacher tout");
            titleRecyclerViewSong.setText("Liste des titres");
            buttonContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(!musicList.isEmpty() ? View.VISIBLE : View.GONE);

            recyclerViewBestTracks.setVisibility(View.GONE);
            albumContainer.setVisibility(View.GONE);
            albumApparitionContainer.setVisibility(View.GONE);
            biographyContainer.setVisibility(View.GONE);
        }else {
            buttonDisplay.setText("Voir tout");
            titleRecyclerViewSong.setText("Titres les plus écoutés");
            buttonContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);

            recyclerViewBestTracks.setVisibility(!musicList.isEmpty() ? View.VISIBLE : View.GONE);
            albumContainer.setVisibility(!albumList.isEmpty() ? View.VISIBLE : View.GONE);
            albumApparitionContainer.setVisibility(!albumApparitionList.isEmpty() ? View.VISIBLE : View.GONE);
            displayBiography();
        }
    }

    private void sortAndDisplayMusics() {
        if (musicList == null) return;
        executor.execute(() -> {
            List<MusicDto> filtered = musicList.stream()
                    .limit(5)
                    .collect(Collectors.toList());

            List<MediaItem> globalPlaylist = MediaItemHelper.loadPlaylist(musicList);
            main.playerViewModel.setPlaylist(globalPlaylist);

            requireActivity().runOnUiThread(() -> adapter.submitList(musicList));
            requireActivity().runOnUiThread(() -> adapterMusicBest.submitList(filtered));
        });
    }

    private void sortAndDisplayAlbum() {
        if (albumList == null) return;
        executor.execute(() -> {
            List<AlbumDto> filtered = albumList;
            filtered.sort(Comparator.comparingInt(album -> album.year));
            requireActivity().runOnUiThread(() -> adapterAlbum.submitList(filtered));
        });
    }

    private void sortAndDisplayAlbumApparition() {
        if (albumApparitionList == null) return;
        executor.execute(() -> {
            List<AlbumDto> filtered = albumApparitionList;
            filtered.sort(Comparator.comparingInt(album -> album.year));
            requireActivity().runOnUiThread(() -> adapterAlbumApparition.submitList(filtered));
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

    private void displayBiography(){
        if (media.description != null && !media.description.isEmpty()) {
            artistDescription.setText(HtmlCompat.fromHtml(media.description, HtmlCompat.FROM_HTML_MODE_LEGACY));
            artistDescription.setMovementMethod(LinkMovementMethod.getInstance());
            Log.d("displayBiography", artistDescription.getText().toString());
            if(artistDescription.getText().toString().equals("Read more on Last.fm")){
                biographyContainer.setVisibility(View.GONE);
            }else{
                biographyContainer.setVisibility(View.VISIBLE);
            }
        } else {
            biographyContainer.setVisibility(View.GONE);
        }
    }
}