package com.example.beautystuffsss.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.model.Promotion;
import com.example.beautystuffsss.util.ImageHelper;

import java.util.ArrayList;

public class PromotionListAdapter extends RecyclerView.Adapter<PromotionListAdapter.PromotionViewHolder> {
    Context context;
    ArrayList<Promotion> promotions;
    OnReadMoreTapped onReadMoreTapped;
    ImageHelper imageHelper;

    public PromotionListAdapter(Context context, ArrayList<Promotion> promotions) {
        this.context = context;
        this.promotions = promotions;
        imageHelper = new ImageHelper(context);
    }

    public interface OnReadMoreTapped {
        void onTapped(int pos);
    }

    public void setOnReadMoreTapped(OnReadMoreTapped listener) {
        onReadMoreTapped = listener;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_list_layout, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        imageHelper.loadImage(promotions.get(position).getPhotoUrl(),holder.promoThumbnail);
        holder.promoTitle.setText(promotions.get(position).getPromoTitle());
        holder.promoDesc.setText(promotions.get(position).getPromoDesc());
        holder.promoDatePosted.setText(promotions.get(position).getDatePosted());
        holder.promoReadMore.setOnClickListener(v -> onReadMoreTapped.onTapped(position));
        if (position == promotions.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        ImageView promoThumbnail;
        TextView promoTitle, promoDesc, promoDatePosted, promoReadMore;
        View divider;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            promoThumbnail = itemView.findViewById(R.id.promo_list_photo);
            promoTitle = itemView.findViewById(R.id.promo_list_title);
            promoDesc = itemView.findViewById(R.id.promo_list_description);
            promoDatePosted = itemView.findViewById(R.id.promo_list_datePosted);
            promoReadMore = itemView.findViewById(R.id.promo_list_readMore);
            divider = itemView.findViewById(R.id.promo_list_divider);
        }
    }
}
