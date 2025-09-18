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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistAdapter2.PlaylistView2Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistView2Holder holder, int position) {
        PlaylistDto playlist = playlists.get(position);
        holder.playlistName.setText(playlist.name);
        holder.trackCounter.setText(String.valueOf(playlist.tracksNumber));

        if(playlist.tracksNumber >= 1){
            String[] coverPlaylists = playlist.coverList.split(",");
            Set<String> set = new LinkedHashSet<>(Arrays.asList(coverPlaylists));
            List<String> coverForDisplay = new ArrayList<>(set);
            if (coverForDisplay.size() > 4) {
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

        holder.itemView.setOnClickListener(v -> {
            executor.execute(() ->{
                main.dbService.getMusicDao().insertMusicPlaylist(playlist.id, musicDto.id);
            });
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistView2Holder extends RecyclerView.ViewHolder {
        final ImageView playlistCover1;
        final GridLayout playlistCover2;
        final ImageView playlistCover2Image1;
        final ImageView playlistCover2Image2;
        final ImageView playlistCover2Image3;
        final ImageView playlistCover2Image4;
        final ImageButton menuButton;
        final TextView playlistName;
        final TextView trackCounter;


        PlaylistView2Holder(@NonNull View itemView) {
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
}
