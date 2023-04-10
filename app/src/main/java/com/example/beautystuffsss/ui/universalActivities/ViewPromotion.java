package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Objects;

public class ViewPromotion extends AppCompatActivity {
    Toolbar toolbar;
    TextView title, description, date;
    Preferences preferences;
    String promoId;
    DatabaseReference promotionRef;
    ImageView promoImageView;
    ProgressDialog progressDialog;
    ImageHelper imageHelper;
    Button editPromo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_promotion);
        promoId = getIntent().getStringExtra(Constants.promoId);
        toolbar = findViewById(R.id.promotion_view_toolbar);
        title = findViewById(R.id.promotion_view_titleTextView);
        date = findViewById(R.id.promotion_view_dateTextView);
        description = findViewById(R.id.promotion_view_descTextView);
        promoImageView = findViewById(R.id.promotion_view_imageView);
        editPromo = findViewById(R.id.promotion_view_btnEdit);
        imageHelper = new ImageHelper(this);
        preferences = new Preferences(this);
        progressDialog = new ProgressDialog(this);
        promotionRef = FirebaseDatabase.getInstance().getReference().child(Constants.promos);
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
            editPromo.setOnClickListener(v -> {
                Intent editPromoIntent = new Intent(this, EditPromotion.class);
                editPromoIntent.putExtra(Constants.intentAction, Constants.intentActionEdit);
                editPromoIntent.putExtra(Constants.promoId, promoId);
                startActivity(editPromoIntent);
            });
        }else{
            editPromo.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
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

    private void toolbarSetter(@NonNull ActionBar toolbar) {
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);
        toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp);
        toolbar.setTitle(getString(R.string.promotion_details));
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.show("Loading promo details");
        promotionRef.child(promoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    progressDialog.dismiss();
                    title.setText(Objects.requireNonNull(snapshot.child(Constants.promoTitle).getValue()).toString());
                    description.setText(Objects.requireNonNull(snapshot.child(Constants.promoDescription).getValue()).toString());
                    date.setText(Objects.requireNonNull(snapshot.child(Constants.promoDatePosted).getValue()).toString());
                    imageHelper.loadImage(Objects.requireNonNull(snapshot.child(Constants.promoPhotoUrl).getValue()).toString(), promoImageView);
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(ViewPromotion.this, "Promotion no more exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}