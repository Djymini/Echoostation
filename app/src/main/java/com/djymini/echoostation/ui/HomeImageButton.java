package com.djymini.echoostation.ui;

public class HomeImageButton {
    private int imageButton;
    private int color;
    private String nameButton;

    public HomeImageButton(String nameButton, int color, int imageButton) {
        this.nameButton = nameButton;
        this.color = color;
        this.imageButton = imageButton;
    }

    public int getImageButton() {
        return imageButton;
    }

    public int getColor() {
        return color;
    }

    public String getNameButton() {
        return nameButton;
    }
}
