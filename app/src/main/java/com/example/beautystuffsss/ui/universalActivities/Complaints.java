package com.example.beautystuffsss.ui.universalActivities;

import android.app.AlertDialog;
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
import com.example.beautystuffsss.helper.ComplaintsAdapter;
import com.example.beautystuffsss.model.Complaint;
import com.example.beautystuffsss.ui.customer.activities.AddComplaint;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Complaints extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    FloatingActionButton addComplaintFab;
    Preferences preferences;
    ComplaintsAdapter adapter;
    ArrayList<Complaint> complaints;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    private static final String TAG = "Complaints";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);
        toolbar = findViewById(R.id.complaints_toolbar);
        recyclerView = findViewById(R.id.complaints_recyclerView);
        addComplaintFab = findViewById(R.id.complaints_addComplaint);
        progressDialog = new ProgressDialog(this);
        preferences = new Preferences(this);
        complaints = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);
        toolbarSetter(getSupportActionBar());
        progressDialog.show("Loading complaints");
        database.getReference().child(Constants.complaints).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                complaints.clear();
                for (DataSnapshot complaintType : snapshot.getChildren()) {
                    for (DataSnapshot complaint : complaintType.getChildren()) {
                        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypeCustomer)) {
                            if (complaint.child(Constants.complainerId).getValue().toString().equals(preferences.getString(Constants.currentUserId))) {
                                complaints.add(new Complaint(
                                        complaint.child(Constants.complaintId).getValue().toString(),
                                        complaint.child(Constants.complaintType).getValue().toString(),
                                        complaint.child(Constants.complaintStatus).getValue().toString(),
                                        complaint.child(Constants.complainerId).getValue().toString()
                                ));
                            }
                        } else {
                            complaints.add(new Complaint(
                                    complaint.child(Constants.complaintId).getValue().toString(),
                                    complaint.child(Constants.complaintType).getValue().toString(),
                                    complaint.child(Constants.complaintStatus).getValue().toString(),
                                    complaint.child(Constants.complainerId).getValue().toString()
                            ));
                        }
                    }
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new ComplaintsAdapter(this, complaints);
        recyclerView.setAdapter(adapter);
        adapter.setOnComplaintTappedListener(position -> {
            Intent intent = new Intent(this, ComplaintView.class);
            ;
            intent.putExtra(Constants.complaintId, complaints.get(position).getComplaintId());
            intent.putExtra(Constants.complaintType, complaints.get(position).getComplaintFor());
            startActivity(intent);
        });
        adapter.setOnComplaintStatusTappedListener(position -> {
            if (complaints.get(position).getComplaintStatus().equals(Constants.complaintStatusPending)){
                CharSequence[] items = new CharSequence[]{Constants.complaintStatusResolved,Constants.complaintStatusDropped};
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Set complaint status");
                adb.setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());
                adb.setSingleChoiceItems(items, -1, (dialog, which) -> {
                    if (which==0){
                        database.getReference().child(Constants.complaints)
                                .child(complaints.get(position)
                                        .getComplaintFor()).child(complaints.get(position)
                                .getComplaintId())
                                .child(Constants.complaintStatus).setValue(Constants.complaintStatusResolved);
                    }else {
                        database.getReference().child(Constants.complaints)
                                .child(complaints.get(position)
                                        .getComplaintFor()).child(complaints.get(position)
                                .getComplaintId())
                                .child(Constants.complaintStatus).setValue(Constants.complaintStatusDropped);
                    }
                    dialog.dismiss();
                });
                adb.create().show();
            }else{
                Toast.makeText(this, "Cannot change complaint status twice", Toast.LENGTH_SHORT).show();
            }
        });
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypeCustomer)) {
            addComplaintFab.show();
            addComplaintFab.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddComplaint.class);
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

    private void toolbarSetter(ActionBar toolbar) {
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);
        toolbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp);
        toolbar.setTitle(getString(R.string.complaints));
    }
}