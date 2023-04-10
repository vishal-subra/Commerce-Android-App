package com.example.beautystuffsss.ui.universalActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.MessageListAdapter;
import com.example.beautystuffsss.model.Message;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ChatView extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Message> messages;
    MessageListAdapter adapter;
    Toolbar toolbar;
    EditText newMessage;
    private static final String TAG = "ChatView";
    ImageButton sendButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference messagesReference;
    String recipientId, recipientName, myId, myName, messagesLocation;
    Preferences preferences;
    ProgressDialog progressDialog;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        recipientId = getIntent().getStringExtra(Constants.currentUserId);
        recipientName = getIntent().getStringExtra(Constants.currentUserName);
        recyclerView = findViewById(R.id.customer_chatView_recyclerView);
        toolbar = findViewById(R.id.chat_view_toolbar);
        newMessage = findViewById(R.id.sendMessageEditText);
        sendButton = findViewById(R.id.sendMessage_btn);
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        preferences = new Preferences(this);
        myId = preferences.getString(Constants.currentUserId);
        myName = preferences.getString(Constants.currentUserName);
        messagesLocation = (recipientId + myId);
        messagesReference = firebaseDatabase.getReference().child(Constants.chats);
        setSupportActionBar(toolbar);
        toolbarSetter(Objects.requireNonNull(getSupportActionBar()));
        messages = new ArrayList<>();
        adapter = new MessageListAdapter(this, messages);
        progressDialog.show("Loading your messages");
        messagesReference.get().addOnCompleteListener(chatsTask -> {
            if (chatsTask.isSuccessful()) {
                for (DataSnapshot chat : Objects.requireNonNull(chatsTask.getResult()).getChildren()) {
                    String chatKey = chat.getKey();
                    assert chatKey != null;
                    if (chatKey.contains(myId) && chatKey.contains(recipientId)) {
                        messagesLocation = chatKey;
                        break;
                    } else {
                        messagesLocation = (recipientId + myId);
                    }

                }
                messagesReference.child(messagesLocation).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot message:snapshot.getChildren()){
                            if (Objects.requireNonNull(message.child(Constants.messageSenderId).getValue()).toString().equals(myId)) {
                                // I'm the sender
                                messages.add(new Message(
                                        1,
                                        Objects.requireNonNull(message.child(Constants.messageReceiverId).getValue()).toString(),
                                        Objects.requireNonNull(message.child(Constants.messageSenderId).getValue()).toString(),
                                        myName,
                                        Objects.requireNonNull(message.child(Constants.messageText).getValue()).toString(),
                                        Objects.requireNonNull(message.child(Constants.messageTime).getValue()).toString(),
                                        Objects.requireNonNull(message.child(Constants.messageDate).getValue()).toString()
                                ));
                            } else {
                                // the other user is sender
                                messages.add(new Message(
                                        2,
                                        Objects.requireNonNull(message.child(Constants.messageSenderId).getValue()).toString(),
                                        Objects.requireNonNull(message.child(Constants.messageReceiverId).getValue()).toString(),
                                        recipientName,
                                        Objects.requireNonNull(message.child(Constants.messageText).getValue()).toString(),
                                        Objects.requireNonNull(message.child(Constants.messageTime).getValue()).toString(),
                                        Objects.requireNonNull(message.child(Constants.messageDate).getValue()).toString()
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messages.size()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: "+error.getMessage());
                    }
                });
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Unable to load chats", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.scrollToPosition(messages.size() - 1);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        sendButton.setOnClickListener(v -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date now = new Date();
            String time = timeFormat.format(now);
            String date = dateFormat.format(now);
            if (newMessage.getText() != null && !newMessage.getText().toString().equals("")) {
                String messageText = newMessage.getText().toString();
                Map<String, Object> message = new HashMap<>();
                message.put(Constants.messageText, messageText);
                message.put(Constants.messageTime, time);
                message.put(Constants.messageDate, date);
                message.put(Constants.messageSenderId, myId);
                message.put(Constants.messageReceiverId, recipientId);
                messagesReference.child(messagesLocation).push().setValue(message).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        newMessage.setText("");
                        recyclerView.scrollToPosition(messages.size() - 1);
                    } else {
                        Toast.makeText(this, "Unable to send message", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void toolbarSetter(ActionBar actionBar) {
        actionBar.setTitle(recipientName);
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