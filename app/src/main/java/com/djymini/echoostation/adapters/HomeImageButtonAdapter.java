package com.djymini.echoostation.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.djymini.echoostation.MainActivity;
import com.djymini.echoostation.R;
import com.djymini.echoostation.fragments.playlistMusicFragment.DefaultPlaylistFragment;
import com.djymini.echoostation.fragments.playlistMusicFragment.MixPlaylistFragment;
import com.djymini.echoostation.ui.HomeImageButton;

import java.util.List;

public class HomeImageButtonAdapter extends RecyclerView.Adapter<HomeImageButtonAdapter.HomeImageButtonViewHolder> {
    private List<HomeImageButton> homeImageButtonList;
    private MainActivity main;

    public HomeImageButtonAdapter(List<HomeImageButton> homeImageButtonList, MainActivity main) {
        this.homeImageButtonList = homeImageButtonList;
        this.main = main;
    }

    @NonNull
    @Override
    public HomeImageButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_button, parent, false);
        return new HomeImageButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeImageButtonAdapter.HomeImageButtonViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(homeImageButtonList.get(position).getImageButton())
                .into(holder.imageButton);

        ColorStateList tint = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), homeImageButtonList.get(position).getColor()));
        holder.imageButton.setImageTintList(tint);

        ColorStateList tintBackground = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), homeImageButtonList.get(position).getBackgroundColor()));
        holder.backgroundButton.setBackgroundTintList(tintBackground);

        holder.nameButton.setText(homeImageButtonList.get(position).getNameButton());

        holder.imageButton.setOnClickListener(v -> {
            FragmentTransaction transaction = main.navigator.getFragmentManager().beginTransaction();
            Fragment fragment;

            switch (homeImageButtonList.get(position).getNameButton()){
                case "Les plus écoutés" :
                case "Favoris" :
                case "Récemment écoutés" :
                    fragment = DefaultPlaylistFragment.newInstance(homeImageButtonList.get(position).getNameButton());
                    break;
                default:
                    fragment = MixPlaylistFragment.newInstance(homeImageButtonList.get(position).getNameButton());
                    break;
            }

            if (!fragment.isAdded()) {
                transaction.add(R.id.frame_layout, fragment);
            } else {
                transaction.show(fragment);
            }

            transaction.hide(main.navigator.getActiveFragment()).commit();

            main.navigator.modifyTitle(homeImageButtonList.get(position).getNameButton());
            main.navigator.setActiveFragment(fragment);
            main.navigator.updateToolbarMenu(fragment);
        });
    }

    @Override
    public int getItemCount() {
        return homeImageButtonList.size();
    }

    static class HomeImageButtonViewHolder extends RecyclerView.ViewHolder {
        CardView backgroundButton;
        ImageButton imageButton;
        TextView nameButton;
        HomeImageButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            backgroundButton = itemView.findViewById(R.id.backbground_button);
            imageButton = itemView.findViewById(R.id.image_button);
            nameButton = itemView.findViewById(R.id.name_button);
        }
    }


}
