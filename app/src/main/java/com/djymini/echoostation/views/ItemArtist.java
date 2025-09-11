package com.djymini.echoostation.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.R;

import java.io.File;

public class ItemArtist extends ConstraintLayout {
    private ImageView artistImage;
    private TextView artistName;

    public ItemArtist(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialisation(context);
    }

    private void initialisation(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_artist, this, true);
        artistImage = findViewById(R.id.artist_image);
        artistName = findViewById(R.id.artist_name);
    }

    public void setImage(Context context, File file){
        Glide.with(context)
                .load(file != null ? file : R.drawable.echoostation_placeholder_music_3x)
                .placeholder(R.drawable.echoostation_placeholder_music_3x)
                .error(R.drawable.echoostation_placeholder_music_3x)
                .into(artistImage);
    }

    public void setName(String name) {
        artistName.setText(name);
    }
}
