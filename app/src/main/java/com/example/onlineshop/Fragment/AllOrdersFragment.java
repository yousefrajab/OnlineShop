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
import com.example.onlineshop.Adapter.OrderClickListener;
import com.example.onlineshop.Domain.OrderModel;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.FragmentAllOrdersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllOrdersFragment extends Fragment implements OrderClickListener {

    private FragmentAllOrdersBinding binding;
    private OrderAdapter adapter;
    private DatabaseReference ordersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        initRecyclerView();
        loadAllOrders();
        setupBackButton();
    }

    private void initRecyclerView() {
        binding.allOrdersView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(new ArrayList<>(), this);
        binding.allOrdersView.setAdapter(adapter);
    }

    private void loadAllOrders() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<OrderModel> allOrders = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        allOrders.add(orderSnapshot.getValue(OrderModel.class));
                    }
                }

                // فرز القائمة لعرض الطلبات الأحدث أولاً
                allOrders.sort(Comparator.comparingLong(OrderModel::getDate).reversed());

                binding.emptyTxt.setVisibility(allOrders.isEmpty() ? View.VISIBLE : View.GONE);
                binding.allOrdersView.setVisibility(allOrders.isEmpty() ? View.GONE : View.VISIBLE);

                adapter = new OrderAdapter(allOrders, AllOrdersFragment.this);
                binding.allOrdersView.setAdapter(adapter);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onOrderClick(OrderModel order) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, OrderDetailFragment.newInstance(order))
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void setupBackButton() {
        binding.backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }
}