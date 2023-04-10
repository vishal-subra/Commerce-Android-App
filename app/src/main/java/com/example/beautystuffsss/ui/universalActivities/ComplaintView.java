package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.ui.customer.activities.AddComplaint;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ComplaintView extends AppCompatActivity {

    Toolbar toolbar;
    String complaintId, complaintType;
    TextView name, lotNo, mfgDate, expDate, reason;
    LinearLayout nameLayout, lotNoLayout, mfgLayout, expLayout, reasonLayout, btnLayout;
    Preferences preferences;
    Button editComplaint, deleteComplaint;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_view);
        toolbar = findViewById(R.id.complaint_view_toolbar);
        name = findViewById(R.id.complaint_view_product_name_tv);
        lotNo = findViewById(R.id.complaint_view_product_lot_no_tv);
        mfgDate = findViewById(R.id.complaint_view_product_mfg_date_tv);
        expDate = findViewById(R.id.complaint_view_product_exp_date_tv);
        reason = findViewById(R.id.complaint_view_reason_tv);
        nameLayout = findViewById(R.id.complaint_view_product_name_layout);
        lotNoLayout = findViewById(R.id.complaint_view_product_lot_no_layout);
        mfgLayout = findViewById(R.id.complaint_view_product_mfg_date_layout);
        expLayout = findViewById(R.id.complaint_view_product_exp_date_layout);
        reasonLayout = findViewById(R.id.complaint_view_product_reason_layout);
        btnLayout = findViewById(R.id.complaint_view_btn_layout);
        editComplaint = findViewById(R.id.complaint_view_btnEdit);
        deleteComplaint = findViewById(R.id.complaint_view_btnDelete);
        preferences = new Preferences(this);
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        complaintType = getIntent().getStringExtra(Constants.complaintType);
        complaintId = getIntent().getStringExtra(Constants.complaintId);
        setSupportActionBar(toolbar);
        toolbarSetter(getSupportActionBar());
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypeCustomer)) {
            btnLayout.setVisibility(View.VISIBLE);
            deleteComplaint.setOnClickListener(v -> {
                firebaseDatabase.getReference().child(Constants.complaints).child(complaintType).child(complaintId).removeValue().addOnCompleteListener(task -> {
                   if (task.isSuccessful()){
                       Toast.makeText(this, "Complaint deleted", Toast.LENGTH_SHORT).show();
                       onBackPressed();
                   }else{
                       Toast.makeText(this, "Unable to delete Complaint", Toast.LENGTH_SHORT).show();
                   }
                });
            });
            editComplaint.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddComplaint.class);
                intent.putExtra(Constants.intentAction, Constants.intentActionEdit);
                intent.putExtra(Constants.complaintId, complaintId);
                intent.putExtra(Constants.complaintType, complaintType);
                startActivity(intent);
            });
        }
        progressDialog.show("Loading complaint details");
        firebaseDatabase.getReference().child(Constants.complaints).child(complaintType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot complaint : snapshot.getChildren()) {
                    if (complaint.child(Constants.complaintId).getValue().toString().equals(complaintId)) {
                        progressDialog.dismiss();
                        if (complaint.child(Constants.complaintType).getValue().toString().equals(Constants.complaintTypeProduct)) {
                            complaintType = Constants.complaintTypeProduct;
                            reason.setVisibility(View.VISIBLE);
                            name.setVisibility(View.VISIBLE);
                            lotNo.setVisibility(View.VISIBLE);
                            mfgDate.setVisibility(View.VISIBLE);
                            expDate.setVisibility(View.VISIBLE);
                            reason.setText(complaint.child(Constants.complaintReason).getValue().toString());
                            lotNo.setText(complaint.child(Constants.complaintProductLotNo).getValue().toString());
                            name.setText(complaint.child(Constants.complaintProductName).getValue().toString());
                            mfgDate.setText(complaint.child(Constants.complaintProductMfg).getValue().toString());
                            expDate.setText(complaint.child(Constants.complaintProductExp).getValue().toString());
                        } else {
                            complaintType = Constants.complaintTypeService;
                            reason.setVisibility(View.VISIBLE);
                            reason.setText(complaint.child(Constants.complaintReason).getValue().toString());

                            name.setVisibility(View.GONE);
                            lotNo.setVisibility(View.GONE);
                            mfgDate.setVisibility(View.GONE);
                            expDate.setVisibility(View.GONE);
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
        toolbar.setTitle(complaintType);
    }
}