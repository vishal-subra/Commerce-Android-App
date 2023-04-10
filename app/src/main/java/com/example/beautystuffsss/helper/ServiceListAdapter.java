package com.example.beautystuffsss.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautystuffsss.R;
import com.example.beautystuffsss.model.Service;

import java.util.ArrayList;
import java.util.Random;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ServiceListViewHolder> {
    ArrayList<Service> services;
    Context context;
    OnServiceTapped onServiceTapped;

    public ServiceListAdapter(ArrayList<Service> services, Context context) {
        this.services = services;
        this.context = context;
    }

    public interface OnServiceTapped {
        void onTap(int pos);
    }

    public void setOnServiceTappedListener(OnServiceTapped serviceTapped) {
        this.onServiceTapped = serviceTapped;
    }

    @NonNull
    @Override
    public ServiceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_list_layout, parent, false);

        return new ServiceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceListViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> onServiceTapped.onTap(position));
        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.service_1);
        images.add(R.drawable.service_2);
        images.add(R.drawable.service_3);
        images.add(R.drawable.service_4);
        Glide.with(context).load(this.getRandomElement(images)).into(holder.serviceThumbnail);
        holder.serviceTitle.setText(services.get(position).getTitle());
        holder.serviceDescription.setText(services.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ServiceListViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceThumbnail;
        TextView serviceTitle, serviceDescription;


        public ServiceListViewHolder(View itemView) {
            super(itemView);
            serviceThumbnail = itemView.findViewById(R.id.service_list_image);
            serviceTitle = itemView.findViewById(R.id.service_list_title);
            serviceDescription = itemView.findViewById(R.id.service_list_desc);
        }
    }

    private int getRandomElement(ArrayList<Integer> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }
}
