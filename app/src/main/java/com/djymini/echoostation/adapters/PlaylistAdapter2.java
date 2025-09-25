package com.djymini.echoostation.adapters;

import android.content.res.ColorStateList;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.dtos.PlaylistDto;
import com.djymini.echoostation.utilities.UiUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class PlaylistAdapter2 extends RecyclerView.Adapter<PlaylistAdapter2.PlaylistView2Holder>{
    private List<PlaylistDto> playlists;
    private MusicDto musicDto;
    private MainActivity main;
    private ExecutorService executor;

    public PlaylistAdapter2(List<PlaylistDto> playlists, MusicDto musicDto, MainActivity main, ExecutorService executor) {
        this.playlists = playlists;
        this.musicDto = musicDto;
        this.main = main;
        this.executor = executor;
    }

    @NonNull
    @Override
    public PlaylistAdapter2.PlaylistView2Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist2, parent, false);
        return new PlaylistAdapter2.PlaylistView2Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistView2Holder holder, int position) {
        PlaylistDto playlist = playlists.get(position);
        holder.playlistName.setText(playlist.name);
        executor.execute(() ->{
            ColorStateList tint;
            if(main.dbService.getPlaylistDao().existsInPlaylist(musicDto.id, playlist.id)){
                holder.checkBox.setImageResource(R.drawable.ic_echoostation_checkbox);
                tint = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),R.color.colorText));
            }else {
                holder.checkBox.setImageResource(R.drawable.ic_echoostation_checkbox_blank);
                tint = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),R.color.disableText));
            }
            holder.checkBox.setImageTintList(tint);

        });


        holder.itemView.setOnClickListener(v -> {
            executor.execute(() ->{
                ColorStateList tint;
                if(main.dbService.getPlaylistDao().existsInPlaylist(musicDto.id, playlist.id)){
                    holder.checkBox.setImageResource(R.drawable.ic_echoostation_checkbox);
                    tint = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),R.color.colorText));
                    main.dbService.getMusicDao().deleteMusicPlaylist(playlist.id, musicDto.id);
                }else {
                    holder.checkBox.setImageResource(R.drawable.ic_echoostation_checkbox_blank);
                    tint = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(),R.color.disableText));
                    main.dbService.getMusicDao().insertMusicPlaylist(playlist.id, musicDto.id);
                }
                holder.checkBox.setImageTintList(tint);
            });
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistView2Holder extends RecyclerView.ViewHolder {
        final ImageView checkBox;
        final TextView playlistName;

        PlaylistView2Holder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlist_name);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
