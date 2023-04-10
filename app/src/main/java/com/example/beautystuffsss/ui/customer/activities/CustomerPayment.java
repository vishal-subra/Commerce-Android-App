package com.example.beautystuffsss.ui.customer.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.braintreepayments.cardform.view.CardForm;
import com.example.beautystuffsss.R;

public class CustomerPayment extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_payment);
        Button buy = findViewById(R.id.btnBuy);
        final CardForm cardForm = findViewById(R.id.card_form);
        toolbar = findViewById(R.id.customer_payment_toolbar);
        setSupportActionBar(toolbar);
        toolbarSetter(getSupportActionBar());
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase")
                .setup(this);
        buy.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK);
            finish();
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
        toolbar.setTitle(getString(R.string.enter_payment_details));
    }
}