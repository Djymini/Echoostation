package com.djymini.echoostation.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.djymini.echoostation.R;

public class ViewHomeData extends ConstraintLayout {
    private TextView dataText, titleText;

    public ViewHomeData(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialisation(context);
    }

    private void initialisation(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_home_data, this, true);
        titleText = findViewById(R.id.title);
        dataText = findViewById(R.id.data);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void setData(String data) {
        dataText.setText(data);
    }
}
