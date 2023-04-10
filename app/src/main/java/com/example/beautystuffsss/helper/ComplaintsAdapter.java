package com.example.beautystuffsss.helper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.model.Complaint;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;

import java.util.ArrayList;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ComplaintsViewHolder> {
    Context context;
    ArrayList<Complaint> complaints;
    OnComplaintTapped onComplaintTapped;
    OnComplaintStatusTapped onComplaintStatusTapped;
    Preferences preferences;

    public ComplaintsAdapter(Context context, ArrayList<Complaint> complaints) {
        this.context = context;
        this.complaints = complaints;
        preferences = new Preferences(context);
    }

    public interface OnComplaintTapped {
        void onTap(int position);
    }

    public interface OnComplaintStatusTapped {
        void onTap(int position);
    }

    public void setOnComplaintTappedListener(OnComplaintTapped onComplaintTapped) {
        this.onComplaintTapped = onComplaintTapped;
    }

    public void setOnComplaintStatusTappedListener(OnComplaintStatusTapped onComplaintTapped) {
        this.onComplaintStatusTapped = onComplaintTapped;
    }

    @NonNull
    @Override
    public ComplaintsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_list_layout, parent, false);

        return new ComplaintsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintsViewHolder holder, int position) {
        if (position == complaints.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        }
        if (complaints.get(position).getComplaintStatus().equals(Constants.complaintStatusPending)){
            holder.complaintStatus.setTextColor(Color.parseColor("#FFD54F"));
        }else if (complaints.get(position).getComplaintStatus().equals(Constants.complaintStatusResolved)){
            holder.complaintStatus.setTextColor(Color.parseColor("#4CAF50"));
        }else {
            holder.complaintStatus.setTextColor(Color.parseColor("#C62828"));
        }
        holder.complaintId.setText(complaints.get(position).getComplaintId());
        holder.complaintFor.setText(complaints.get(position).getComplaintFor());
        holder.complaintStatus.setText(complaints.get(position).getComplaintStatus());
        holder.itemView.setOnClickListener(v -> onComplaintTapped.onTap(position));
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
            holder.complaintStatus.setOnClickListener(v -> onComplaintStatusTapped.onTap(position));
        }
    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }

    public static class ComplaintsViewHolder extends RecyclerView.ViewHolder {
        TextView complaintId, complaintFor, complaintStatus;
        View divider;

        public ComplaintsViewHolder(View itemView) {
            super(itemView);
            complaintId = itemView.findViewById(R.id.complaint_list_complaint_id);
            complaintFor = itemView.findViewById(R.id.complaint_list_complaint_type);
            complaintStatus = itemView.findViewById(R.id.complaint_list_complaint_status);
            divider = itemView.findViewById(R.id.complaint_list_divider);
        }

        ;
    }
}
