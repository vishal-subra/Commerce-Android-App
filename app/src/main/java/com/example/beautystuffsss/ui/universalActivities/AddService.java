package com.example.beautystuffsss.ui.universalActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddService extends AppCompatActivity {
    Toolbar toolbar;
    String action, serviceId;
    EditText title, desc;
    Button submitButton,deleteService;
    DatabaseReference servicesReference;
    ProgressDialog progressDialog;
    private static final String TAG = "AddService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        toolbar = findViewById(R.id.add_service_toolbar);
        title = findViewById(R.id.add_service_title_et);
        desc = findViewById(R.id.add_service_desc_et);
        submitButton = findViewById(R.id.add_service_btnSubmit);
        deleteService = findViewById(R.id.delete_service_button);
        action = getIntent().getStringExtra(Constants.intentAction);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        servicesReference = FirebaseDatabase.getInstance().getReference().child(Constants.services);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        if (action.equals(Constants.intentActionEdit)) {
            serviceId = getIntent().getStringExtra(Constants.serviceId);
            progressDialog.show("Loading Service details");
            deleteService.setVisibility(View.VISIBLE);
            servicesReference.child(serviceId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot service) {
                    if (service.exists()){
                        progressDialog.dismiss();
                        title.setText(Objects.requireNonNull(service.child(Constants.serviceTitle).getValue()).toString());
                        desc.setText(Objects.requireNonNull(service.child(Constants.serviceDesc).getValue()).toString());
                    }else{
                        onBackPressed();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        deleteService.setOnClickListener(v -> {
            progressDialog.show("Deleting Service");
            servicesReference.child(serviceId).removeValue().addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Service deletion successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
        submitButton.setOnClickListener(v -> {
            if (title.getText().toString().isEmpty()) {
                title.setError("Service Title is required");
                title.requestFocus();
            } else if (desc.getText().toString().isEmpty()) {
                desc.setError("Service Title is required");
                desc.requestFocus();
            } else {
                saveService();
            }
        });
    }

    private void saveService() {
        progressDialog.show("Adding your service.");
        Map<String, String> service = new HashMap<>();
        String serviceKey;
        if (action.equals(Constants.intentActionEdit)) {
            serviceKey = serviceId;
        } else {
            serviceKey = servicesReference.push().getKey();
        }
        service.put(Constants.serviceTitle, title.getText().toString());
        service.put(Constants.serviceDesc, desc.getText().toString());
        service.put(Constants.serviceId, serviceKey);
        assert serviceKey != null;
        servicesReference.child(serviceKey).setValue(service).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Service Added", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                progressDialog.dismiss();
                Log.d(TAG, "saveService: " + task.getException());
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
        if (action.equals(Constants.intentActionEdit)) {
            toolbar.setTitle(getString(R.string.edit_service));
        } else {
            toolbar.setTitle(getString(R.string.add_service));
        }
    }
}