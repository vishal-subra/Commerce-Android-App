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

public class AddUpdateProduct extends AppCompatActivity {
    private static final int PICK_IMAGE = 104;
    Toolbar toolbar;
    ImageView productImage;
    EditText productTitle, productPrice,productDescription;
    Button pickImage, saveProduct, deleteProduct;
    String action, productId;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ImageHelper imageHelper;
    Uri imageUri;
    private static final String TAG = "AddUpdateProduct";
    boolean selectedPic, updatePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_product);
        toolbar = findViewById(R.id.add_product_toolbar);
        productImage = findViewById(R.id.add_product_imageView);
        productTitle = findViewById(R.id.add_product_title);
        productDescription = findViewById(R.id.add_product_description);
        productPrice = findViewById(R.id.add_product_price);
        pickImage = findViewById(R.id.add_product_pickImage);
        saveProduct = findViewById(R.id.add_product_btnSave);
        deleteProduct = findViewById(R.id.add_product_btnDelete);
        action = getIntent().getStringExtra(Constants.intentAction);
        progressDialog = new ProgressDialog(this);
        imageHelper = new ImageHelper(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.products);
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.images).child(Constants.productImages);
        selectedPic = false;
        updatePic = false;
        deleteProduct.setVisibility(View.GONE);
        if (action.equals(Constants.intentActionEdit)) {
            productId = getIntent().getStringExtra(Constants.productId);
            progressDialog.show("Loading product details");
            databaseReference.child(productId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        progressDialog.dismiss();
                        productTitle.setText(Objects.requireNonNull(snapshot.child(Constants.productName).getValue()).toString());
                        productPrice.setText(Objects.requireNonNull(snapshot.child(Constants.productPrice).getValue()).toString());
                        productDescription.setText(Objects.requireNonNull(snapshot.child(Constants.promoDescription).getValue()).toString());
                        imageHelper.loadImage(Objects.requireNonNull(snapshot.child(Constants.productPhotoUrl).getValue()).toString(), productImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            deleteProduct.setVisibility(View.VISIBLE);
            deleteProduct.setOnClickListener(v -> {
                databaseReference.child(productId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        onBackPressed();
                        progressDialog.dismiss();
                    }else{
                        Log.d(TAG, "onCreate: "+task.getException().getMessage());
                    }
                });
            });
        }
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));

        pickImage.setOnClickListener(v -> {
            pickPhoto();
        });
        saveProduct.setOnClickListener(v -> {
            if (productTitle.getText().toString().isEmpty()) {
                productTitle.setError("Please enter product title");
                productTitle.requestFocus();
            } else if (productPrice.getText().toString().isEmpty()) {
                productPrice.setError("Please enter product price");
                productPrice.requestFocus();
            }else if (productDescription.getText().toString().isEmpty()) {
                productDescription.setError("Please enter product price");
                productDescription.requestFocus();
            } else {
                saveProduct();
            }
        });

    }

    private void saveProduct() {
        if (action.equals(Constants.intentActionAdd)) {
            if (!selectedPic) {
                Toast.makeText(this, "Please select a pic", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show("Adding product...");
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
                    String productKey = databaseReference.push().getKey();
                    assert productKey != null;
                    UploadTask uploadTask = storageReference.child(productKey).putBytes(data);
                    uploadTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Map<String, Object> productData = new HashMap<>();
                            productData.put(Constants.productName, productTitle.getText().toString());
                            productData.put(Constants.productPrice, productPrice.getText().toString());
                            productData.put(Constants.promoDescription, productDescription.getText().toString());
                            productData.put(Constants.productId, productKey);
                            productData.put(Constants.productPhotoUrl, Constants.productImages + "/" + productKey);
                            databaseReference.child(productKey).setValue(productData).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        } else {
            progressDialog.show("Updating product");
            if (updatePic) {
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
                UploadTask uploadTask = storageReference.child(productId).putBytes(data);
                Map<String, Object> productData = new HashMap<>();
                productData.put(Constants.productName, productTitle.getText().toString());
                productData.put(Constants.productPrice, productPrice.getText().toString());
                productData.put(Constants.promoDescription, productDescription.getText().toString());
                productData.put(Constants.productId, productId);
                productData.put(Constants.productPhotoUrl, Constants.productImages + "/" + productId);
                databaseReference.child(productId).updateChildren(productData).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            } else {
                Map<String, Object> productData = new HashMap<>();
                productData.put(Constants.productName, productTitle.getText().toString());
                productData.put(Constants.productPrice, productPrice.getText().toString());
                productData.put(Constants.promoDescription, productDescription.getText().toString());
                productData.put(Constants.productId, productId);
                productData.put(Constants.productPhotoUrl, Constants.productImages + "/" + productId);
                databaseReference.child(productId).updateChildren(productData).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
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
                Glide.with(AddUpdateProduct.this).load(imageUri).into(productImage);
                selectedPic = true;
                if (action.equals(Constants.intentActionEdit)) {
                    updatePic = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddUpdateProduct.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(AddUpdateProduct.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
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
            toolbar.setTitle(getString(R.string.edit_product));
        } else
            toolbar.setTitle(getString(R.string.add_product));
    }
}