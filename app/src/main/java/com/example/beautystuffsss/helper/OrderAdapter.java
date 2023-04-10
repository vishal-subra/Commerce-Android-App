package com.example.beautystuffsss.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystuffsss.R;
import com.example.beautystuffsss.model.Order;
import com.example.beautystuffsss.util.Constants;
import com.example.beautystuffsss.util.Preferences;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrdersViewHolder> {
    Context context;
    ArrayList<Order> orders;
    Preferences preferences;
    OnChatButtonTapped chatButtonTappedListener;
    OnOrderStatusTapped orderStatusTappedListener;
    OnAddReminderTapped onAddReminderTappedListener;
    OnOrderTapped onOrderTapped;

    public OrderAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
        preferences = new Preferences(context);
    }

    public interface OnChatButtonTapped {
        void onTapped(int position);
    }

    public interface OnAddReminderTapped {
        void onTapped(int position);
    }

    public interface OnOrderStatusTapped {
        void onTapped(int position);
    }

    public interface OnOrderTapped {
        void onTap(int position);
    }

    public void setOnChatButtonTappedListener(OnChatButtonTapped chatButtonTappedListener) {
        this.chatButtonTappedListener = chatButtonTappedListener;
    }

    public void setOnOrderStatusTappedListener(OnOrderStatusTapped onOrderStatusTappedListener) {
        this.orderStatusTappedListener = onOrderStatusTappedListener;
    }

    public void setOnAddReminderTappedListener(OnAddReminderTapped onAddReminderTappedListener) {
        this.onAddReminderTappedListener = onAddReminderTappedListener;
    }

    public void setOnOrderTappedListener(OnOrderTapped onOrderTapped) {
        this.onOrderTapped = onOrderTapped;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_list_layout, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        holder.orderId.setText(orders.get(position).getOrderId());
        holder.orderFrom.setText(orders.get(position).getOrderFrom());
        holder.placedOn.setText(orders.get(position).getPlacedOn());
        holder.orderStatus.setText(orders.get(position).getOrderStatus());
        if (preferences.getString(Constants.currentUserType).equals(Constants.uTypePharmacist)) {
            if (!orders.get(position).getOrderStatus().equals("Completed")) {
                holder.chatButton.setVisibility(View.VISIBLE);
                holder.chatButton.setOnClickListener(v -> chatButtonTappedListener.onTapped(position));
                holder.orderStatus.setOnClickListener(v -> orderStatusTappedListener.onTapped(position));
            }
        }
        holder.addReminder.setOnClickListener(v -> onAddReminderTappedListener.onTapped(position));
        holder.itemView.setOnClickListener(v -> onOrderTapped.onTap(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderFrom, placedOn, orderStatus;
        LinearLayout chatButton;
        Button addReminder;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_orderId);
            placedOn = itemView.findViewById(R.id.order_placedOn);
            orderFrom = itemView.findViewById(R.id.order_customerName);
            orderStatus = itemView.findViewById(R.id.order_orderStatus);
            chatButton = itemView.findViewById(R.id.order_chat_btn);
            addReminder = itemView.findViewById(R.id.order_btnAddReminder);
        }

        ;
    }
}
