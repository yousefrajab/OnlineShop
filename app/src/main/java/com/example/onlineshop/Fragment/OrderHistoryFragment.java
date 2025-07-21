package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlineshop.Adapter.OrderAdapter;
import com.example.onlineshop.Adapter.OrderClickListener; // <-- استيراد الواجهة
import com.example.onlineshop.Domain.OrderModel;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.FragmentOrderHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

// ▼▼▼ تطبيق الواجهة الجديدة ▼▼▼
public class OrderHistoryFragment extends Fragment implements OrderClickListener {

    private FragmentOrderHistoryBinding binding;
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);
        }

        initRecyclerView();
        loadOrders();
        setupBackButton();
    }

    private void initRecyclerView() {
        binding.ordersView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadOrders() {
        binding.progressBarOrders.setVisibility(View.VISIBLE);
        if (ordersRef != null) {
            ordersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<OrderModel> orderList = new ArrayList<>();
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        orderList.add(orderSnapshot.getValue(OrderModel.class));
                    }
                    Collections.reverse(orderList);

                    binding.emptyTxt.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.ordersView.setVisibility(orderList.isEmpty() ? View.GONE : View.VISIBLE);

                    // تمرير "this" كـ listener
                    OrderAdapter adapter = new OrderAdapter(orderList, OrderHistoryFragment.this);
                    binding.ordersView.setAdapter(adapter);
                    binding.progressBarOrders.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.progressBarOrders.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setupBackButton() {
        binding.backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    // ▼▼▼ هذه هي الدالة التي سيتم استدعاؤها من الـ Adapter ▼▼▼
    @Override
    public void onOrderClick(OrderModel order) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, OrderDetailFragment.newInstance(order))
                    .addToBackStack(null)
                    .commit();
        }
    }
}