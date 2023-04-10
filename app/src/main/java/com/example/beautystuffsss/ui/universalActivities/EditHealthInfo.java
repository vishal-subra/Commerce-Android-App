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

import com.bumptech.glide.Glide;
import com.example.beautystuffsss.R;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditHealthInfo extends AppCompatActivity {
    Toolbar toolbar;
    String type;
    ImageView addHealthInfoImageView;
    Button btnPickImage, btnSave, btnDelete;
    EditText title, description, youtubeUrl;
    final int PICK_IMAGE = 102;
    Uri imageUri;
    boolean selectedPic;
    DatabaseReference healthInfoRef;
    StorageReference storageReference;
    private static final String TAG = "EditHealthInfo";
    ProgressDialog progressDialog;
    String action, healthInfoId;
    ImageHelper imageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__edit_healthinfo);
        action = getIntent().getStringExtra(Constants.intentAction);
        type = getIntent().getStringExtra(Constants.intentAction);
        toolbar = findViewById(R.id.add_healthInfo_toolbar);
        addHealthInfoImageView = findViewById(R.id.add_healthInfo_imageView);
        btnPickImage = findViewById(R.id.add_healthInfo_btnPickImage);
        btnSave = findViewById(R.id.add_healthInfo_btnSave);
        btnDelete = findViewById(R.id.add_healthInfo_btnDelete);
        title = findViewById(R.id.add_healthInfo_title_et);
        description = findViewById(R.id.add_healthInfo_description_et);
        youtubeUrl = findViewById(R.id.add_healthInfo_youtubeLink_et);
        progressDialog = new ProgressDialog(this);
        imageHelper = new ImageHelper(this);
        selectedPic = false;
        healthInfoRef = FirebaseDatabase.getInstance().getReference().child(Constants.healthInfo);
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.images).child(Constants.healthInfo);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        setSupportActionBar(toolbar);
        toolbarSetter(getSupportActionBar());
        btnDelete.setVisibility(View.GONE);
        if (action.equals(Constants.intentActionEdit)) {
            // we are editing an available health news
            healthInfoId = getIntent().getStringExtra(Constants.healthInfoId);
            progressDialog.show("Loading health info details");
            healthInfoRef.child(healthInfoId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        title.setText(snapshot.child(Constants.healthInfoTitle).getValue().toString());
                        youtubeUrl.setText(snapshot.child(Constants.healthYoutubeUrl).getValue().toString());
                        description.setText(snapshot.child(Constants.healthInfoDescription).getValue().toString());
                        imageHelper.loadImage(snapshot.child(Constants.healthInfoPhotoUrl).getValue().toString(), addHealthInfoImageView);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> {
                progressDialog.show("Deleting HealthInfo");
                healthInfoRef.child(healthInfoId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        onBackPressed();
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
                title.requestFocus();
                title.setError("Please enter title");
            } else if (youtubeUrl.getText().toString().isEmpty()) {
                youtubeUrl.requestFocus();
                youtubeUrl.setError("Please enter title");
            } else if (description.getText().toString().isEmpty()) {
                description.requestFocus();
                description.setError("Please enter title");
            } else {
                saveHealthInfo();
            }
        });
    }

    private void saveHealthInfo() {
        if (action.equals(Constants.intentActionAdd)) {
            if (selectedPic) {
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
                String pushKey = healthInfoRef.push().getKey();
                UploadTask uploadTask = storageReference.child(pushKey).putBytes(data);
                progressDialog.show("Adding health Info");
                uploadTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> healthInfo = new HashMap<>();
                        healthInfo.put(Constants.healthInfoId, pushKey);
                        healthInfo.put(Constants.healthInfoPhotoUrl, Constants.healthInfo + "/" + pushKey);
                        healthInfo.put(Constants.healthInfoTitle, title.getText().toString());
                        healthInfo.put(Constants.healthYoutubeUrl, youtubeUrl.getText().toString());
                        healthInfo.put(Constants.healthInfoDescription, description.getText().toString());
                        healthInfoRef.child(pushKey).setValue(healthInfo).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Health Info added", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "saveHealthInfo: " + task.getException());
                            }
                        });
                    } else {
                        Toast.makeText(this, "Unable to upload Image", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please pick a photo also", Toast.LENGTH_SHORT).show();
            }
        } else if (action.equals(Constants.intentActionEdit)) {
            if (selectedPic) {
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
                String pushKey = healthInfoId;
                UploadTask uploadTask = storageReference.child(pushKey).putBytes(data);
                progressDialog.show("Updating health info");
                uploadTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> healthInfo = new HashMap<>();
                        healthInfo.put(Constants.healthInfoId, pushKey);
                        healthInfo.put(Constants.healthInfoPhotoUrl, Constants.healthInfo + "/" + pushKey);
                        healthInfo.put(Constants.healthInfoTitle, title.getText().toString());
                        healthInfo.put(Constants.healthYoutubeUrl, youtubeUrl.getText().toString());
                        healthInfo.put(Constants.healthInfoDescription, description.getText().toString());
                        healthInfoRef.child(pushKey).updateChildren(healthInfo).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Health Info added", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "saveHealthInfo: " + task.getException());
                            }
                        });
                    } else {
                        Toast.makeText(this, "Unable to upload Image", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressDialog.show("Updating health info");
                Map<String, Object> healthInfo = new HashMap<>();
                healthInfo.put(Constants.healthInfoId, healthInfoId);
                healthInfo.put(Constants.healthInfoPhotoUrl, Constants.healthInfo + "/" + healthInfoId);
                healthInfo.put(Constants.healthInfoTitle, title.getText().toString());
                healthInfo.put(Constants.healthYoutubeUrl, youtubeUrl.getText().toString());
                healthInfo.put(Constants.healthInfoDescription, description.getText().toString());
                healthInfoRef.child(healthInfoId).updateChildren(healthInfo).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Health Info added", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "saveHealthInfo: " + task1.getException());
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
                Glide.with(this).load(imageUri).into(addHealthInfoImageView);
                selectedPic = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditHealthInfo.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(EditHealthInfo.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
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
        if (type.equals(Constants.intentActionEdit)) {
            toolbar.setTitle(getString(R.string.edit_healthInfo));
        } else if (type.equals(Constants.intentActionAdd)) {
            toolbar.setTitle(getString(R.string.add_healthInfo));
        }
    }
}