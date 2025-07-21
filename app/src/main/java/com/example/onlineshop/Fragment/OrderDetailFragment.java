package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlineshop.Adapter.CartAdapter;
import com.example.onlineshop.Domain.OrderModel;
import com.example.onlineshop.databinding.FragmentOrderDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailFragment extends Fragment {

    private FragmentOrderDetailBinding binding;
    private OrderModel order;

    // دالة Factory لتمرير بيانات الطلب إلى الـ Fragment بطريقة آمنة
    public static OrderDetailFragment newInstance(OrderModel order) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("order_details", order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // استلام البيانات من الـ Bundle
        if (getArguments() != null) {
            order = getArguments().getParcelable("order_details");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (order != null) {
            // عرض تفاصيل الطلب الرئيسية
            displayOrderSummary();
            // إعداد وعرض المنتجات في RecyclerView
            setupItemsRecyclerView();
        }

        // إعداد زر الرجوع
        setupBackButton();
    }

    private void displayOrderSummary() {
        binding.detailOrderIdTxt.setText("Order ID: #" + order.getOrderId().substring(0, 8));
        binding.detailOrderTotalTxt.setText("Total Paid: $" + String.format("%.2f", order.getTotalAmount()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String dateString = formatter.format(new Date(order.getDate()));
        binding.detailOrderDateTxt.setText("Date: " + dateString);
    }

    private void setupItemsRecyclerView() {
        // سنعيد استخدام CartAdapter لأنه يعرض المنتجات بشكل جيد
        // (عنوان، سعر، صورة، وكمية)
        binding.itemsInOrderView.setLayoutManager(new LinearLayoutManager(getContext()));
        CartAdapter adapter = new CartAdapter(order.getItems(), getContext());
        binding.itemsInOrderView.setAdapter(adapter);
    }

    private void setupBackButton() {
        binding.backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}