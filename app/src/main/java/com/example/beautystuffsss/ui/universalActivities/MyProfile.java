package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ImageHelper;
import com.example.beautystuffsss.util.Preferences;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {
    TextView name, email, phone, address;
    CircleImageView imageView;
    Button editButton;
    Preferences preferences;
    ImageHelper imageHelper;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    String TAG = "MyProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
            name = findViewById(R.id.my_profile_name);
            email = findViewById(R.id.my_profile_email);
            phone = findViewById(R.id.my_profile_phone);
            imageView = findViewById(R.id.my_profile_image);
            address = findViewById(R.id.my_profile_address);
            editButton = findViewById(R.id.my_profile_btnEdit);
            toolbar = findViewById(R.id.my_profile_toolbar);
            preferences = new Preferences(MyProfile.this);
            imageHelper = new ImageHelper(this);
            mAuth = FirebaseAuth.getInstance();
            setSupportActionBar(toolbar);
            toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(MyProfile.this, EditProfile.class);
                startActivity(intent);
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        name.setText(preferences.getString(Constants.currentUserName));
        email.setText(preferences.getString(Constants.currentUserEmail));
        phone.setText(preferences.getString(Constants.currentUserPhone));
        address.setText(preferences.getString(Constants.currentUserAddress));
        imageHelper.loadImage(preferences.getString(Constants.currentUserPhotoUrl), imageView);
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
        toolbar.setTitle(getString(R.string.my_profile));
    }
}