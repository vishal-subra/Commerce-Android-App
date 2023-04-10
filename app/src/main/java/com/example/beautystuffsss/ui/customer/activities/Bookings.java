package com.example.beautystuffsss.ui.customer.activities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.BookingsAdapter;
import com.example.beautystuffsss.model.Booking;
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

public class Bookings extends AppCompatActivity {
    ArrayList<Booking> bookings;
    RecyclerView recyclerView;
    Toolbar toolbar;
    BookingsAdapter adapter;
    DatabaseReference bookingReference;
    ProgressDialog progressDialog;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);
        toolbar = findViewById(R.id.customer_bookings_toolbar);
        recyclerView = findViewById(R.id.customer_bookings_recyclerView);
        bookings = new ArrayList<>();
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        bookingReference = FirebaseDatabase.getInstance().getReference().child(Constants.bookings);
        progressDialog = new ProgressDialog(this);
        preferences = new Preferences(this);
        progressDialog.show("loading bookings");
        bookingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookings.clear();
                progressDialog.dismiss();
                for (DataSnapshot booking : snapshot.getChildren()) {

                    if (!snapshot.exists()) {
                        Toast.makeText(Bookings.this, "No booking found", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else if (preferences.getString(Constants.currentUserType).equals(Constants.uTypeCustomer)) {
                        if (Objects.requireNonNull(booking.child(Constants.bookingCustomerId).getValue()).toString().equals(preferences.getString(Constants.currentUserId))) {
                            bookings.add(new Booking(
                                    booking.getKey(),
                                    Objects.requireNonNull(booking.child(Constants.bookingFor).getValue()).toString(),
                                    Objects.requireNonNull(booking.child(Constants.bookingDateAndTime).getValue()).toString(),
                                    Objects.requireNonNull(booking.child(Constants.bookingStatus).getValue()).toString()
                            ));
                            progressDialog.dismiss();
                            adapter.notifyDataSetChanged();
                        }
                    } else if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)){
                        bookings.add(new Booking(
                                booking.getKey(),
                                Objects.requireNonNull(booking.child(Constants.bookingFor).getValue()).toString(),
                                Objects.requireNonNull(booking.child(Constants.bookingDateAndTime).getValue()).toString(),
                                Objects.requireNonNull(booking.child(Constants.bookingStatus).getValue()).toString()
                        ));
                        progressDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingsAdapter(bookings, this);
        recyclerView.setAdapter(adapter);
        adapter.setOnVisitedTappedListener(pos -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Bookings.this);
            dialogBuilder.setTitle("Sure ?");
            dialogBuilder.setMessage("This action will remove the booking forever");
            dialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                bookingReference.child(bookings.get(pos).getBookingId()).removeValue();
            });
            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        });
        adapter.setOnStatusTappedListener(pos -> {
            final androidx.appcompat.app.AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(Bookings.this);
            adb.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            adb.setPositiveButton("Set Status", (dialog, which) -> dialog.dismiss());
            adb.setTitle("Order status");
            CharSequence[] items = new CharSequence[]{Constants.bookingStatusAccepted, Constants.bookingStatusRejected};
            final int[] selectedIndex = new int[1];
            if (bookings.get(pos).getBookingStatus().equals(Constants.bookingStatusAccepted)) {
                selectedIndex[0] = 0;
            } else if (bookings.get(pos).getBookingStatus().equals(Constants.bookingStatusRejected)) {
                selectedIndex[0] = 1;
            } else {
                selectedIndex[0] = -1;
            }
            adb.setSingleChoiceItems(items, selectedIndex[0], (dialog, which) -> {
                if (which == 0) {
                    bookingReference.child(bookings.get(pos).getBookingId()).child(Constants.bookingStatus).setValue(Constants.bookingStatusAccepted);
                } else {
                    bookingReference.child(bookings.get(pos).getBookingId()).child(Constants.bookingStatus).setValue(Constants.bookingStatusRejected);
                }
                dialog.dismiss();
            });
            adb.create().show();
        });
        adapter.setOnLinkTappedListener(pos -> {
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
        toolbar.setTitle(getString(R.string.view_bookings));
    }
}