package com.djymini.echoostation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.ArtistDto;
import com.djymini.echoostation.entities.Genre;
import com.djymini.echoostation.interfaces.OnItemClickListener;
import com.djymini.echoostation.interfaces.OnItemLongClickListener;
import com.djymini.echoostation.interfaces.OnMusicMenuClickListener;
import com.djymini.echoostation.utilities.SortOptionArtist;
import com.djymini.echoostation.utilities.SortOptionGenre;
import com.l4digital.fastscroll.FastScroller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> implements FastScroller.SectionIndexer {
    private final List<Genre> genres = new ArrayList<>();
    private final Set<Genre> selectedItems = new HashSet<>();
    private SortOptionGenre currentSort = SortOptionGenre.GENRE_ASC;
    private OnMusicMenuClickListener menuClickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;

    public GenreAdapter(){}

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void submitList(List<Genre> newGenres) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return genres.size();
            }

            @Override
            public int getNewListSize() {
                return newGenres.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return genres.get(oldItemPosition).id == newGenres.get(newItemPosition).id;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return genres.get(oldItemPosition).equals(newGenres.get(newItemPosition));
            }
        });

        genres.clear();
        genres.addAll(newGenres);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.genreName.setText(genre.name);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public List<Genre> getCurrentList(){
        return genres;
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        final TextView genreName;

        GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreName = itemView.findViewById(R.id.genre_name);
        }
    }

    public void setSortOption(SortOptionGenre option) {
        this.currentSort = option;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public String getSectionText(int position) {
        if (genres.isEmpty() || position < 0 || position >= genres.size()) {
            return "";
        }

        Genre genre = genres.get(position);
        return genre.name != null && !genre.name.isEmpty()
                ? genre.name.substring(0, 1).toUpperCase()
                : "#";
    }
}
