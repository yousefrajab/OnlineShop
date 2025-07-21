package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onlineshop.R;
import com.example.onlineshop.databinding.FragmentAdminDashboardBinding;

import com.example.onlineshop.Fragment.AllOrdersFragment;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupButtons();
    }

    private void setupButtons() {
        binding.backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.manageProductsBtn.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ManageProductsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // ▼▼▼ هذا هو الجزء الذي تم تعديله ▼▼▼
        binding.viewOrdersBtn.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                // استبدال الـ Toast بالكود الفعلي لفتح AllOrdersFragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AllOrdersFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        // ▲▲▲ نهاية الجزء المعدل ▲▲▲
    }
}