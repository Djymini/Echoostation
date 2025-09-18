package com.djymini.echoostation.utilities;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class UiUtilities {
    public static void displayImageWithGlide(int image, int placeholder, ImageView view, Context context){
        Glide.with(context)
                .load(image)
                .placeholder(placeholder)
                .error(placeholder)
                .into(view);
    }

    public static void displayImageWithGlide(Uri image, int placeholder, ImageView view, Context context){
        Glide.with(context)
                .load(image)
                .placeholder(placeholder)
                .error(placeholder)
                .into(view);
    }
}
