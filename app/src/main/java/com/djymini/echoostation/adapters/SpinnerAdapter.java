package com.djymini.echoostation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.djymini.echoostation.R;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] sortCategories;

    public SpinnerAdapter(Context context, String[] sortCategories) {
        super(context, R.layout.spinner_item, sortCategories);
        this.context = context;
        this.sortCategories = sortCategories;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_dropdown_item, parent, false);
        TextView textView = row.findViewById(R.id.text_spinner);
        textView.setText(sortCategories[position]);
        return row;
    }
}
