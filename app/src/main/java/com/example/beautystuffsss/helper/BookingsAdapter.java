package com.example.beautystuffsss.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.model.Booking;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;

import java.util.ArrayList;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.BookingsViewHolder> {
    ArrayList<Booking> bookingArrayList;
    Context context;
    OnVisitedTapped visitedTappedListener;
    OnStatusTapped onStatusTappedListener;
    OnLinkTapped onLinkTappedListener;
    Preferences preferences;

    public BookingsAdapter(ArrayList<Booking> bookingArrayList, Context context) {
        this.bookingArrayList = bookingArrayList;
        this.context = context;
        preferences = new Preferences(context);
    }

    public interface OnVisitedTapped {
        void onTapped(int pos);
    }

    public interface OnStatusTapped {
        void onTapped(int pos);
    }

    public interface OnLinkTapped {
        void onTap(int pos);
    }

    public void setOnVisitedTappedListener(OnVisitedTapped listener) {
        visitedTappedListener = listener;
    }

    public void setOnStatusTappedListener(OnStatusTapped onStatusTappedListener) {
        this.onStatusTappedListener = onStatusTappedListener;
    }

    public void setOnLinkTappedListener(OnLinkTapped onLinkTappedListener) {
        this.onLinkTappedListener = onLinkTappedListener;
    }

    @NonNull
    @Override
    public BookingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_list_layout, parent, false);

        return new BookingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingsViewHolder holder, int position) {
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
            holder.setVisited.setVisibility(View.VISIBLE);
            holder.setVisited.setOnClickListener(v -> visitedTappedListener.onTapped(position));
            holder.bookingStatus.setOnClickListener(v -> onStatusTappedListener.onTapped(position));
        }
        if (position == bookingArrayList.size() - 1) {
            holder.divider.setVisibility(View.INVISIBLE);
        }
        holder.bookingTitle.setText(bookingArrayList.get(position).getBookingTitle());
        holder.bookingDate.setText(bookingArrayList.get(position).getBookingDate());
        holder.bookingStatus.setText(bookingArrayList.get(position).getBookingStatus());
        holder.linkToCalendar.setOnClickListener(v -> {
            onLinkTappedListener.onTap(position);
        });
    }

    @Override
    public int getItemCount() {
        return bookingArrayList.size();
    }

    public static class BookingsViewHolder extends RecyclerView.ViewHolder {
        TextView bookingTitle, bookingDate, bookingStatus;
        View divider;
        Button setVisited, linkToCalendar;

        public BookingsViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingTitle = itemView.findViewById(R.id.booking_list_title);
            bookingDate = itemView.findViewById(R.id.booking_list_date);
            bookingStatus = itemView.findViewById(R.id.booking_list_status);
            divider = itemView.findViewById(R.id.booking_list_divider);
            setVisited = itemView.findViewById(R.id.booking_list_setVisited);
            linkToCalendar = itemView.findViewById(R.id.booking_list_linkToGoogleCalendar);
        }
    }
}
