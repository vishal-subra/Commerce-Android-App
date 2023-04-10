package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.PromotionListAdapter;
import com.example.beautystuffsss.model.Promotion;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class Promotions extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    Preferences preferences;
    FloatingActionButton addPromotionFab;
    ArrayList<Promotion> promotions;
    PromotionListAdapter adapter;
    StorageReference storageReference;
    DatabaseReference promotionRef;
    ProgressDialog progressDialog;
    private static final String TAG = "Promotions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);
        toolbar = findViewById(R.id.promotions_toolbar);
        recyclerView = findViewById(R.id.promotions_recyclerView);
        addPromotionFab = findViewById(R.id.promotions_add_fab);
        promotions = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.images).child(Constants.promos);
        promotionRef = FirebaseDatabase.getInstance().getReference().child(Constants.promos);
        preferences = new Preferences(this);
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        adapter = new PromotionListAdapter(this, promotions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnReadMoreTapped(pos -> {
            Intent viewPromotionIntent = new Intent(this, ViewPromotion.class);
            viewPromotionIntent.putExtra(Constants.promoId, promotions.get(pos).getPromoId());
            startActivity(viewPromotionIntent);
        });
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
            addPromotionFab.show();
            addPromotionFab.setOnClickListener(v -> {
                Intent intent = new Intent(Promotions.this, EditPromotion.class);
                intent.putExtra(Constants.intentAction, Constants.intentActionAdd);
                startActivity(intent);
            });
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.show("Loading Promotions");
        promotionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promotions.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot promotion : snapshot.getChildren()) {
                        promotions.add(new Promotion(
                                promotion.getKey(),
                                Objects.requireNonNull(promotion.child(Constants.promoTitle).getValue()).toString(),
                                Objects.requireNonNull(promotion.child(Constants.promoDescription).getValue()).toString(),
                                Objects.requireNonNull(promotion.child(Constants.promoPhotoUrl).getValue().toString()),
                                Objects.requireNonNull(promotion.child(Constants.promoDatePosted).getValue()).toString()
                        ));
                    }
                    progressDialog.dismiss();
                    adapter.notifyDataSetChanged();
                } else {
                    progressDialog.dismiss();
                    promotions.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(Promotions.this, "No promotion available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void toolbarSetter(ActionBar toolbar) {
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);
        toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp);
        toolbar.setTitle(getString(R.string.promotions));
    }
}