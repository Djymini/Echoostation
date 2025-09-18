package com.djymini.echoostation.ui;

public class HomeImageButton {
    private int imageButton;
    private int color;
    private int backgroundColor;
    private String nameButton;

    public HomeImageButton(String nameButton, int color, int backgroundColor, int imageButton) {
        this.nameButton = nameButton;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.imageButton = imageButton;
    }

    public int getImageButton() {
        return imageButton;
    }

    public int getColor() {
        return color;
    }

    public int getBackgroundColor() { return backgroundColor; }

    public String getNameButton() {
        return nameButton;
    }
}
