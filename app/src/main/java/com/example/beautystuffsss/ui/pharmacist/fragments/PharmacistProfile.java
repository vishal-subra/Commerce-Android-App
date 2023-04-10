package com.example.beautystuffsss.ui.pharmacist.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.ui.universalActivities.Complaints;
import com.example.beautystuffsss.ui.universalActivities.ContactUs;
import com.example.beautystuffsss.ui.universalActivities.Intro;
import com.example.beautystuffsss.ui.universalActivities.MyProfile;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ImageHelper;
import com.example.beautystuffsss.util.Preferences;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class PharmacistProfile extends Fragment {

    CardView userProfileCard, complaints, contactUsCard;
    CircleImageView profilePhoto;
    TextView profileName;
    Preferences preferences;
    FirebaseAuth mAuth;
    ImageHelper imageHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pharmacist_profile, container, false);
        userProfileCard = view.findViewById(R.id.pharmacist_profile_cardProfile);
        complaints = view.findViewById(R.id.pharmacist_profile_cardComplaints);
        contactUsCard = view.findViewById(R.id.pharmacist_profile_cardContactUs);
        profilePhoto = view.findViewById(R.id.pharmacist_profile_photo);
        profileName = view.findViewById(R.id.pharmacist_profile_name);
        preferences = new Preferences(requireActivity());
        imageHelper = new ImageHelper(requireActivity());
        mAuth = FirebaseAuth.getInstance();
        userProfileCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyProfile.class)));
        contactUsCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), ContactUs.class)));
        complaints.setOnClickListener(v -> startActivity(new Intent(getActivity(), Complaints.class)));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_logout) {
            try {
                mAuth.signOut();
            } finally {
                Toast.makeText(requireActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(requireActivity(), Intro.class));
            getActivity().finish();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        profileName.setText(preferences.getString(Constants.currentUserName));
        imageHelper.loadImage(preferences.getString(Constants.currentUserPhotoUrl),profilePhoto);
    }
}