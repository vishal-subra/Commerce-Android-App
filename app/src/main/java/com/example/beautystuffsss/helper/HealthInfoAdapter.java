package com.example.beautystuffsss.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.model.HealthInfo;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ImageHelper;
import com.example.beautystuffsss.util.Preferences;

import java.util.ArrayList;

public class HealthInfoAdapter extends RecyclerView.Adapter<HealthInfoAdapter.HealthInfoViewHolder> {
    Context context;
    ArrayList<HealthInfo> healthInfos;
    OnPlayOnYoutubeTapped tappedListener;
    OnHealthInfoTapped healthInfoTapped;
    Preferences preferences;
    ImageHelper imageHelper;

    public HealthInfoAdapter(Context context, ArrayList<HealthInfo> healthInfos) {
        this.context = context;
        this.healthInfos = healthInfos;
        preferences = new Preferences(context);
        imageHelper = new ImageHelper(context);
    }

    public interface OnPlayOnYoutubeTapped {
        void onTapped(int position);
    }

    public interface OnHealthInfoTapped {
        void onViewTapped(int position);
    }

    public void setOnYoutubeTappedListener(OnPlayOnYoutubeTapped tappedListener) {
        this.tappedListener = tappedListener;
    }

    public void setOnHealthInfoTappedListener(OnHealthInfoTapped tappedListener) {
        this.healthInfoTapped = tappedListener;
    }

    @NonNull
    @Override
    public HealthInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.health_info_list_layout, parent, false);
        return new HealthInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthInfoViewHolder holder, int position) {
        if(preferences.getString(Constants.currentUserType).equals(Constants.uTypeCustomer)){
            holder.editInfoButton.setVisibility(View.GONE);
        }else{
            holder.editInfoButton.setOnClickListener(v -> healthInfoTapped.onViewTapped(position));
        }
        imageHelper.loadImage(healthInfos.get(position).getPhotoUrl(),holder.photoPager);
        holder.infoTitle.setText(healthInfos.get(position).getInfoTitle());
        holder.infoDesc.setText(healthInfos.get(position).getInfoDesc());
        holder.infoYoutubeLink.setText(healthInfos.get(position).getInfoYoutubeLink());
        holder.playOnYoutube.setOnClickListener(v -> tappedListener.onTapped(position));
    }

    @Override
    public int getItemCount() {
        return healthInfos.size();
    }

    public static class HealthInfoViewHolder extends RecyclerView.ViewHolder {
        TextView infoTitle, infoDesc, infoYoutubeLink;
        Button playOnYoutube, editInfoButton;
        ImageView photoPager;

        public HealthInfoViewHolder(View itemView) {
            super(itemView);
            infoTitle = itemView.findViewById(R.id.health_info_item_title);
            infoDesc = itemView.findViewById(R.id.health_info_item_desc);
            infoYoutubeLink = itemView.findViewById(R.id.health_info_item_youtube_link);
            playOnYoutube = itemView.findViewById(R.id.health_info_item_youtube_link_play);
            editInfoButton = itemView.findViewById(R.id.health_info_item_editInfo);
            photoPager = itemView.findViewById(R.id.health_info_item_viewPager);
        }
    }
}
