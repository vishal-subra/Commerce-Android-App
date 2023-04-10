package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.Image;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ImageHelper;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EditPromotion extends AppCompatActivity {
    Toolbar toolbar;
    String action, promoId;
    ArrayList<Image> images;
    ArrayList<String> promoKeys;
    Button btnPickImage, btnSave, btnDelete;
    EditText title, description;
    StorageReference storageReference;
    ImageView promoImage;
    DatabaseReference promotionRef;
    final int PICK_IMAGE = 101;
    ProgressDialog progressDialog;
    ImageHelper imageHelper;
    boolean picSelected;
    Uri imageUri;
    private static final String TAG = "EditPromotion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_promotion);
        action = getIntent().getStringExtra(Constants.intentAction);
        toolbar = findViewById(R.id.edit_promotion_toolbar);
        btnPickImage = findViewById(R.id.edit_promotion_btnPickImage);
        btnSave = findViewById(R.id.edit_promotion_btnSave);
        btnDelete = findViewById(R.id.edit_promotion_btnDelete);
        title = findViewById(R.id.edit_promotion_title_et);
        description = findViewById(R.id.edit_promotion_description_et);
        promoImage = findViewById(R.id.promotion_imageView);
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.images).child(Constants.promos);
        promotionRef = FirebaseDatabase.getInstance().getReference().child(Constants.promos);
        promoKeys = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        imageHelper = new ImageHelper(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        title.requestFocus();
        btnDelete.setVisibility(View.GONE);
        picSelected = false;
        if (action.equals(Constants.intentActionEdit)) {
            promoId = getIntent().getStringExtra(Constants.promoId);
            progressDialog.show("Loading promo details");
            promotionRef.child(promoId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    if (snapshot.exists()) {
                        title.setText(Objects.requireNonNull(snapshot.child(Constants.promoTitle).getValue()).toString());
                        description.setText(Objects.requireNonNull(snapshot.child(Constants.promoDescription).getValue()).toString());
                        imageHelper.loadImage(snapshot.child(Constants.promoPhotoUrl).getValue().toString(), promoImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> {
                promotionRef.child(promoId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        startActivity(new Intent(EditPromotion.this,Promotions.class));
                        finish();
                    } else {
                        Log.d(TAG, "onCreate: " + task.getException().getMessage());
                    }
                });
            });
        }
        btnPickImage.setOnClickListener(v -> {
            pickPhoto();
        });
        btnSave.setOnClickListener(v -> {
            if (title.getText().toString().isEmpty()) {
                title.setError("Title is required");
                title.requestFocus();
            } else if (description.getText().toString().isEmpty()) {
                description.setError("Description is required");
                description.requestFocus();
            } else {
                savePromotion();
            }
        });
    }

    private void savePromotion() {
        if (action.equals(Constants.intentActionAdd)) {
            if (picSelected) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                String promoKey = promotionRef.push().getKey();
                progressDialog.show("Adding promo");
                UploadTask uploadTask = storageReference.child(promoKey).putBytes(data);
                uploadTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Map<String, String> promo = new HashMap<>();
                        promo.put(Constants.promoId,promoKey);
                        promo.put(Constants.promoTitle,title.getText().toString());
                        promo.put(Constants.promoDescription,description.getText().toString());
                        promo.put(Constants.promoPhotoUrl,Constants.promos+"/"+promoKey);
                        promo.put(Constants.promoDatePosted,new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date()));
                        promotionRef.child(promoKey).setValue(promo).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(this, "Promotion added successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Log.d(TAG, "savePromotion: "+task.getException().getMessage());
                    }
                });
            }else{
                Toast.makeText(this, "Pick a photo first", Toast.LENGTH_SHORT).show();
            }
        }else if (action.equals(Constants.intentActionEdit)){
            if (picSelected) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                String promoKey = promoId;
                progressDialog.show("Adding promo");
                UploadTask uploadTask = storageReference.child(promoKey).putBytes(data);
                uploadTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Map<String, Object> promo = new HashMap<>();
                        promo.put(Constants.promoId,promoKey);
                        promo.put(Constants.promoTitle,title.getText().toString());
                        promo.put(Constants.promoDescription,description.getText().toString());
                        promo.put(Constants.promoPhotoUrl,Constants.promos+"/"+promoKey);
                        promo.put(Constants.promoDatePosted,new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date()));
                        promotionRef.child(promoKey).updateChildren(promo).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(this, "Promotion added successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Log.d(TAG, "savePromotion: "+task.getException().getMessage());
                    }
                });
            }else{
                progressDialog.show("Updating");
                Map<String, Object> promo = new HashMap<>();
                promo.put(Constants.promoId,promoId);
                promo.put(Constants.promoTitle,title.getText().toString());
                promo.put(Constants.promoDescription,description.getText().toString());
                promo.put(Constants.promoPhotoUrl,Constants.promos+"/"+promoId);
                promo.put(Constants.promoDatePosted,new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date()));
                promotionRef.child(promoId).updateChildren(promo).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(this, "Promotion added successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            try {
                imageUri = Objects.requireNonNull(data).getData();
                Glide.with(this).load(imageUri).into(promoImage);
                picSelected = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditPromotion.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(EditPromotion.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
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

    private void toolbarSetter(ActionBar toolbar) {
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);
        toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp);
        if (action.equals(Constants.intentActionEdit)) {
            toolbar.setTitle(getString(R.string.edit_promo));
        } else if (action.equals(Constants.intentActionAdd)) {
            toolbar.setTitle(getString(R.string.add_promo));
        }
    }
}