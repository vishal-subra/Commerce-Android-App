package com.example.beautystuffsss.ui.pharmacist.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.beautystuffsss.ui.universalActivities.AddUpdateProduct;
import com.example.beautystuffsss.ui.universalActivities.MyOrders;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class PharmacistProducts extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    RecyclerView recyclerView;
    SearchView searchView;
    ProductsAdapter adapter;
    ArrayList<Product> products;
    ArrayList<Product> tempProducts;
    FloatingActionButton addProductFab;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pharmacist_products, container, false);

        recyclerView = view.findViewById(R.id.pharmacist_products_recyclerView);
        searchView = view.findViewById(R.id.pharmacist_products_searchView);
        addProductFab = view.findViewById(R.id.pharmacist_products_add_product);
        progressDialog = new ProgressDialog(requireActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.products);
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.images).child(Constants.productImages);
        addProductFab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddUpdateProduct.class);
            intent.putExtra(Constants.intentAction, Constants.intentActionAdd);
            startActivity(intent);
        });
        products = new ArrayList<>();
        tempProducts = new ArrayList<>();
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
                return  false;
            }
        });
        adapter.setOnClickListener(position -> {
            Intent intent = new Intent(getActivity(), AddUpdateProduct.class);
            intent.putExtra(Constants.intentAction, Constants.intentActionEdit);
            intent.putExtra(Constants.productId, products.get(position).getProductId());
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.shopping_menu_pharmacist, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show("Loading products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
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
                }else{
                    progressDialog.dismiss();
                    products.clear();
                    tempProducts.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(requireActivity(), "No product found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_view_orders) {
            startActivity(new Intent(getActivity(), MyOrders.class));
            return true;
        }
        return false;
    }
}