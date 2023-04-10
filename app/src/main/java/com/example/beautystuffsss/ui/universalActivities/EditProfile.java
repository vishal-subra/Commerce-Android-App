package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    EditText email, phone, name, address;
    CircleImageView circleImageView;
    ImageButton editButton;
    Button saveButton;
    Toolbar toolbar;
    Preferences preferences;
    ProgressDialog progressDialog;
    boolean selectedPic;
    Uri imageUri;
    private static final String TAG = "EditProfile";
    String photoURL;
    StorageReference storageReference;
    FirebaseFirestore firestore;
    ImageHelper imageHelper;
    final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        preferences = new Preferences(EditProfile.this);
        name = findViewById(R.id.edit_profile_name);
        email = findViewById(R.id.edit_profile_email);
        phone = findViewById(R.id.edit_profile_phone);
        address = findViewById(R.id.edit_profile_address);
        circleImageView = findViewById(R.id.edit_profile_image);
        editButton = findViewById(R.id.edit_profile_editPicture_btnEdit);
        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        saveButton = findViewById(R.id.edit_profile_btnSave);
        progressDialog = new ProgressDialog(this);
        imageHelper = new ImageHelper(this);
        selectedPic = false;
        name.setText(preferences.getString(Constants.currentUserName));
        email.setText(preferences.getString(Constants.currentUserEmail));
        phone.setText(preferences.getString(Constants.currentUserPhone));
        address.setText(preferences.getString(Constants.currentUserAddress));
        imageHelper.loadImage(preferences.getString(Constants.currentUserPhotoUrl),circleImageView);
        toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        editButton.setOnClickListener(v -> pickPhoto());
        saveButton.setOnClickListener(v -> {
            if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                email.setError("Invalid Email");
                email.setFocusable(true);
            } else if (name.getText().toString().isEmpty()) {
                name.setError("Name is required");
            } else if (!phone.getText().toString().matches("[0-9]{10}")) {
                phone.setError("Phone number is invalid");
            } else if (address.getText().toString().isEmpty()) {
                address.setError("Address is required");
            } else {
                saveUser();
            }
        });
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
                Glide.with(EditProfile.this).load(imageUri).into(circleImageView);
                selectedPic = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(EditProfile.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }


    private void saveUser() {
        Map<String, Object> userData = new HashMap<>();
        progressDialog.show("Updating Profile");
        if (selectedPic) {
            if (imageUri != null) {
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
                UploadTask uploadTask = storageReference.child(Constants.images).child(Constants.profileImages).child(preferences.getString(Constants.currentUserId)).putBytes(data);
                uploadTask.addOnFailureListener(exception -> {
                    Log.d(TAG, "saveUser: " + exception.getMessage());
                }).addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        photoURL = Constants.profileImages+"/"+ preferences.getString(Constants.currentUserId);
                        userData.put(Constants.currentUserEmail, email.getText().toString().trim());
                        userData.put(Constants.currentUserName, name.getText().toString());
                        userData.put(Constants.currentUserPhone, phone.getText().toString());
                        userData.put(Constants.currentUserAddress, address.getText().toString());
                        userData.put(Constants.currentUserPhotoUrl, photoURL);
                        userData.put(Constants.currentUserType, preferences.getString(Constants.currentUserType));
                        firestore.collection(Constants.users).document(preferences.getString(Constants.currentUserId)).update(userData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                onBackPressed();
                                saveDataToPreferences(
                                        email.getText().toString().trim(),
                                        name.getText().toString().trim(),
                                        phone.getText().toString().trim(),
                                        address.getText().toString().trim(),
                                        photoURL
                                        );

                            }
                        });
                    });
                });
            }
        } else {
            userData.put(Constants.currentUserEmail, email.getText().toString().trim());
            userData.put(Constants.currentUserName, name.getText().toString());
            userData.put(Constants.currentUserPhone, phone.getText().toString());
            userData.put(Constants.currentUserAddress, address.getText().toString());
            userData.put(Constants.currentUserType, preferences.getString(Constants.currentUserType));
            firestore.collection(Constants.users).document(preferences.getString(Constants.currentUserId)).update(userData).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            saveDataToPreferences(
                                    email.getText().toString().trim(),
                                    name.getText().toString().trim(),
                                    phone.getText().toString().trim(),
                                    address.getText().toString().trim(),
                                    Constants.noPhotoUrl
                            );
                            Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
            );
        }
    }

    private void saveDataToPreferences(
            final String email,
            final String fullName,
            final String phone,
            final String address,
            final String photoURL) {
        preferences.saveString(Constants.currentUserEmail, email);
        preferences.saveString(Constants.currentUserName, fullName);
        preferences.saveString(Constants.currentUserPhone, phone);
        preferences.saveString(Constants.currentUserAddress, address);
        preferences.saveString(Constants.currentUserPhotoUrl, photoURL);
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
        toolbar.setTitle(getString(R.string.edit_profile));
    }
}