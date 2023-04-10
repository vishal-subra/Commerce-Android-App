package com.example.beautystuffsss.ui.customer.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.beautystuffsss.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerHome extends AppCompatActivity {
    BottomNavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        navigationView = findViewById(R.id.customer_bottom_nav);
        toolbar = findViewById(R.id.customer_home_toolbar);
        setSupportActionBar(toolbar);
        toolbarSetter(getSupportActionBar());
        setUpNavigation();
    }

    private void toolbarSetter(ActionBar supportActionBar) {
        supportActionBar.setDisplayShowTitleEnabled(false);
    }

    public void setUpNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.customer_host_fragment);
        NavigationUI.setupWithNavController(navigationView,
                navHostFragment.getNavController());
    }
}