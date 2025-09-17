package com.djymini.echoostation.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.dtos.MusicDto;
import com.djymini.echoostation.ui.HomeImageButton;
import com.djymini.echoostation.ui.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    private List<HomeImageButton> homeImageButtonList;
    private Map<String, Tag> tagMap;
    private MusicDto musicDto;
    private MainActivity main;
    private ExecutorService executor;

    public TagAdapter(List<HomeImageButton> homeImageButtonList, MusicDto musicDto, ExecutorService executor, MainActivity main) {
        this.homeImageButtonList = homeImageButtonList;
        this.musicDto = musicDto;
        this.main = main;
        this.executor = executor;

        this.tagMap = new HashMap<>();

        tagMap.put("Good vibe", new Tag("Good vibe", musicDto.goodVibeMusic, music -> {
            music.goodVibeMusic = !music.goodVibeMusic;
            this.main.dbService.getMusicTagDao().updateGoodVibeTag(music.id, music.goodVibeMusic);
        }));
        tagMap.put("Motivation", new Tag("Motivation", musicDto.motivationMusic, music -> {
            music.motivationMusic = !music.motivationMusic;
            this.main.dbService.getMusicTagDao().updateMotivationTag(music.id, music.motivationMusic);
        }));
        tagMap.put("Fête", new Tag("Fête", musicDto.partyMusic, music -> {
            music.partyMusic = !music.partyMusic;
            this.main.dbService.getMusicTagDao().updatePartyTag(music.id, music.partyMusic);
        }));
        tagMap.put("Détente", new Tag("Détente", musicDto.chillMusic, music -> {
            music.chillMusic = !music.chillMusic;
            this.main.dbService.getMusicTagDao().updateChillTag(music.id, music.chillMusic);
        }));
        tagMap.put("Nuit", new Tag("Nuit",musicDto.nightMusic, music -> {
            music.nightMusic = !music.nightMusic;
            this.main.dbService.getMusicTagDao().updateNightTag(music.id, music.nightMusic);
        }));
        tagMap.put("Tristesse", new Tag("Tristesse", musicDto.sadMusic, music -> {
            music.sadMusic = !music.sadMusic;
            this.main.dbService.getMusicTagDao().updateSadTag(music.id, music.sadMusic);
        }));
        tagMap.put("Gaming", new Tag("Gaming", musicDto.gamingMusic, music -> {
            music.gamingMusic = !music.gamingMusic;
            this.main.dbService.getMusicTagDao().updateGamingTag(music.id, music.gamingMusic);
        }));
        tagMap.put("Matin", new Tag("Matin", musicDto.morningMusic, music -> {
            music.morningMusic = !music.morningMusic;
            this.main.dbService.getMusicTagDao().updateMorningTag(music.id, music.morningMusic);
        }));
        tagMap.put("Ménage", new Tag("Ménage", musicDto.walkMusic, music -> {
            music.walkMusic = !music.walkMusic;
            this.main.dbService.getMusicTagDao().updateWalkTag(music.id, music.walkMusic);
        }));
        tagMap.put("Conduite", new Tag("Conduite", musicDto.driveMusic, music -> {
            music.driveMusic = !music.driveMusic;
            this.main.dbService.getMusicTagDao().updateDriveTag(music.id, music.driveMusic);
        }));
        tagMap.put("Travail", new Tag("Travail", musicDto.workMusic, music -> {
            music.workMusic = !music.workMusic;
            this.main.dbService.getMusicTagDao().updateWorkTag(music.id, music.workMusic);
        }));
        tagMap.put("Réflexion", new Tag("Réflexion", musicDto.mindMusic, music -> {
            music.mindMusic = !music.mindMusic;
            this.main.dbService.getMusicTagDao().updateMindTag(music.id, music.mindMusic);
        }));
    }

    @NonNull
    @Override
    public TagAdapter.TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new TagAdapter.TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.TagViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(homeImageButtonList.get(position).getImageButton())
                .into(holder.imageButton);

        ColorStateList tint = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), tagMap.get(homeImageButtonList.get(position).getNameButton()).isActive ? homeImageButtonList.get(position).getColor() : R.color.disableText));
        holder.imageButton.setImageTintList(tint);

        holder.imageButton.setOnClickListener(v -> {
            executor.execute(() ->{
                tagMap.get(homeImageButtonList.get(position).getNameButton()).changeTagValue(musicDto);
                ColorStateList tint2 = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), tagMap.get(homeImageButtonList.get(position).getNameButton()).isActive ? homeImageButtonList.get(position).getColor() : R.color.disableText));
                holder.imageButton.setImageTintList(tint2);
            });
        });
    }

    @Override
    public int getItemCount() {
        return homeImageButtonList.size();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        TagViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.image_button);
        }
    }
}
