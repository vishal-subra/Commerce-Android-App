package com.example.beautystuffsss.ui.customer.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.CartItemAdapter;
import com.example.beautystuffsss.model.CartItem;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ShoppingCart extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView cartItemRecyclerView;
    TextView totalPriceTextView, deliveryChargesTextView, serviceChargesTextView;
    Button confirmOrderButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myCartReference, ordersReference;
    ProgressDialog progressDialog;
    Preferences preferences;
    CartItemAdapter adapter;
    private static final String TAG = "CustomerShoppingCart";
    ArrayList<CartItem> cartItems;
    ArrayList<String> keys;
    ArrayList<Float> prices, serviceCharges, deliveryCharges;
    private static final int PAYMENT_CODE = 2;
    Float totalPrice, totalServiceCharges, totalDeliveryCharges;
    ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        toolbar = findViewById(R.id.customer_shopping_cart_toolbar);
        cartItemRecyclerView = findViewById(R.id.customer_shopping_cart_recyclerView);
        totalPriceTextView = findViewById(R.id.customer_shopping_cart_totalPrice);
        confirmOrderButton = findViewById(R.id.customer_shopping_cart_confirmOrder);
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        cartItems = new ArrayList<>();
        prices = new ArrayList<>();
        serviceCharges = new ArrayList<>();
        deliveryCharges = new ArrayList<>();
        keys = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        preferences = new Preferences(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myCartReference = firebaseDatabase.getReference().child(Constants.carts).child(preferences.getString(Constants.currentUserId));
        ordersReference = firebaseDatabase.getReference().child(Constants.orders);
        progressDialog.show("Loading your cart Items");
        childEventListener = new ChildEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                keys.add(snapshot.getKey());
                float itemPrice = Float.parseFloat(Objects.requireNonNull(snapshot.child(Constants.cartItemPrice).getValue()).toString().substring(Objects.requireNonNull(snapshot.child(Constants.cartItemPrice).getValue()).toString().lastIndexOf(" ") + 1));
                float itemQuantity = Float.parseFloat(Objects.requireNonNull(snapshot.child(Constants.cartItemQuantity).getValue()).toString());
                prices.add(itemPrice * itemQuantity);
                cartItems.add(new CartItem(
                        Objects.requireNonNull(snapshot.child(Constants.cartItemId).getValue()).toString(),
                        Objects.requireNonNull(snapshot.child(Constants.cartItemPhotoUrl).getValue()).toString(),
                        Objects.requireNonNull(snapshot.child(Constants.cartItemName).getValue()).toString(),
                        Objects.requireNonNull(snapshot.child(Constants.cartItemPrice).getValue()).toString(),
                        Objects.requireNonNull(snapshot.child(Constants.cartItemQuantity).getValue()).toString()
                ));
                totalPrice = (float) 0.0;
                for (Float price : prices) {
                    totalPrice = totalPrice + price;
                }
                totalPriceTextView.setText("RM " + String.format(Locale.getDefault(), "%.2f", totalPrice));
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                int index = keys.indexOf(key);
                if (index != -1) {
                    cartItems.remove(index);
                    keys.remove(index);
                    prices.remove(index);
                    totalPrice = (float) 0.0;
                    for (Float price : prices) {
                        totalPrice = totalPrice + price;
                    }
                    totalPriceTextView.setText("RM " + String.format(Locale.getDefault(), "%.2f", totalPrice));
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "onChildRemoved: Index out of bound");
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myCartReference.addChildEventListener(childEventListener);
        myCartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: " + cartItems);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
        adapter = new CartItemAdapter(cartItems, this);
        adapter.setOnRemoveFromCartTappedListener(position -> myCartReference.child(cartItems.get(position).getItemId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
            }
        }));
        confirmOrderButton.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                Intent paymentIntent = new Intent(this, CustomerPayment.class);
                startActivityForResult(paymentIntent, PAYMENT_CODE);
            } else {
                Toast.makeText(this, "Your cart is empty cannot order", Toast.LENGTH_SHORT).show();
            }
        });
        cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItemRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_CODE && resultCode == Activity.RESULT_OK) {
            progressDialog.show("Placing your orders");
            try {
                for (CartItem item : cartItems) {
                    myCartReference.child(item.getItemId()).removeValue().addOnCompleteListener(task -> {
                        makeOrder(item.getItemId(),
                                preferences.getString(Constants.currentUserName),
                                item.getProductName(),
                                item.getPhotoUrl(),
                                item.getProductPrice(),
                                item.getQuantity(),
                                preferences.getString(Constants.currentUserId)
                        );
                    });
                }
            } catch (Exception e) {
                Log.d(TAG, "onCreateView: " + e.getMessage());
            } finally {
                Toast.makeText(this, "Order(s) placed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    private void toolbarSetter(ActionBar toolbar) {
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);
        toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp);
        toolbar.setTitle(getString(R.string.shopping_cart));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return false;
        }
    }

    private void makeOrder(
            final String orderId,
            final String orderFrom,
            final String productName,
            final String productPhoto,
            final String productPrice,
            final String quantity,
            final String cusId
    ) {
        Map<String, String> order = new HashMap<>();
        order.put(Constants.orderId, orderId);
        order.put(Constants.orderCustomerName, orderFrom);
        order.put(Constants.orderCustomerId, cusId);
        order.put(Constants.orderProductName, productName);
        order.put(Constants.orderProductPhotoUrl, productPhoto);
        order.put(Constants.orderProductPrice, productPrice);
        order.put(Constants.orderQuantity, quantity);
        order.put(Constants.orderPlacedOn, new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date()));
        order.put(Constants.orderStatus, Constants.orderPending);
        ordersReference.child(orderId).setValue(order).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

            } else {
                Toast.makeText(this, "Cannot make order", Toast.LENGTH_SHORT).show();
            }
        });
    }
}