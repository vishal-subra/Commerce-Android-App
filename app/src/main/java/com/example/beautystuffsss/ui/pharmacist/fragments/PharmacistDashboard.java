package com.example.beautystuffsss.ui.pharmacist.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.ui.universalActivities.ChatPharmacist;
import com.example.beautystuffsss.ui.universalActivities.Promotions;
import com.example.beautystuffsss.ui.universalActivities.Services;

public class PharmacistDashboard extends Fragment {
    CardView cardServices,cardPromotion,cardChatHistory;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pharmacist_dashboard, container, false);

        cardServices = view.findViewById(R.id.pharmacist_dashboard_cardServices);
        cardPromotion = view.findViewById(R.id.pharmacist_dashboard_cardPromotion);
        cardChatHistory = view.findViewById(R.id.pharmacist_dashboard_cardChatHistory);
        cardPromotion.setOnClickListener(v -> startActivity(new Intent(getActivity(), Promotions.class)));
        cardServices.setOnClickListener(v -> startActivity(new Intent(getActivity(), Services.class)));
        cardChatHistory.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChatPharmacist.class)));
        return view;

    }
}