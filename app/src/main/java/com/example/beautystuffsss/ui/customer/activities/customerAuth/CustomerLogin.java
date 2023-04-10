package com.example.beautystuffsss.ui.customer.activities.customerAuth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.ui.customer.activities.CustomerHome;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomerLogin extends AppCompatActivity {
    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView registerTextView;
    ProgressDialog progressDialog;
    Preferences preferences;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private static final String TAG = "CustomerLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        emailEditText = findViewById(R.id.customer_login_editText_email);
        passwordEditText = findViewById(R.id.customer_login_editText_password);
        registerTextView = findViewById(R.id.customer_login_textView_register_here);
        loginButton = findViewById(R.id.customer_login__login_button);
        progressDialog = new ProgressDialog(this);
        preferences = new Preferences(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        registerTextView.setOnClickListener(v -> startActivity(new Intent(CustomerLogin.this, CustomerRegister.class)));
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Invalid Email");
                emailEditText.setFocusable(true);
            }
            if (password.isEmpty() || password == null) {
                passwordEditText.setError("Can't login without a password");
                passwordEditText.setFocusable(true);
            } else {
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        progressDialog.show("We are logging you in.....");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uId = task.getResult().getUser().getUid();
                        firestore.collection(Constants.users).document(uId).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                progressDialog.dismiss();
                                DocumentSnapshot result = task1.getResult();
                                if (result.getString(Constants.currentUserType).equals(Constants.uTypeCustomer)) {
                                    try {
                                        saveDataToPreferences(
                                                result.getString(Constants.currentUserEmail),
                                                uId,
                                                result.getString(Constants.currentUserName),
                                                result.getString(Constants.currentUserPhone),
                                                result.getString(Constants.currentUserAddress),
                                                result.getString(Constants.currentUserType),
                                                result.getString(Constants.currentUserPhotoUrl)
                                        );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(this, CustomerHome.class));
                                    }
                                } else {
                                    emailEditText.requestFocus();
                                    emailEditText.setError("Email not registered as customer");
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Problem in signing you in", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            passwordEditText.requestFocus();
                            passwordEditText.setError("Wrong password");
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            emailEditText.requestFocus();
                            emailEditText.setError("Email doesn't exist");
                        }
                    }
                });

    }

    private void saveDataToPreferences(
            final String email,
            final String userId,
            final String fullName,
            final String phone,
            final String address,
            final String userType,
            final String photoUrl) {
        preferences.saveString(Constants.currentUserEmail, email);
        preferences.saveString(Constants.currentUserId, userId);
        preferences.saveString(Constants.currentUserName, fullName);
        preferences.saveString(Constants.currentUserPhone, phone);
        preferences.saveString(Constants.currentUserAddress, address);
        preferences.saveString(Constants.currentUserType, userType);
        preferences.saveString(Constants.currentUserPhotoUrl, photoUrl);
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