package com.djymini.echoostation.ui;

import com.djymini.echoostation.dtos.MusicDto;

import java.util.function.Consumer;

public class Tag {
    public String name;
    public boolean isActive;
    public Consumer<MusicDto> action;

    public Tag(String name, boolean isActive, Consumer<MusicDto> action) {
        this.name = name;
        this.isActive = isActive;
        this.action = action;
    }

    public void changeTagValue(MusicDto musicDto){
        action.accept(musicDto);
        isActive = !isActive;
    }
}
