package com.example.beautystuffsss.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.model.CartItem;
import com.example.beautystuffsss.util.ImageHelper;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    ArrayList<CartItem> cartItems;
    Context context;
    ImageHelper imageHelper;
    RemoveFromCart removeFromCart;

    public CartItemAdapter(ArrayList<CartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
        imageHelper = new ImageHelper(this.context);
    }

    public interface RemoveFromCart {
        void onTapped(int position);
    }

    public void setOnRemoveFromCartTappedListener(RemoveFromCart removeFromCartTappedListener) {
        removeFromCart = removeFromCartTappedListener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        holder.productTitle.setText(cartItems.get(position).getQuantity() + " x "+cartItems.get(position).getProductName());
        holder.productPrice.setText(cartItems.get(position).getProductPrice());
        imageHelper.loadImage(cartItems.get(position).getPhotoUrl(), holder.productPhoto);
        holder.removeFromCart.setOnClickListener(v -> {
            removeFromCart.onTapped(position);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productPhoto;
        TextView productTitle, productPrice;
        ImageButton removeFromCart;

        public CartItemViewHolder(View itemView) {
            super(itemView);
            productPhoto = itemView.findViewById(R.id.cart_item_imageView);
            productTitle = itemView.findViewById(R.id.cart_item_product_title);
            productPrice = itemView.findViewById(R.id.cart_item_product_price);
            removeFromCart = itemView.findViewById(R.id.cart_item_removeFromCart);
        }
    }
}
