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

        tagMap.put("Joyeux", new Tag("Joyeux", musicDto.happyMusic, music -> {
            music.happyMusic = !music.happyMusic;
            this.main.dbService.getMusicTagDao().updateHappyTag(music.id, music.happyMusic);
        }));
        tagMap.put("Motivant", new Tag("Motivant", musicDto.motivatedMusic, music -> {
            music.motivatedMusic = !music.motivatedMusic;
            this.main.dbService.getMusicTagDao().updateMotivatedTag(music.id, music.motivatedMusic);
        }));
        tagMap.put("Triste", new Tag("Triste", musicDto.sadMusic, music -> {
            music.sadMusic = !music.sadMusic;
            this.main.dbService.getMusicTagDao().updateSadTag(music.id, music.sadMusic);
        }));
        tagMap.put("Relaxant", new Tag("Relaxant", musicDto.relaxingMusic, music -> {
            music.relaxingMusic = !music.relaxingMusic;
            this.main.dbService.getMusicTagDao().updateRelaxingTag(music.id, music.relaxingMusic);
        }));
        tagMap.put("Introspectif", new Tag("Introspectif",musicDto.introspectiveMusic, music -> {
            music.introspectiveMusic = !music.introspectiveMusic;
            this.main.dbService.getMusicTagDao().updateIntrospectiveTag(music.id, music.introspectiveMusic);
        }));
        tagMap.put("Epique", new Tag("Epique", musicDto.epicMusic, music -> {
            music.epicMusic = !music.epicMusic;
            this.main.dbService.getMusicTagDao().updateEpicTag(music.id, music.epicMusic);
        }));
        tagMap.put("Travail", new Tag("Travail", musicDto.workMusic, music -> {
            music.workMusic = !music.workMusic;
            this.main.dbService.getMusicTagDao().updateWorkTag(music.id, music.workMusic);
        }));
        tagMap.put("Soirée", new Tag("Soirée", musicDto.partyMusic, music -> {
            music.partyMusic = !music.partyMusic;
            this.main.dbService.getMusicTagDao().updatePartyTag(music.id, music.partyMusic);
        }));
        tagMap.put("Balade", new Tag("Balade", musicDto.rideMusic, music -> {
            music.rideMusic = !music.rideMusic;
            this.main.dbService.getMusicTagDao().updateRideTag(music.id, music.rideMusic);
        }));
        tagMap.put("Réveil", new Tag("Réveil", musicDto.wakeMusic, music -> {
            music.wakeMusic = !music.wakeMusic;
            this.main.dbService.getMusicTagDao().updateWakeTag(music.id, music.wakeMusic);
        }));
        tagMap.put("Couché", new Tag("Couché", musicDto.sleepMusic, music -> {
            music.sleepMusic = !music.sleepMusic;
            this.main.dbService.getMusicTagDao().updateSleepTag(music.id, music.sleepMusic);
        }));
        tagMap.put("Ménage", new Tag("Ménage", musicDto.washMusic, music -> {
            music.washMusic = !music.washMusic;
            this.main.dbService.getMusicTagDao().updateWashTag(music.id, music.washMusic);
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
