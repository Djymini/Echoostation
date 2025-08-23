package com.djymini.echoostation.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.interfaces.OnItemClickListener;
import com.djymini.echoostation.interfaces.OnItemLongClickListener;
import com.djymini.echoostation.interfaces.OnMusicMenuClickListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private final List<MusicDto> musics = new ArrayList<>();
    private final Set<MusicDto> selectedItems = new HashSet<>();
    private OnMusicMenuClickListener menuClickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;

    public MusicAdapter() {
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
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicDto music = musics.get(position);
        holder.title.setText(music.title);
        holder.artist.setText(music.artistName);

        String durationStr = formatDuration(music.duration);
        holder.duration.setText(durationStr);

        Uri albumArt = music.getCover();
        Glide.with(holder.itemView.getContext())
                .load(albumArt)
                .placeholder(R.drawable.echoostation_placeholder_music_3x)
                .error(R.drawable.echoostation_placeholder_music_3x)
                .into(holder.cover);

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

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView artist;
        final TextView duration;
        final ImageView cover;
        final ImageButton menuButton;

        MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.music_title);
            artist = itemView.findViewById(R.id.music_artist);
            duration = itemView.findViewById(R.id.music_duration);
            cover = itemView.findViewById(R.id.image_cover);
            menuButton = itemView.findViewById(R.id.item_menu_button);
        }
    }

    private String formatDuration(long durationMs) {
        long minutes = (durationMs / 1000) / 60;
        long seconds = (durationMs / 1000) % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
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