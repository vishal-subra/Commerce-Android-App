package com.example.beautystuffsss.util;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.beautystuffsss.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageHelper {
    private final Context context;
    private static final String TAG = "ImageHelper";
    StorageReference imagesReference;
    ProgressDialog progressDialog;

    public ImageHelper(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        imagesReference = firebaseStorage.getReference().child(Constants.images);
    }

    public void loadImage(String url, ImageView imageView) {
        imagesReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context.getApplicationContext()).load(uri).placeholder(R.drawable.photo_unavailable).error(R.drawable.photo_unavailable).into(imageView)).addOnFailureListener(e -> Log.d(TAG, "loadImage: " + e.getMessage()));
    }
}
