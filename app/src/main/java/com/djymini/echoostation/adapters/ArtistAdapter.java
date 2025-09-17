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
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.entities.Artist;
import com.djymini.echoostation.interfaces.OnItemClickListener;
import com.djymini.echoostation.interfaces.OnItemLongClickListener;
import com.djymini.echoostation.interfaces.OnMusicMenuClickListener;
import com.djymini.echoostation.utilities.SortOption;
import com.djymini.echoostation.utilities.SortOptionArtist;
import com.djymini.echoostation.utilities.TimeUtilities;
import com.l4digital.fastscroll.FastScroller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> implements FastScroller.SectionIndexer {
    private final List<ArtistDto> artists = new ArrayList<>();
    private final Set<ArtistDto> selectedItems = new HashSet<>();
    private SortOptionArtist currentSort = SortOptionArtist.ARTIST_ASC;
    private OnMusicMenuClickListener menuClickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;

    public ArtistAdapter(){}

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void submitList(List<ArtistDto> newArtists) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return artists.size();
            }

            @Override
            public int getNewListSize() {
                return newArtists.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return artists.get(oldItemPosition).id == newArtists.get(newItemPosition).id;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return artists.get(oldItemPosition).equals(newArtists.get(newItemPosition));
            }
        });

        artists.clear();
        artists.addAll(newArtists);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        ArtistDto artist = artists.get(position);
        holder.artistName.setText(artist.name);

        String photoPath = artist.photoPath;
        File file = null;

        if (photoPath != null && !photoPath.isEmpty()) {
            file = new File(photoPath);
            if (!file.exists()) file = null; // fallback si fichier supprimé
        }

        Glide.with(holder.itemView.getContext())
                .load(file != null ? file : R.drawable.echoostation_placeholder_artist_3x)
                .placeholder(R.drawable.echoostation_placeholder_artist_3x)
                .error(R.drawable.echoostation_placeholder_artist_3x)
                .into(holder.artistImage);

        holder.itemView.setBackgroundColor(
                selectedItems.contains(artist) ?
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.selectedColor) :
                        ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent)
        );

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public List<ArtistDto> getCurrentList(){
        return artists;
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        final TextView artistName;
        final ImageView artistImage;

        ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artist_name);
            artistImage = itemView.findViewById(R.id.artist_image);
        }
    }

    public void setSortOption(SortOptionArtist option) {
        this.currentSort = option;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public String getSectionText(int position) {
        if (artists.isEmpty() || position < 0 || position >= artists.size()) {
            return "";
        }

        ArtistDto artist = artists.get(position);
        return artist.name != null && !artist.name.isEmpty()
                ? artist.name.substring(0, 1).toUpperCase()
                : "#";
    }
}
