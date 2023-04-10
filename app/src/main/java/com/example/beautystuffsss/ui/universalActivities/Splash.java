package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.ui.customer.activities.CustomerHome;
import com.example.beautystuffsss.ui.pharmacist.PharmacistHome;
import com.example.beautystuffsss.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class Splash extends AppCompatActivity {
    FirebaseUser user;
    FirebaseFirestore firestore;
    private static final String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        new Handler().postDelayed(() -> {
            if (user != null) {
                firestore.collection(Constants.users).document(user.getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onCreate: "+ Objects.requireNonNull(task.getResult()).getId());
                        if (Objects.equals(task.getResult().getString(Constants.currentUserType), Constants.uTypeCustomer)) {
                            // Signed In user is customer
                            startActivity(new Intent(this, CustomerHome.class));
                            finish();
                        } else if (Objects.equals(task.getResult().getString(Constants.currentUserType), Constants.uTypePharmacist)) {
                            //Signed In user is pharmacist
                            startActivity(new Intent(this, PharmacistHome.class));
                            finish();
                        }
                    } else {
                        Log.d(TAG, "onCreate: Unable to get user details"+task.getException().getMessage());
                    }
                });
            } else {
                startActivity(new Intent(this, Intro.class));
                finish();
            }
        }, 5000);
    }
}