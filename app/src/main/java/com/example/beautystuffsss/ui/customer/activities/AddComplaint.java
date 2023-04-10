package com.example.beautystuffsss.ui.customer.activities;

import android.os.Bundle;
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
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

public class AddComplaint extends AppCompatActivity {
    Toolbar toolbar;
    MaterialSpinner spinner;
    EditText productName, lotNumber, mfgDate, expDate, reason;
    Button btnAddComplaint;
    int spinnerIndex;
    String complaintType;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    Preferences preferences;
    String action, complaintId;
    private static final String TAG = "AddComplaint";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);
        toolbar = findViewById(R.id.add_complaint_toolbar);
        spinner = findViewById(R.id.add_complaint_category_spinner);
        productName = findViewById(R.id.add_complaint_product_name_et);
        lotNumber = findViewById(R.id.add_complaint_product_lot_number_et);
        mfgDate = findViewById(R.id.add_complaint_product_mfg_date_et);
        expDate = findViewById(R.id.add_complaint_product_exp_date_et);
        reason = findViewById(R.id.add_complaint_reason_et);
        btnAddComplaint = findViewById(R.id.add_complaint_btnSubmit);
        action = getIntent().getStringExtra(Constants.intentAction);
        progressDialog = new ProgressDialog(this);
        preferences = new Preferences(this);
        database = FirebaseDatabase.getInstance();

        setSupportActionBar(toolbar);
        toolbarSetter(getSupportActionBar());
        spinnerSetter();
        if (action.equals(Constants.intentActionEdit)) {
            complaintId = getIntent().getStringExtra(Constants.complaintId);
            complaintType = getIntent().getStringExtra(Constants.complaintType);
            progressDialog.show("Loading complaint details");
            database.getReference().child(Constants.complaints).child(complaintType).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot complaint : snapshot.getChildren()) {
                        if (complaint.child(Constants.complaintId).getValue().toString().equals(complaintId)) {
                            progressDialog.dismiss();
                            if (complaint.child(Constants.complaintType).getValue().toString().equals(Constants.complaintTypeProduct))
                            {
                                complaintType = Constants.complaintTypeProduct;
                                reason.setVisibility(View.VISIBLE);
                                btnAddComplaint.setVisibility(View.VISIBLE);
                                productName.setVisibility(View.VISIBLE);
                                lotNumber.setVisibility(View.VISIBLE);
                                mfgDate.setVisibility(View.VISIBLE);
                                expDate.setVisibility(View.VISIBLE);
                                spinner.setSelectedIndex(1);
                                reason.setText(complaint.child(Constants.complaintReason).getValue().toString());
                                productName.setText(complaint.child(Constants.productName).getValue().toString());
                                lotNumber.setText(complaint.child(Constants.complaintProductLotNo).getValue().toString());
                                mfgDate.setText(complaint.child(Constants.complaintProductMfg).getValue().toString());
                                expDate.setText(complaint.child(Constants.complaintProductExp).getValue().toString());
                            }else {
                                complaintType = Constants.complaintTypeService;
                                spinner.setSelectedIndex(2);
                                reason.setVisibility(View.VISIBLE);
                                reason.setText(complaint.child(Constants.complaintReason).getValue().toString());
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        btnAddComplaint.setOnClickListener(v -> {
            if (complaintType.equals(Constants.complaintTypeProduct)) {
                // Product complaint
                if (productName.getText().toString().isEmpty()) {
                    productName.setError("Enter product name");
                    productName.requestFocus();
                } else if (lotNumber.getText().toString().isEmpty()) {
                    lotNumber.setError("Enter lot number");
                    lotNumber.requestFocus();
                } else if (mfgDate.getText().toString().isEmpty()) {
                    mfgDate.setError("Enter Manufacturing Date");
                    mfgDate.requestFocus();
                } else if (expDate.getText().toString().isEmpty()) {
                    expDate.setError("Enter lot number");
                    expDate.requestFocus();
                } else if (reason.getText().toString().length() < 20) {
                    reason.setError("Explain a bit more");
                    reason.requestFocus();
                } else {
                    Map<String, String> complaint = new HashMap<>();
                    String complaintId = database.getReference().child(complaintType).push().getKey();
                    complaint.put(Constants.complaintReason, reason.getText().toString());
                    complaint.put(Constants.complaintType, complaintType);
                    complaint.put(Constants.complaintProductName, productName.getText().toString());
                    complaint.put(Constants.complaintProductLotNo, lotNumber.getText().toString());
                    complaint.put(Constants.complaintProductMfg, mfgDate.getText().toString());
                    complaint.put(Constants.complaintProductExp, expDate.getText().toString());
                    complaint.put(Constants.complainerId, preferences.getString(Constants.currentUserId));
                    complaint.put(Constants.complaintId, complaintId);
                    complaint.put(Constants.complaintStatus, Constants.complaintStatusPending);
                    progressDialog.show("Adding your complaint");
                    database.getReference().child(Constants.complaints).child(complaintType).child(complaintId).setValue(complaint).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Complaint SuccessfullyAdded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Unable to add complaint", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (complaintType.equals(Constants.complaintTypeService)) {
                // Service complaints
                if (reason.getText().toString().length() < 20) {
                    reason.setError("Explain a bit more");
                    reason.requestFocus();
                } else {
                    String complaintId = database.getReference().child(complaintType).push().getKey();
                    Map<String, String> complaint = new HashMap<>();
                    complaint.put(Constants.complaintReason, reason.getText().toString());
                    complaint.put(Constants.complaintType, complaintType);
                    complaint.put(Constants.complainerId, preferences.getString(Constants.currentUserId));
                    complaint.put(Constants.complaintId, complaintId);
                    complaint.put(Constants.complaintStatus, Constants.complaintStatusPending);
                    progressDialog.show("Adding your complaint...");
                    database.getReference().child(Constants.complaints).child(complaintType).child(complaintId).setValue(complaint).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Complaint SuccessfullyAdded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Unable to add complaint", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void spinnerSetter() {
        spinner.setItems("Select Complaint Category", Constants.complaintTypeProduct, Constants.complaintTypeService);
        spinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
            if (position == 1) {
                complaintType = Constants.complaintTypeProduct;
                reason.setVisibility(View.VISIBLE);
                btnAddComplaint.setVisibility(View.VISIBLE);
                productName.setVisibility(View.VISIBLE);
                lotNumber.setVisibility(View.VISIBLE);
                mfgDate.setVisibility(View.VISIBLE);
                expDate.setVisibility(View.VISIBLE);

            } else if (position == 2) {
                complaintType = Constants.complaintTypeService;
                reason.setVisibility(View.VISIBLE);
                btnAddComplaint.setVisibility(View.VISIBLE);
                productName.setVisibility(View.GONE);
                lotNumber.setVisibility(View.GONE);
                mfgDate.setVisibility(View.GONE);
                expDate.setVisibility(View.GONE);
            } else {
                reason.setVisibility(View.GONE);
                btnAddComplaint.setVisibility(View.GONE);
                productName.setVisibility(View.GONE);
                lotNumber.setVisibility(View.GONE);
                mfgDate.setVisibility(View.GONE);
                expDate.setVisibility(View.GONE);
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
            toolbar.setTitle(getString(R.string.edit_complaint));
        } else {
            toolbar.setTitle(getString(R.string.add_complaint));
        }
    }
}