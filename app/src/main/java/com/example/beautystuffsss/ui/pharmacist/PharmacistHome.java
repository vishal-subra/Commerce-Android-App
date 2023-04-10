package com.example.beautystuffsss.ui.pharmacist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.beautystuffsss.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PharmacistHome extends AppCompatActivity {
    BottomNavigationView navigationView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist_home);
        navigationView = findViewById(R.id.pharmacist_bottom_nav);
        toolbar = findViewById(R.id.pharmacist_home_toolbar);
        setSupportActionBar(toolbar);
        toolbarSetter(getSupportActionBar());
        setUpNavigation();
    }

    private void toolbarSetter(ActionBar supportActionBar) {
        supportActionBar.setDisplayShowTitleEnabled(false);
    }

    public void setUpNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.pharmacist_host_fragment);
        NavigationUI.setupWithNavController(navigationView,
                navHostFragment.getNavController());
    }
}