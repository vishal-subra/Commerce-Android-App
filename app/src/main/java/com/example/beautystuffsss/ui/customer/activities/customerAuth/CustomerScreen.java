package com.example.beautystuffsss.ui.customer.activities.customerAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystuffsss.R;

public class CustomerScreen extends AppCompatActivity {
    Button register, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_screen);
        register = findViewById(R.id.customer_screen_registerButton);
        login = findViewById(R.id.customer_screen_loginButton);
        login.setOnClickListener(v -> {
            startActivity(new Intent(CustomerScreen.this, CustomerLogin.class));
        });
        register.setOnClickListener(v -> startActivity(new Intent(CustomerScreen.this, CustomerRegister.class)));
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