package com.example.beautystuffsss.ui.universalActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.ui.customer.activities.customerAuth.CustomerScreen;
import com.example.beautystuffsss.ui.pharmacist.pharmacistAuth.PharmacistScreen;

public class Intro extends AppCompatActivity {

    Button mCustomer;
    Button mPharmacist;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        mCustomer =findViewById(R.id.customer_btn);
        mPharmacist= findViewById(R.id.pharmacist_btn);

        mCustomer.setOnClickListener(view -> startActivity(new Intent(Intro.this, CustomerScreen.class)));

        mPharmacist.setOnClickListener(view -> startActivity(new Intent(Intro.this, PharmacistScreen.class)));

    }
}