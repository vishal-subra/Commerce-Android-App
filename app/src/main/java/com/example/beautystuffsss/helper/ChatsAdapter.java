package com.example.beautystuffsss.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautystuffsss.R;
import com.example.beautystuffsss.model.PharmacistChat;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ImageHelper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {
    ArrayList<PharmacistChat> pharmacistChats;
    Context context;
    OnUserTap onUserTap;
    ImageHelper imageHelper;

    public ChatsAdapter(ArrayList<PharmacistChat> pharmacistChats, Context context) {
        this.pharmacistChats = pharmacistChats;
        this.context = context;
        imageHelper = new ImageHelper(this.context);
    }

    public interface OnUserTap {
        void onTap(int position);
    }


    public void setOnUserTappedListener(OnUserTap userTapListener) {
        onUserTap = userTapListener;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_layout, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        if (pharmacistChats.get(position).getPhotoUrl().equals(Constants.noPhotoUrl)) {
            Glide.with(context).load(R.drawable.user1).into(holder.avatar);
        } else {
            imageHelper.loadImage(pharmacistChats.get(position).getPhotoUrl(),holder.avatar);
        }
        holder.name.setText(pharmacistChats.get(position).getName());
        holder.itemView.setOnClickListener(v -> onUserTap.onTap(position));
        holder.dateAndTime.setText(pharmacistChats.get(position).getLatestMessage());
    }

    @Override
    public int getItemCount() {
        return pharmacistChats.size();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView name, dateAndTime;

        public ChatsViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.chats_user_avatar);
            name = view.findViewById(R.id.chats_user_name);
            dateAndTime = view.findViewById(R.id.chat_latest_message);
        }
    }
}
