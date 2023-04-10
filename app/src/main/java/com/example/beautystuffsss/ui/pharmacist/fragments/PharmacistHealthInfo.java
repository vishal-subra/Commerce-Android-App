package com.example.beautystuffsss.ui.pharmacist.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.helper.HealthInfoAdapter;
import com.example.beautystuffsss.model.HealthInfo;
import com.example.beautystuffsss.ui.universalActivities.EditHealthInfo;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.ProgressDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PharmacistHealthInfo extends Fragment {
    ViewPager2 pager;
    HealthInfoAdapter adapter;
    ArrayList<HealthInfo> healthInfoArrayList;
    FloatingActionButton fab;
    DatabaseReference healthInfoRef;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pharmacist_health_info, container, false);
        pager = view.findViewById(R.id.pharmacist_health_info_pager);
        fab = view.findViewById(R.id.pharmacist_health_info_addInfoFab);
        healthInfoRef = FirebaseDatabase.getInstance().getReference().child(Constants.healthInfo);
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.images).child(Constants.healthInfo);
        healthInfoArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(requireActivity());
        adapter = new HealthInfoAdapter(getActivity(), healthInfoArrayList);
        pager.setAdapter(adapter);
        adapter.setOnYoutubeTappedListener(position -> {
            String videoId;
            if (healthInfoArrayList.get(position).getInfoYoutubeLink().contains("=")){
                videoId = healthInfoArrayList.get(position).getInfoYoutubeLink().substring(healthInfoArrayList.get(position).getInfoYoutubeLink().lastIndexOf("=") + 1);
            }else{
                videoId = healthInfoArrayList.get(position).getInfoYoutubeLink().substring(healthInfoArrayList.get(position).getInfoYoutubeLink().lastIndexOf("/") + 1);
            }
            Toast.makeText(requireActivity(), videoId, Toast.LENGTH_SHORT).show();
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=" + videoId));
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);
            }
        });
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditHealthInfo.class);
            intent.putExtra(Constants.intentAction, Constants.intentActionAdd);
            startActivity(intent);
        });
        adapter.setOnHealthInfoTappedListener(position -> {
            Intent intent = new Intent(getActivity(), EditHealthInfo.class);
            intent.putExtra(Constants.intentAction, Constants.intentActionEdit);
            intent.putExtra(Constants.healthInfoId, healthInfoArrayList.get(position).getInfoId());
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show("Loading health infos");
        healthInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                healthInfoArrayList.clear();
                for (DataSnapshot healthInfo:snapshot.getChildren()){
                    healthInfoArrayList.add(new HealthInfo(
                            healthInfo.getKey(),
                            healthInfo.child(Constants.healthInfoTitle).getValue().toString(),
                            healthInfo.child(Constants.healthInfoDescription).getValue().toString(),
                            healthInfo.child(Constants.healthYoutubeUrl).getValue().toString(),
                            healthInfo.child(Constants.healthInfoPhotoUrl).getValue().toString()
                    ));
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}