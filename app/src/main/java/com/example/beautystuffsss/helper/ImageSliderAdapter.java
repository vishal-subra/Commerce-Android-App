package com.example.beautystuffsss.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.util.ImageHelper;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {
    Context context;
    ArrayList<Image> images;
    ViewPager2 pager;
    ImageHelper imageHelper;

    public ImageSliderAdapter(Context context, ArrayList<Image> images, ViewPager2 pager) {
        this.context = context;
        this.images = images;
        this.pager = pager;
        imageHelper = new ImageHelper(context);
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view_layout, parent, false);
        return new ImageSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        imageHelper.loadImage(images.get(position).getImageUri(),holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageSliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_layout_image);
        }
    }
}