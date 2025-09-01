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
import com.djymini.echoostation.dtos.AlbumDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Album;
import com.djymini.echoostation.interfaces.OnItemClickListener;
import com.djymini.echoostation.interfaces.OnItemLongClickListener;
import com.djymini.echoostation.interfaces.OnMusicMenuClickListener;
import com.djymini.echoostation.utilities.TimeUtilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private final List<AlbumDto> albums = new ArrayList<>();
    private final Set<AlbumDto> selectedItems = new HashSet<>();
    private OnMusicMenuClickListener menuClickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;

    public AlbumAdapter(){}

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void submitList(List<AlbumDto> newAlbums) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return albums.size();
            }

            @Override
            public int getNewListSize() {
                return newAlbums.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return albums.get(oldItemPosition).id == newAlbums.get(newItemPosition).id;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return albums.get(oldItemPosition).equals(newAlbums.get(newItemPosition));
            }
        });

        albums.clear();
        albums.addAll(newAlbums);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        AlbumDto album = albums.get(position);
        holder.albumName.setText(album.name);
        holder.artist.setText(album.artistName);

        Uri albumArt = album.getCover();
        Glide.with(holder.itemView.getContext())
                .load(albumArt)
                .placeholder(R.drawable.echoostation_placeholder_music_3x)
                .error(R.drawable.echoostation_placeholder_music_3x)
                .into(holder.cover);

        /*holder.menuButton.setOnClickListener(v -> {
            if (menuClickListener != null) {
                menuClickListener.onMenuClick(album, holder.menuButton);
            }
        });*/

        holder.itemView.setBackgroundColor(
                selectedItems.contains(album) ?
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
        return albums.size();
    }

    public List<AlbumDto> getCurrentList(){
        return albums;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        final TextView albumName;
        final TextView artist;
        final ImageView cover;
        final ImageButton menuButton;

        AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.album_name);
            artist = itemView.findViewById(R.id.album_artist);
            cover = itemView.findViewById(R.id.album_cover);
            menuButton = itemView.findViewById(R.id.item_menu_button);
        }
    }

    public void setOnMusicMenuClickListener(OnMusicMenuClickListener listener) {
        this.menuClickListener = listener;
    }

    public void toggleSelection(AlbumDto item) {
        int index = albums.indexOf(item);
        if (index == -1) return;

        if (selectedItems.contains(item)) {
            selectedItems.remove(item);
        } else {
            selectedItems.add(item);
        }
        notifyItemChanged(index);
    }

    public void clearSelection() {
        Set<AlbumDto> oldSelection = new HashSet<>(selectedItems);
        selectedItems.clear();

        for (AlbumDto item : oldSelection) {
            int index = albums.indexOf(item);
            if (index != -1) {
                notifyItemChanged(index);
            }
        }
    }

    public Set<AlbumDto> getSelectedItems() {
        return selectedItems;
    }
}
