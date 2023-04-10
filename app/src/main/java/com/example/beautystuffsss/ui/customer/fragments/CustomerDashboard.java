package com.example.beautystuffsss.ui.customer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.ui.universalActivities.Services;
import com.example.beautystuffsss.ui.universalActivities.ChatPharmacist;
import com.example.beautystuffsss.ui.universalActivities.Promotions;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CustomerDashboard extends Fragment {
    CardView services, promotion, pickup, enquiry;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;
    private static final String TAG = "CustomerDashboard";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_dashboard, container, false);
        services = view.findViewById(R.id.customer_dashboard_cardServices);
        promotion = view.findViewById(R.id.customer_dashboard_cardPromotion);
        pickup = view.findViewById(R.id.customer_dashboard_cardPickup);
        enquiry = view.findViewById(R.id.customer_dashboard_cardEnquiry);
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(requireActivity());
        enquiry.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChatPharmacist.class)));
        services.setOnClickListener(v -> startActivity(new Intent(getActivity(), Services.class)));
        promotion.setOnClickListener(v -> startActivity(new Intent(getActivity(), Promotions.class)));
        pickup.setOnClickListener(v -> {
            progressDialog.show("Loading shop address");
            firebaseDatabase.getReference().child(Constants.pharmacyDetails).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    final String pharmacyAddress = snapshot.child(Constants.pharmacyAddress).getValue().toString();
                            String search = "";
                    try {
                        search = URLEncoder.encode(pharmacyAddress, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + search);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            });
        });
        return view;
    }
}