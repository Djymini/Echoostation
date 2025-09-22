package com.djymini.echoostation.fragments.mediaDetailFragment;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.adapters.MusicAlbumAdapter;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.helpers.MediaItemHelper;
import com.djymini.echoostation.helpers.RecyclerViewHelper;
import com.djymini.echoostation.utilities.Constants;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.djymini.echoostation.utilities.UiUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MediaDetailFragment<E, T extends RecyclerView.Adapter<?>> extends Fragment {
    public MainActivity main;
    public ExecutorService executor;
    public ActionMode actionMode;

    public E media;
    public List<MusicDto> musicList = new ArrayList<>();

    public RecyclerView recyclerView;
    public T adapter;

    public int fragmentId;

    public ImageView backgroundImage;
    public Button playButton, shuffleButton;
    public TextView artistName, numberTrack, durationTotal;

    public void bindView(View view){
        backgroundImage = view.findViewById(R.id.background_image);
        playButton = view.findViewById(R.id.play_button);
        shuffleButton = view.findViewById(R.id.shuffle_button);
        durationTotal = view.findViewById(R.id.duration_total);
        numberTrack = view.findViewById(R.id.number_tracks);
        artistName = view.findViewById(R.id.artist_name);
        recyclerView = view.findViewById(R.id.recycler_view_main);
    }

    public void setText() {}

    public void setArtistImage(String pathImage){
        String photoPath = pathImage;
        File file = null;

        if (photoPath != null && !photoPath.isEmpty()) {
            file = new File(photoPath);
            if (!file.exists()) file = null;
        }
        if (file != null){
            UiUtilities.displayImageWithGlide(file, R.drawable.echoostation_placeholder_artist_3x, backgroundImage, requireContext());
        }else {
            UiUtilities.displayImageWithGlide(R.drawable.echoostation_placeholder_music_3x, R.drawable.echoostation_placeholder_artist_3x, backgroundImage, requireContext());
        }
    }

    public void setButton(List<MusicDto> adapterMusicList){
        playButton.setOnClickListener(v -> MediaItemHelper.playPlaylist(adapterMusicList.get(0), main, requireContext()));

        shuffleButton.setOnClickListener(v -> {
            int musicPosition = (int) ( Math.random() * musicList.size()-1 );
            MediaItemHelper.shufflePlaylist(adapterMusicList.get(musicPosition), main, requireContext());
        });
    }

    public void setupUi(String imagePath, List<MusicDto> adapterMusicList){
        setText();
        setArtistImage(imagePath);
        setButton(adapterMusicList);
    }

    public void setupRecyclerView(T newAdapter) {
        adapter = newAdapter;
        RecyclerViewHelper.setupRecyclerViewLinear(recyclerView, getContext(), adapter, LinearLayoutManager.VERTICAL, true);
    }

    public void setNumberTrackAndDuration(String numberTrackText, String durationTotalText){
        numberTrack.setText(numberTrackText);
        durationTotal.setText(durationTotalText);
    }
}
