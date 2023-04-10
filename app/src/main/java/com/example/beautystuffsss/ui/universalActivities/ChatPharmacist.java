package com.example.beautystuffsss.ui.universalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.ChatsAdapter;
import com.example.beautystuffsss.model.PharmacistChat;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ChatPharmacist extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<PharmacistChat> users;
    ChatsAdapter adapter;
    FirebaseDatabase database;
    FirebaseFirestore firestore;
    DatabaseReference chatsReference;
    ProgressDialog progressDialog;
    Preferences preferences;
    private static final String TAG = "ChatPharmacist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist_chat);
        toolbar = findViewById(R.id.chats_toolbar);
        recyclerView = findViewById(R.id.chats_recyclerView);
        users = new ArrayList<>();
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        adapter = new ChatsAdapter(users, this);
        progressDialog = new ProgressDialog(this);
        preferences = new Preferences(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        chatsReference = database.getReference().child(Constants.chats);
        progressDialog.show("Loading your chats");
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
            chatsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users.clear();
                    for (DataSnapshot chat : snapshot.getChildren()) {
                        long visitedMessage = 0;
                        long totalMessagesInChat = chat.getChildrenCount();
                        if (chat.getKey().contains(preferences.getString(Constants.currentUserId))) {
                            for (DataSnapshot message : chat.getChildren()) {
                                visitedMessage++;
                                if (visitedMessage == totalMessagesInChat) {
                                    //Last message
                                    Log.d(TAG, "onDataChange: " + message.getKey());

                                    if (Objects.requireNonNull(message.child(Constants.messageSenderId).getValue()).toString().equals(preferences.getString(Constants.currentUserId))) {
                                        // I am the sender
                                        firestore.collection(Constants.users).document(Objects.requireNonNull(message.child(Constants.messageReceiverId).getValue()).toString()).get().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                users.add(new PharmacistChat(
                                                        task1.getResult().getString(Constants.currentUserName),
                                                        task1.getResult().getString(Constants.currentUserPhotoUrl),
                                                        task1.getResult().getId(),
                                                        Objects.requireNonNull(message.child(Constants.messageText).getValue()).toString()
                                                ));
                                                progressDialog.dismiss();
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(ChatPharmacist.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        // I am the receiver
                                        firestore.collection(Constants.users).document(Objects.requireNonNull(message.child(Constants.messageSenderId).getValue()).toString()).get().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                users.add(new PharmacistChat(
                                                        task1.getResult().getString(Constants.currentUserName),
                                                        task1.getResult().getString(Constants.currentUserPhotoUrl),
                                                        task1.getResult().getId(),
                                                        Objects.requireNonNull(message.child(Constants.messageText).getValue()).toString()
                                                ));
                                                progressDialog.dismiss();
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(ChatPharmacist.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                    progressDialog.dismiss();
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            });
        } else {
            // Load for customer
            firestore.collection(Constants.users).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (DocumentSnapshot user:task.getResult().getDocuments()){
                        if (user.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)){
                            users.add(new PharmacistChat(
                                    user.getString(Constants.currentUserName),
                                    user.getString(Constants.currentUserPhotoUrl),
                                    user.getId(),
                                    user.getString(Constants.currentUserEmail)
                            ));
                            progressDialog.dismiss();
                            adapter.notifyDataSetChanged();

                        }
                    }
                }
            });
        }
        try {
            adapter.setOnUserTappedListener(position -> {
                Intent intent = new Intent(ChatPharmacist.this, ChatView.class);
                intent.putExtra(Constants.currentUserName, users.get(position).getName());
                intent.putExtra(Constants.currentUserId, users.get(position).getPharmacistId());
                startActivity(intent);
            });
        } catch (Exception e) {
            Log.d("TAF", e.getMessage());
        }
    }


    private void toolbarSetter(androidx.appcompat.app.ActionBar actionBar) {
        actionBar.setTitle("Chats");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp);
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
}