package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditContactUs extends AppCompatActivity {
    private static final int PICK_IMAGE = 200;
    Toolbar toolbar;
    ImageView pharmacyPhoto;
    EditText phone, address, facebookLink;
    Button pickPhoto, saveButton;
    Bitmap pharmacyImage;
    DatabaseReference pharmacyDetailsRef;
    FirebaseStorage storage;
    ProgressDialog progressDialog;
    Preferences preferences;
    boolean selectedPic;
    ImageHelper imageHelper;
    private static final String TAG = "EditContactUs";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_us);
        toolbar = findViewById(R.id.edit_contact_us_toolbar);
        pharmacyPhoto = findViewById(R.id.edit_contact_us_pharma_photo);
        phone = findViewById(R.id.edit_contact_us_phone_et);
        address = findViewById(R.id.edit_contact_us_address_et);
        facebookLink = findViewById(R.id.edit_contact_us_fbLink_et);
        pickPhoto = findViewById(R.id.edit_contact_us_btnPickPhoto);
        saveButton = findViewById(R.id.edit_contact_us_btnSave);
        preferences = new Preferences(this);
        progressDialog = new ProgressDialog(this);
        imageHelper = new ImageHelper(this);
        pharmacyDetailsRef = FirebaseDatabase.getInstance().getReference().child(Constants.pharmacyDetails);
        storage = FirebaseStorage.getInstance();
        setSupportActionBar(toolbar);
        progressDialog.show("Loading details");
        pharmacyDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                phone.setText(Objects.requireNonNull(snapshot.child(Constants.pharmacyContactNo).getValue()).toString());
                address.setText(Objects.requireNonNull(snapshot.child(Constants.pharmacyAddress).getValue()).toString());
                facebookLink.setText(Objects.requireNonNull(snapshot.child(Constants.pharmacyFacebookLink).getValue()).toString());
                imageHelper.loadImage(Objects.requireNonNull(snapshot.child(Constants.pharmacyPhotoUrl).getValue()).toString(), pharmacyPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
        selectedPic = false;
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        phone.setText(getString(R.string.pharmacy_phone));
        address.setText(getString(R.string.pharmacy_address));
        facebookLink.setText(getString(R.string.pharmacy_fb_link));
        Glide.with(this).load(R.drawable.pharma).into(pharmacyPhoto);
        pickPhoto.setOnClickListener(v -> pickPhoto());
        saveButton.setOnClickListener(v -> {
            if (phone.getText().toString().length() != 9) {
                phone.requestFocus();
                phone.setError("Enter a phone number with 9 digits");
            } else if (address.getText().toString().length() < 20) {
                address.requestFocus();
                address.setError("Address is too short");
            } else if (facebookLink.getText().toString().isEmpty()) {
                facebookLink.requestFocus();
                facebookLink.setError("Please provide facebook link");
            } else {
                updatePharmacyDetails(phone.getText().toString(), address.getText().toString(), facebookLink.getText().toString());
            }
        });
    }

    private void updatePharmacyDetails(String pharmacyPhone, String pharmacyAddress, String pharmacyFacebookLink) {
        progressDialog.show("Updating pharmacy details");
        if (selectedPic) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            assert pharmacyImage != null;
            pharmacyImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storage.getReference().child(Constants.images).child(Constants.pharmacyPhotoName).putBytes(data);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Map<String, Object> pharmacyDetails = new HashMap<>();
                    pharmacyDetails.put(Constants.pharmacyPhotoUrl, Constants.pharmacyPhotoName);
                    pharmacyDetails.put(Constants.pharmacyContactNo, pharmacyPhone);
                    pharmacyDetails.put(Constants.pharmacyAddress, pharmacyAddress);
                    pharmacyDetails.put(Constants.pharmacyFacebookLink, pharmacyFacebookLink);
                    pharmacyDetailsRef.updateChildren(pharmacyDetails).addOnCompleteListener(dataTask -> {
                        progressDialog.dismiss();
                        if (dataTask.isSuccessful()) {
                            Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.d(TAG, "updatePharmacyDetails: " + dataTask.getException());
                        }
                    });
                } else {
                    Log.d(TAG, "updatePharmacyDetails: ");
                }
            });
        } else {
            Map<String, Object> pharmacyDetails = new HashMap<>();
            pharmacyDetails.put(Constants.pharmacyContactNo, pharmacyPhone);
            pharmacyDetails.put(Constants.pharmacyAddress, pharmacyAddress);
            pharmacyDetails.put(Constants.pharmacyFacebookLink, pharmacyFacebookLink);
            pharmacyDetailsRef.updateChildren(pharmacyDetails).addOnCompleteListener(dataTask -> {
                progressDialog.dismiss();
                if (dataTask.isSuccessful()) {
                    Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.d(TAG, "updatePharmacyDetails: " + dataTask.getException());
                }
            });
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
                final Uri imageUri = Objects.requireNonNull(data).getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                pharmacyImage = BitmapFactory.decodeStream(imageStream);
                Glide.with(EditContactUs.this).load(pharmacyImage).into(pharmacyPhoto);
                selectedPic = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditContactUs.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(EditContactUs.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
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
        toolbar.setTitle(getString(R.string.edit_contact_us));
    }
}