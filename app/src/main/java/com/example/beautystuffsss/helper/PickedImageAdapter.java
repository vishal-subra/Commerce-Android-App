package com.example.beautystuffsss.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautystuffsss.R;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ImageHelper;

import java.util.ArrayList;

public class PickedImageAdapter extends RecyclerView.Adapter<PickedImageAdapter.PickedImageViewHolder> {
    Context context;
    ArrayList<Image> images;
    SetOnRemoveTapped onRemoveTapped;
    ImageHelper imageHelper;

    public PickedImageAdapter(Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
        imageHelper = new ImageHelper(context);
    }

    public interface SetOnRemoveTapped {
        void onTap(int pos);
    }

    public void setOnRemoveTapped(SetOnRemoveTapped listener) {
        onRemoveTapped = listener;
    }

    @NonNull
    @Override
    public PickedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_pick_layout, parent, false);
        return new PickedImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickedImageViewHolder holder, int position) {
        if (images.get(position).imageUri.contains(Constants.promos)){
            imageHelper.loadImage(images.get(position).getImageUri(),holder.imagePreview);
        }else {
            Glide.with(context).load(images.get(position).getImageUri()).into(holder.imagePreview);
        }
        holder.removeButton.setOnClickListener(v -> {
            onRemoveTapped.onTap(position);
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class PickedImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePreview;
        ImageButton removeButton;

        public PickedImageViewHolder(View itemView) {
            super(itemView);
            imagePreview = itemView.findViewById(R.id.image_pick_preview);
            removeButton = itemView.findViewById(R.id.image_pick_remove);
        }
    }
}
