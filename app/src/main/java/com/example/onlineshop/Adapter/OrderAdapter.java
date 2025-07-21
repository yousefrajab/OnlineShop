package com.example.onlineshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlineshop.Domain.OrderModel;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ViewholderOrderBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final ArrayList<OrderModel> items;
    private final OrderClickListener listener;
    private Context context;

    public OrderAdapter(ArrayList<OrderModel> items, OrderClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        ViewholderOrderBinding binding = ViewholderOrderBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = items.get(position);

        if (order.getUserName() != null) {
            holder.binding.userNameTxt.setText(order.getUserName());
        } else {
            holder.binding.userNameTxt.setText("N/A");
        }

        Glide.with(context)
                .load(order.getUserProfileImageUrl())
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .circleCrop()
                .into(holder.binding.userImageView);

        if (order.getOrderId() != null) {
            holder.binding.orderIdTxt.setText("Order ID: #" + order.getOrderId().substring(Math.max(0, order.getOrderId().length() - 8)));
        }

        holder.binding.orderTotalTxt.setText("Total: $" + String.format("%.2f", order.getTotalAmount()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String dateString = formatter.format(new Date(order.getDate()));
        holder.binding.orderDateTxt.setText("Date: " + dateString);

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