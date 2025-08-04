package com.djymini.echoostation.adapters;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.R;
import com.djymini.echoostation.daos.AlbumDao;
import com.djymini.echoostation.daos.ArtistDao;
import com.djymini.echoostation.entities.Music;
import com.djymini.echoostation.services.AlbumService;
import com.djymini.echoostation.services.ArtistService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private List<Music> musics = new ArrayList<>();
    private ArtistService artistService;
    private AlbumService albumService;

    public MusicAdapter(ArtistService artistService, AlbumService albumService) {
        this.artistService = artistService;
        this.albumService = albumService;
    }

    public void submitList(List<Music> newMusics) {
        musics.clear();
        musics.addAll(newMusics);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = musics.get(position);
        holder.title.setText(music.title);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Traitement en arrière-plan
            String artistName = artistService.getArtistsNameOfMusic(music.id);
            String durationStr = formatDuration(music.duration);
            Uri albumArt = albumService.getCover(music.idAlbum);

            // Mise à jour de l’UI sur le thread principal
            new Handler(Looper.getMainLooper()).post(() -> {
                holder.artist.setText(artistName);
                holder.duration.setText(durationStr);

                Glide.with(holder.itemView.getContext())
                        .load(albumArt)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.cover);
            });
        });
    }


    @Override
    public int getItemCount() {
        return musics.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist, duration;
        ImageView cover;
        ImageButton menuButton;

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
        return String.format("%02d:%02d", minutes, seconds);
    }
}