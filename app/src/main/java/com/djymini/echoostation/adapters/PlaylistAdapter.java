package com.djymini.echoostation.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
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
import com.djymini.echoostation.dtos.PlaylistDto;
import com.djymini.echoostation.interfaces.OnItemClickListener;
import com.djymini.echoostation.interfaces.OnItemLongClickListener;
import com.djymini.echoostation.interfaces.OnMusicMenuClickListener;
import com.djymini.echoostation.utilities.SortOptionAlbum;
import com.djymini.echoostation.utilities.UiUtilities;
import com.l4digital.fastscroll.FastScroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> implements FastScroller.SectionIndexer {
    private final List<PlaylistDto> playlists = new ArrayList<>();
    private final Set<PlaylistDto> selectedItems = new HashSet<>();
    private OnMusicMenuClickListener menuClickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;

    public PlaylistAdapter(){}

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void submitList(List<PlaylistDto> newPlaylists) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return playlists.size();
            }

            @Override
            public int getNewListSize() {
                return newPlaylists.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return playlists.get(oldItemPosition).id == newPlaylists.get(newItemPosition).id;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return playlists.get(oldItemPosition).equals(newPlaylists.get(newItemPosition));
            }
        });

        playlists.clear();
        playlists.addAll(newPlaylists);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistDto playlist = playlists.get(position);
        holder.playlistName.setText(playlist.name);
        holder.trackCounter.setText(String.valueOf(playlist.tracksNumber));

        if(playlist.tracksNumber >= 1){
            String[] coverPlaylists = playlist.coverList.split(",");
            Set<String> set = new LinkedHashSet<>(Arrays.asList(coverPlaylists));
            List<String> coverForDisplay = new ArrayList<>(set);
            if (coverForDisplay.size() >= 4) {
                holder.playlistCover1.setVisibility(View.GONE);
                holder.playlistCover2.setVisibility(View.VISIBLE);
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(0)), R.drawable.echoostation_placeholder_album_3x, holder.playlistCover2Image1, holder.itemView.getContext());
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(1)), R.drawable.echoostation_placeholder_album_3x, holder.playlistCover2Image2, holder.itemView.getContext());
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(2)), R.drawable.echoostation_placeholder_album_3x, holder.playlistCover2Image3, holder.itemView.getContext());
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(3)), R.drawable.echoostation_placeholder_album_3x, holder.playlistCover2Image4, holder.itemView.getContext());
            }else {
                holder.playlistCover1.setVisibility(View.VISIBLE);
                holder.playlistCover2.setVisibility(View.GONE);
                UiUtilities.displayImageWithGlide(Uri.parse(coverForDisplay.get(0)), R.drawable.echoostation_placeholder_album_3x, holder.playlistCover1, holder.itemView.getContext());
            }
        }else {
            holder.playlistCover1.setVisibility(View.VISIBLE);
            holder.playlistCover2.setVisibility(View.GONE);
            UiUtilities.displayImageWithGlide(R.drawable.echoostation_placeholder_playlist_3x, R.drawable.echoostation_placeholder_album_3x, holder.playlistCover1, holder.itemView.getContext());
        }

        /*holder.menuButton.setOnClickListener(v -> {
            if (menuClickListener != null) {
                menuClickListener.onMenuClick(album, holder.menuButton);
            }
        });*/

        holder.itemView.setBackgroundColor(
                selectedItems.contains(playlist) ?
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
        return playlists.size();
    }

    public List<PlaylistDto> getCurrentList(){
        return playlists;
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        final ImageView playlistCover1;
        final GridLayout playlistCover2;
        final ImageView playlistCover2Image1;
        final ImageView playlistCover2Image2;
        final ImageView playlistCover2Image3;
        final ImageView playlistCover2Image4;
        final ImageButton menuButton;
        final TextView playlistName;
        final TextView trackCounter;


        PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistCover1 = itemView.findViewById(R.id.playlist_cover);
            playlistCover2 = itemView.findViewById(R.id.playlist_cover2);
            playlistCover2Image1 = itemView.findViewById(R.id.playlist_cover2_image1);
            playlistCover2Image2 = itemView.findViewById(R.id.playlist_cover2_image2);
            playlistCover2Image3 = itemView.findViewById(R.id.playlist_cover2_image3);
            playlistCover2Image4 = itemView.findViewById(R.id.playlist_cover2_image4);
            menuButton = itemView.findViewById(R.id.item_menu_button);
            playlistName = itemView.findViewById(R.id.playlist_name);
            trackCounter = itemView.findViewById(R.id.tracks_counter);
        }
    }

    public void setOnMusicMenuClickListener(OnMusicMenuClickListener listener) {
        this.menuClickListener = listener;
    }

    public void toggleSelection(PlaylistDto item) {
        int index = playlists.indexOf(item);
        if (index == -1) return;

        if (selectedItems.contains(item)) {
            selectedItems.remove(item);
        } else {
            selectedItems.add(item);
        }
        notifyItemChanged(index);
    }

    public void clearSelection() {
        Set<PlaylistDto> oldSelection = new HashSet<>(selectedItems);
        selectedItems.clear();

        for (PlaylistDto item : oldSelection) {
            int index = playlists.indexOf(item);
            if (index != -1) {
                notifyItemChanged(index);
            }
        }
    }

    public Set<PlaylistDto> getSelectedItems() {
        return selectedItems;
    }

    @NonNull
    @Override
    public String getSectionText(int position) {
        if (playlists.isEmpty() || position < 0 || position >= playlists.size()) {
            return "";
        }

        PlaylistDto playlist = playlists.get(position);
        return playlist.name != null && !playlist.name.isEmpty()
                ? playlist.name.substring(0, 1).toUpperCase()
                : "#";
    }
}
