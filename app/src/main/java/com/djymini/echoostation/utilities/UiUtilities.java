package com.djymini.echoostation.utilities;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class UiUtilities {
    public static void displayImageWithGlide(int image, int placeholder, ImageView view, Context context){
        Glide.with(context)
                .load(image)
                .override(500, 500)
                .placeholder(placeholder)
                .error(placeholder)
                .into(view);
    }

    public static void displayImageWithGlide(Uri image, int placeholder, ImageView view, Context context){
        Glide.with(context)
                .load(image)
                .override(500, 500)
                .placeholder(placeholder)
                .error(placeholder)
                .into(view);
    }

    public static void displayImageWithGlide(File image, int placeholder, ImageView view, Context context){
        Glide.with(context)
                .load(image)
                .override(500, 500)
                .placeholder(placeholder)
                .error(placeholder)
                .into(view);
    }

    public static String displayCounterText(boolean moreThanOne, int value, String textWithS, String text){
        return moreThanOne ? value + textWithS : value + text;
    }
}
