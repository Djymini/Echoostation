package com.djymini.echoostation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.interfaces.OnItemClickListener;
import com.djymini.echoostation.interfaces.OnItemLongClickListener;
import com.djymini.echoostation.interfaces.OnMusicMenuClickListener;
import com.djymini.echoostation.utilities.TimeUtilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MusicAlbumAdapter extends RecyclerView.Adapter<MusicAlbumAdapter.MusicAlbumViewHolder> {
    private final List<MusicDto> musics = new ArrayList<>();
    private final Set<MusicDto> selectedItems = new HashSet<>();
    private OnMusicMenuClickListener menuClickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;

    public MusicAlbumAdapter() {
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void submitList(List<MusicDto> newMusics) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return musics.size();
            }

            @Override
            public int getNewListSize() {
                return newMusics.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return musics.get(oldItemPosition).id == newMusics.get(newItemPosition).id;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return musics.get(oldItemPosition).equals(newMusics.get(newItemPosition));
            }
        });

        musics.clear();
        musics.addAll(newMusics);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MusicAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_album, parent, false);
        return new MusicAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAlbumViewHolder holder, int position) {
        MusicDto music = musics.get(position);
        holder.title.setText(music.title);
        holder.artist.setText(music.artistName);
        holder.numberTrack.setText(String.valueOf(music.track));

        String durationStr = TimeUtilities.formatDuration(music.duration);

        holder.menuButton.setOnClickListener(v -> {
            if (menuClickListener != null) {
                menuClickListener.onMenuClick(music, holder.menuButton);
            }
        });

        holder.itemView.setBackgroundColor(
                selectedItems.contains(music) ?
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.selectedColor) :
                        ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent)
        );

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) longClickListener.onItemLongClick(position);
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return musics.size();
    }

    public List<MusicDto> getCurrentList(){
        return musics;
    }

    public static class MusicAlbumViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView artist;
        final TextView numberTrack;
        final ImageButton menuButton;

        MusicAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.music_title);
            artist = itemView.findViewById(R.id.music_artist);
            numberTrack = itemView.findViewById(R.id.music_number);
            menuButton = itemView.findViewById(R.id.item_menu_button);
        }
    }

    public void setOnMusicMenuClickListener(OnMusicMenuClickListener listener) {
        this.menuClickListener = listener;
    }

    public void toggleSelection(MusicDto item) {
        int index = musics.indexOf(item);
        if (index == -1) return;

        if (selectedItems.contains(item)) {
            selectedItems.remove(item);
        } else {
            selectedItems.add(item);
        }
        notifyItemChanged(index);
    }

    public void clearSelection() {
        Set<MusicDto> oldSelection = new HashSet<>(selectedItems);
        selectedItems.clear();

        for (MusicDto item : oldSelection) {
            int index = musics.indexOf(item);
            if (index != -1) {
                notifyItemChanged(index);
            }
        }
    }

    public Set<MusicDto> getSelectedItems() {
        return selectedItems;
    }
}