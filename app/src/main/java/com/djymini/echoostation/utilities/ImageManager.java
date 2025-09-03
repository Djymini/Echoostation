package com.djymini.echoostation.utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.R;

public class ImageManager {
    public static void DisplayImage(Uri imageArt, Context context, Drawable placeholder, Drawable error, ImageView imageView){
        Glide.with(context)
                .load(imageArt)
                .placeholder(placeholder)
                .error(error)
                .into(imageView);
    }
}
