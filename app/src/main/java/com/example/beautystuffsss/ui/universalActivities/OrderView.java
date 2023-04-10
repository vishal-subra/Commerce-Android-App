package com.example.beautystuffsss.ui.universalActivities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ImageHelper;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class OrderView extends AppCompatActivity {
    private static final String TAG = "OrderView";
    String orderId;
    DatabaseReference orderReference;
    ProgressDialog progressDialog;
    ImageHelper imageHelper;
    ImageView productImage;
    TextView nameAndQuantity, cusName, orderDate, orderStatus, orderPrice, orderTotalPrice;
    FloatingActionButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        initViews();
        progressDialog = new ProgressDialog(this);
        imageHelper = new ImageHelper(this);
        orderId = getIntent().getStringExtra(Constants.orderId);
        orderReference = FirebaseDatabase.getInstance().getReference().child(Constants.orders);
        progressDialog.show("Loading Order details");
        orderReference.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageHelper.loadImage(snapshot.child(Constants.orderProductPhotoUrl).getValue().toString(), productImage);
                nameAndQuantity.setText(snapshot.child(Constants.orderQuantity).getValue().toString() + " X " + snapshot.child(Constants.orderProductName).getValue().toString());
                cusName.setText(snapshot.child(Constants.orderCustomerName).getValue().toString());
                orderDate.setText(snapshot.child(Constants.orderPlacedOn).getValue().toString());
                orderStatus.setText(snapshot.child(Constants.orderStatus).getValue().toString());
                orderPrice.setText(snapshot.child(Constants.orderProductPrice).getValue().toString());
                double price = Double.parseDouble(snapshot.child(Constants.orderProductPrice).getValue().toString().substring(snapshot.child(Constants.orderProductPrice).getValue().toString().lastIndexOf(" ") + 1));
                int quantity = Integer.parseInt(snapshot.child(Constants.orderQuantity).getValue().toString());
                orderTotalPrice.setText(String.format(Locale.getDefault(),"%.2f", price * quantity));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        productImage = findViewById(R.id.imageView_orderView_photo);
        nameAndQuantity = findViewById(R.id.textView_orderView_titleQuantity);
        cusName = findViewById(R.id.textView_orderView_customerName);
        orderDate = findViewById(R.id.textView_orderView_orderDate);
        orderStatus = findViewById(R.id.textView_orderView_orderStatus);
        orderPrice = findViewById(R.id.textView_orderView_orderPrice);
        orderTotalPrice = findViewById(R.id.textView_orderView_orderTotalPrice);
        backButton = findViewById(R.id.fab_orderView_back);
    }
}