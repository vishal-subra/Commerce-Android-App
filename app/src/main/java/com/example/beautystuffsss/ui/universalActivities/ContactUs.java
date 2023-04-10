package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ImageHelper;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class ContactUs extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout facebookButton;
    TextView addressTextView, editButton, phoneTextView;
    ImageView pharmacyImage;
    Preferences preferences;
    ProgressDialog progressDialog;
    DatabaseReference pharmacyDetailsRef;
    FirebaseStorage storage;
    String fbLink;
    ImageHelper imageHelper;
    private static final String TAG = "ContactUs";


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        toolbar = findViewById(R.id.contact_us_toolbar);
        facebookButton = findViewById(R.id.contact_us_facebook);
        addressTextView = findViewById(R.id.contact_us_address);
        editButton = findViewById(R.id.contact_us_edit_tv);
        phoneTextView = findViewById(R.id.contact_us_phone);
        pharmacyImage = findViewById(R.id.contact_us_image);
        preferences = new Preferences(this);
        progressDialog = new ProgressDialog(this);
        imageHelper = new ImageHelper(this);
        pharmacyDetailsRef = FirebaseDatabase.getInstance().getReference().child(Constants.pharmacyDetails);
        storage = FirebaseStorage.getInstance();
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(v -> startActivity(new Intent(this, EditContactUs.class)));
        }
        addressTextView.setOnClickListener(v -> {
            String pharmacyAddress = addressTextView.getText().toString();
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
        });
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        facebookButton.setOnClickListener(v -> {
            Uri uri = Uri.parse(getString(R.string.pharmacy_fb_link));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.show("Loading details");
        pharmacyDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                phoneTextView.setText(snapshot.child(Constants.pharmacyContactNo).getValue().toString());
                addressTextView.setText(snapshot.child(Constants.pharmacyAddress).getValue().toString());
                fbLink = snapshot.child(Constants.pharmacyFacebookLink).getValue().toString();
                imageHelper.loadImage(snapshot.child(Constants.pharmacyPhotoUrl).getValue().toString(),pharmacyImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
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
        toolbar.setTitle(getString(R.string.contact_us));
    }
}