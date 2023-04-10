package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.ServiceListAdapter;
import com.example.beautystuffsss.model.Service;
import com.example.beautystuffsss.ui.customer.activities.Bookings;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Services extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<Service> services;
    ServiceListAdapter adapter;
    FloatingActionButton addServiceFab;
    Preferences preferences;
    ProgressDialog progressDialog;
    DatabaseReference servicesReference, bookingReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        toolbar = findViewById(R.id.services_toolbar);
        recyclerView = findViewById(R.id.services_recyclerView);
        addServiceFab = findViewById(R.id.services_addService_fab);
        preferences = new Preferences(this);
        progressDialog = new ProgressDialog(this);
        servicesReference = FirebaseDatabase.getInstance().getReference().child(Constants.services);
        bookingReference = FirebaseDatabase.getInstance().getReference().child(Constants.bookings);
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        services = new ArrayList<>();

        adapter = new ServiceListAdapter(services, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
            addServiceFab.show();
            addServiceFab.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddService.class);
                intent.putExtra(Constants.intentAction, Constants.intentActionAdd);
                startActivity(intent);
            });
        }
        adapter.setOnServiceTappedListener(pos -> {
            if (preferences.getString(Constants.currentUserType).equals(Constants.uTypeCustomer)) {
                Calendar now = Calendar.getInstance();
                final int[] currentDate = {0};
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        (view, year, monthOfYear, dayOfMonth) -> {
                            currentDate[0] = dayOfMonth;
                            TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                                    (view1, hourOfDay, minute, second) -> {
                                        Calendar selectedTime = Calendar.getInstance();
                                        selectedTime.set(year, monthOfYear, dayOfMonth, hourOfDay, minute, second);
                                        String date = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(selectedTime.getTime());
                                        progressDialog.show("Adding your booking");
                                        Map<String, Object> booking = new HashMap<>();
                                        String bookingId = bookingReference.push().getKey();
                                        booking.put(Constants.bookingId, bookingId);
                                        booking.put(Constants.bookingFor, services.get(pos).getTitle());
                                        booking.put(Constants.bookingDateAndTime, date);
                                        booking.put(Constants.bookingStatus, Constants.bookingStatusPending);
                                        booking.put(Constants.bookingCustomerId, preferences.getString(Constants.currentUserId));
                                        assert bookingId != null;
                                        bookingReference.child(bookingId).setValue(booking).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toast.makeText(this, "Booking Added", Toast.LENGTH_SHORT).show();
                                            }else {
                                                progressDialog.dismiss();
                                                Toast.makeText(this, "Unable to add booking", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    },
                                    now.get(Calendar.HOUR_OF_DAY),
                                    now.get(Calendar.MINUTE),
                                    now.get(Calendar.SECOND),
                                    false
                            );
                            timePickerDialog.show(getSupportFragmentManager(), "TimePickerDialog");
                            if (now.get(Calendar.HOUR_OF_DAY) > 9 && currentDate[0] == now.get(Calendar.DAY_OF_MONTH)) {
                                timePickerDialog.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
                            } else {
                                timePickerDialog.setMinTime(9, 0, 0);
                            }
                            timePickerDialog.setMaxTime(18, 0, 0);
                            timePickerDialog.setCancelable(false);
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setCancelable(true);
                dpd.setMinDate(now);
                dpd.show(getSupportFragmentManager(), "DatePickerDialog");
            } else {
                Intent intent = new Intent(this, AddService.class);
                intent.putExtra(Constants.intentAction, Constants.intentActionEdit);
                intent.putExtra(Constants.serviceId, services.get(pos).getServiceId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        new MenuInflater(this).inflate(R.menu.booking_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.show("Loading services");
        servicesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    services.clear();
                    for (DataSnapshot service : snapshot.getChildren()) {
                        services.add(new Service(
                                service.getKey(),
                                Objects.requireNonNull(service.child(Constants.serviceTitle).getValue()).toString(),
                                Objects.requireNonNull(service.child(Constants.serviceDesc).getValue()).toString()
                        ));

                        progressDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Services.this, "No service found", Toast.LENGTH_SHORT).show();
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_view_booking) {
            startActivity(new Intent(Services.this, Bookings.class));
            return true;
        } else {
            return false;
        }
    }

    private void toolbarSetter(ActionBar toolbar) {
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);
        toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp);
        toolbar.setTitle(getString(R.string.services));
    }
}