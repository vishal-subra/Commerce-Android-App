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
import com.example.beautystuffsss.model.Product;
import com.example.beautystuffsss.util.ImageHelper;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.CustomerProductsViewHolder> {
    ArrayList<Product> products;
    ArrayList<Product> productsTempList;
    Context context;
    OnClickListener listener;
    ImageHelper imageHelper;

    public interface OnClickListener {
        void onClick(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public ProductsAdapter(ArrayList<Product> productList, Context context) {
        this.products = productList;
        this.context = context;
        productsTempList = new ArrayList<>();
        productsTempList.addAll(products);
        imageHelper = new ImageHelper(context);
    }

    @NonNull
    @Override
    public CustomerProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_model, parent, false);
        return new CustomerProductsViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerProductsViewHolder holder, final int position) {
        imageHelper.loadImage(products.get(position).getPhotoUrl(),holder.productImage);
        holder.productName.setText(products.get(position).getProductName());
        holder.productPrice.setText(products.get(position).getProductPrice());
        holder.itemView.setOnClickListener(v -> listener.onClick(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class CustomerProductsViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productPrice;
        ImageView productImage;

        public CustomerProductsViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
