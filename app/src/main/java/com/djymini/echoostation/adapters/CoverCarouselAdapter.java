package com.djymini.echoostation.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.R;

import java.util.List;

public class CoverCarouselAdapter extends RecyclerView.Adapter<CoverCarouselAdapter.CoverViewHolder> {
    private final List<MediaItem> items;
    private final Context context;

    public CoverCarouselAdapter(Context context, List<MediaItem> items) {
        this.context = context;
        this.items = items;
    }

    public void setItems(List<MediaItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new CoverViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull CoverViewHolder holder, int position) {
        MediaItem item = items.get(position);
        Glide.with(context)
                .load(item.mediaMetadata.artworkUri)
                .placeholder(R.drawable.echoostation_placeholder_album_3x)
                .override(350, 350)
                .error(R.drawable.echoostation_placeholder_album_3x)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CoverViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CoverViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}

