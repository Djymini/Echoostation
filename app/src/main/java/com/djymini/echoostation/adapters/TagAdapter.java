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
import com.djymini.echoostation.utilities.Constants;

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

        tagMap.put(Constants.HAPPY, new Tag(Constants.HAPPY, musicDto.happyMusic, music -> {
            music.happyMusic = !music.happyMusic;
            this.main.dbService.getMusicTagDao().updateHappyTag(music.id, music.happyMusic);
        }));
        tagMap.put(Constants.MOTIVATED, new Tag(Constants.MOTIVATED, musicDto.motivatedMusic, music -> {
            music.motivatedMusic = !music.motivatedMusic;
            this.main.dbService.getMusicTagDao().updateMotivatedTag(music.id, music.motivatedMusic);
        }));
        tagMap.put(Constants.SAD, new Tag(Constants.SAD, musicDto.sadMusic, music -> {
            music.sadMusic = !music.sadMusic;
            this.main.dbService.getMusicTagDao().updateSadTag(music.id, music.sadMusic);
        }));
        tagMap.put(Constants.RELAXING, new Tag(Constants.RELAXING, musicDto.relaxingMusic, music -> {
            music.relaxingMusic = !music.relaxingMusic;
            this.main.dbService.getMusicTagDao().updateRelaxingTag(music.id, music.relaxingMusic);
        }));
        tagMap.put(Constants.WORK, new Tag(Constants.WORK, musicDto.workMusic, music -> {
            music.workMusic = !music.workMusic;
            this.main.dbService.getMusicTagDao().updateWorkTag(music.id, music.workMusic);
        }));
        tagMap.put(Constants.PARTY, new Tag(Constants.PARTY, musicDto.partyMusic, music -> {
            music.partyMusic = !music.partyMusic;
            this.main.dbService.getMusicTagDao().updatePartyTag(music.id, music.partyMusic);
        }));
        tagMap.put(Constants.RIDE, new Tag(Constants.RIDE, musicDto.rideMusic, music -> {
            music.rideMusic = !music.rideMusic;
            this.main.dbService.getMusicTagDao().updateRideTag(music.id, music.rideMusic);
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
