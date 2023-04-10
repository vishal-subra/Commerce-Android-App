package com.example.beautystuffsss.ui.universalActivities;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.OrderAdapter;
import com.example.beautystuffsss.model.Order;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MyOrders extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    ArrayList<Order> orders;
    DatabaseReference ordersReference;
    ProgressDialog progressDialog;
    Preferences preferences;
    private static final String TAG = "MyOrders";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        toolbar = findViewById(R.id.my_orders_toolbar);
        recyclerView = findViewById(R.id.my_orders_recyclerView);
        setSupportActionBar(toolbar);
        ordersReference = FirebaseDatabase.getInstance().getReference().child(Constants.orders);
        orders = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        preferences = new Preferences(this);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        progressDialog.show("Loading your orders");
        ordersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot order : snapshot.getChildren()) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onDataChange: " + order.getKey());
                        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypeCustomer)) {
                            // load customer's order
                            if (Objects.equals(Objects.requireNonNull(order.child(Constants.orderCustomerId).getValue()).toString(), preferences.getString(Constants.currentUserId))) {
                                orders.add(new Order(
                                        order.getKey(),
                                        Objects.requireNonNull(order.child(Constants.orderCustomerName).getValue()).toString(),
                                        Objects.requireNonNull(order.child(Constants.orderCustomerId).getValue()).toString(),
                                        Objects.requireNonNull(order.child(Constants.orderProductName).getValue()).toString(),
                                        Objects.requireNonNull(order.child(Constants.orderProductPhotoUrl).getValue()).toString(),
                                        Objects.requireNonNull(order.child(Constants.orderProductPrice).getValue()).toString(),
                                        Objects.requireNonNull(order.child(Constants.orderQuantity).getValue()).toString(),
                                        Objects.requireNonNull(order.child(Constants.orderPlacedOn).getValue()).toString(),
                                        Objects.requireNonNull(order.child(Constants.orderStatus).getValue()).toString()
                                ));
                                progressDialog.dismiss();
                                orderAdapter.notifyDataSetChanged();
                            }
                        } else if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
                            // load pharmacist's orders
                            Log.d(TAG, "onDataChange: ");
                            orders.add(new Order(
                                    order.getKey(),
                                    Objects.requireNonNull(order.child(Constants.orderCustomerName).getValue()).toString(),
                                    Objects.requireNonNull(order.child(Constants.orderCustomerId).getValue()).toString(),
                                    Objects.requireNonNull(order.child(Constants.orderProductName).getValue()).toString(),
                                    Objects.requireNonNull(order.child(Constants.orderProductPhotoUrl).getValue()).toString(),
                                    Objects.requireNonNull(order.child(Constants.orderProductPrice).getValue()).toString(),
                                    Objects.requireNonNull(order.child(Constants.orderQuantity).getValue()).toString(),
                                    Objects.requireNonNull(order.child(Constants.orderPlacedOn).getValue()).toString(),
                                    Objects.requireNonNull(order.child(Constants.orderStatus).getValue()).toString()
                            ));
                            progressDialog.dismiss();
                            orderAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MyOrders.this, "No Order found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orders);
        recyclerView.setAdapter(orderAdapter);
        orderAdapter.setOnChatButtonTappedListener(position -> {
            Intent intent = new Intent(MyOrders.this, ChatView.class);
            intent.putExtra(Constants.currentUserName, orders.get(position).getOrderFrom());
            intent.putExtra(Constants.currentUserId, orders.get(position).getCustomerId());
            startActivity(intent);
        });
        orderAdapter.setOnOrderTappedListener(position -> {
            Intent orderViewIntent = new Intent(this, OrderView.class);
            orderViewIntent.putExtra(Constants.orderId, orders.get(position).getOrderId());
            startActivity(orderViewIntent);
        });
        orderAdapter.setOnOrderStatusTappedListener(position -> {
            final AlertDialog.Builder adb = new AlertDialog.Builder(MyOrders.this);
            adb.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            adb.setPositiveButton("Set Status", (dialog, which) -> dialog.dismiss());
            adb.setTitle("Order status");
            CharSequence[] items = new CharSequence[]{Constants.orderConfirmed, Constants.orderCompleted};
            final int[] selectedIndex = new int[1];
            if (orders.get(position).getOrderStatus().equals(Constants.orderPending)) {
                selectedIndex[0] = -1;
            } else if (orders.get(position).getOrderStatus().equals(Constants.orderConfirmed)) {
                selectedIndex[0] = 0;
            } else {
                selectedIndex[0] = 1;
            }
            adb.setSingleChoiceItems(items, -1, (dialog, which) -> {
                if (which == 0) {
                    ordersReference.child(orders.get(position).getOrderId()).child(Constants.orderStatus).setValue(Constants.orderConfirmed);
                } else {
                    ordersReference.child(orders.get(position).getOrderId()).child(Constants.orderStatus).setValue(Constants.orderCompleted);
                }
                dialog.dismiss();
            });
            adb.create().show();
        });
        orderAdapter.setOnAddReminderTappedListener(position -> {
            long startMillis = System.currentTimeMillis();
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, startMillis);
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
            startActivity(intent);
        });
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

    private void toolbarSetter(ActionBar toolbar) {
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);
        toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp);
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
            toolbar.setTitle(getString(R.string.orders));
        } else {
            toolbar.setTitle(getString(R.string.my_orders));
        }
    }
}