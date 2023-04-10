package com.example.beautystuffsss.ui.customer.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.ProductsAdapter;
import com.example.beautystuffsss.helper.SpacingItemDecoration;
import com.example.beautystuffsss.model.Product;
import com.example.beautystuffsss.ui.customer.activities.ShoppingCart;
import com.example.beautystuffsss.ui.universalActivities.MyOrders;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ImageHelper;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CustomerShopping extends Fragment {
    RecyclerView recyclerView;
    SearchView searchView;
    ProductsAdapter adapter;
    ArrayList<Product> products;
    ArrayList<Product> tempProducts;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    ImageHelper imageHelper;
    Preferences preferences;
    DatabaseReference cartReference;
    Product currentProduct;
    int quantity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_shopping, container, false);
        recyclerView = view.findViewById(R.id.customer_shopping_recyclerView);
        searchView = view.findViewById(R.id.customer_shopping_searchView);
        products = new ArrayList<>();
        tempProducts = new ArrayList<>();
        imageHelper = new ImageHelper(requireActivity());
        progressDialog = new ProgressDialog(requireActivity());
        preferences = new Preferences(requireActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.products);
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.images).child(Constants.productImages);
        progressDialog.show("Loading products");
        cartReference = FirebaseDatabase.getInstance().getReference().child(Constants.carts).child(preferences.getString(Constants.currentUserId));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    products.clear();
                    for (DataSnapshot product : snapshot.getChildren()) {
                        products.add(new Product(
                                product.getKey(),
                                Objects.requireNonNull(product.child(Constants.productName).getValue()).toString(),
                                Objects.requireNonNull(product.child(Constants.productPrice).getValue()).toString(),
                                Objects.requireNonNull(product.child(Constants.promoDescription).getValue()).toString(),
                                Objects.requireNonNull(product.child(Constants.productPhotoUrl).getValue()).toString()
                        ));
                        tempProducts.add(new Product(
                                product.getKey(),
                                Objects.requireNonNull(product.child(Constants.productName).getValue()).toString(),
                                Objects.requireNonNull(product.child(Constants.productPrice).getValue()).toString(),
                                Objects.requireNonNull(product.child(Constants.promoDescription).getValue()).toString(),
                                Objects.requireNonNull(product.child(Constants.productPhotoUrl).getValue()).toString()
                        ));
                        progressDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    products.clear();
                    tempProducts.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        tempProducts.addAll(products);
        adapter = new ProductsAdapter(products, requireActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.setClipToPadding(false);
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, spacingInPixels, false));
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                products.clear();
                if (newText.isEmpty()) {
                    products.addAll(tempProducts);
                } else {
                    for (Product product : tempProducts) {
                        if (product.getProductName().toLowerCase(Locale.getDefault()).contains(newText.toLowerCase())) {
                            products.add(product);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        adapter.setOnClickListener(this::showBuyDialog);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.shopping_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_view_orders) {
            startActivity(new Intent(getActivity(), MyOrders.class));
            return true;
        } else if (item.getItemId() == R.id.action_view_cart) {
            startActivity(new Intent(getActivity(), ShoppingCart.class));
            return true;
        }
        return false;
    }

    private void showBuyDialog(int position) {
        ImageView productImage;
        ImageButton cancelButton, increaseButton, decreaseButton;
        final TextView title, price, quantity,description;
        Button buyButton;
        final int[] q = {1};
        final Dialog dialog = new Dialog(getActivity());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.product_buy_dialog, null, false);
        productImage = view.findViewById(R.id.product_buy_image);
        cancelButton = view.findViewById(R.id.product_buy_cancel);
        buyButton = view.findViewById(R.id.product_buy_button);
        title = view.findViewById(R.id.product_buy_name);
        price = view.findViewById(R.id.product_buy_price);
        quantity = view.findViewById(R.id.product_buy_quantity);
        description = view.findViewById(R.id.product_buy_description);
        increaseButton = view.findViewById(R.id.product_buy_quantity_add);
        decreaseButton = view.findViewById(R.id.product_buy_quantity_rem);
        title.setText(products.get(position).getProductName());
        price.setText(products.get(position).getProductPrice());
        description.setText(products.get(position).getProductDescription());
        imageHelper.loadImage(products.get(position).getPhotoUrl(), productImage);
        increaseButton.setOnClickListener(v -> {
            q[0]++;
            quantity.setText(String.valueOf(q[0]));
        });
        decreaseButton.setOnClickListener(v -> {
            if (q[0] >= 1) {
                q[0]--;
                quantity.setText(String.valueOf(q[0]));
            }
        });
        buyButton.setOnClickListener(v -> {
            dialog.dismiss();
            currentProduct = products.get(position);
            String itemId = cartReference.push().getKey();
            addToCart(itemId, currentProduct.getPhotoUrl(), currentProduct.getProductName(), currentProduct.getProductPrice(), q[0]);
        });
        quantity.setText(String.valueOf(q[0]));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setContentView(view);
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addToCart(
            final String itemId,
            final String productPhotoUrl,
            final String productName,
            final String productPrice,
            final int quantity
    ) {
        progressDialog.show("Adding item to cart");
        Map<String, String> cartItem = new HashMap<>();
        cartItem.put(Constants.cartItemId, itemId);
        cartItem.put(Constants.cartItemPhotoUrl, productPhotoUrl);
        cartItem.put(Constants.cartItemName, productName);
        cartItem.put(Constants.cartItemPrice, productPrice);
        cartItem.put(Constants.cartItemQuantity, String.valueOf(quantity));
        cartReference.child(itemId).setValue(cartItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "Item Added to cart successfully", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "Unable to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}