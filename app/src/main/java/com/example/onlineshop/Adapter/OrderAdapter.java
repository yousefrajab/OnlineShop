package com.example.onlineshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlineshop.Domain.OrderModel;
import com.example.onlineshop.databinding.ViewholderOrderBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final ArrayList<OrderModel> items;
    private final OrderClickListener listener; // <-- المتغير الجديد

    // --- تعديل الـ Constructor ---
    public OrderAdapter(ArrayList<OrderModel> items, OrderClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderOrderBinding binding = ViewholderOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = items.get(position);

        holder.binding.orderIdTxt.setText("Order ID: #" + order.getOrderId().substring(0, 8));
        holder.binding.orderTotalTxt.setText("Total: $" + String.format("%.2f", order.getTotalAmount()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String dateString = formatter.format(new Date(order.getDate()));
        holder.binding.orderDateTxt.setText("Date: " + dateString);

        // --- إضافة OnClickListener ---
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderBinding binding;
        public ViewHolder(ViewholderOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}