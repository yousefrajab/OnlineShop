package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.onlineshop.Adapter.PopularAdapter;
import com.example.onlineshop.Adapter.ProductAdminClickListener;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.R;
import com.example.onlineshop.ViewModel.MainViewModel;
import com.example.onlineshop.databinding.FragmentManageProductsBinding;

import java.util.ArrayList;

public class ManageProductsFragment extends Fragment implements ProductAdminClickListener {

    private FragmentManageProductsBinding binding;
    private MainViewModel viewModel;
    private PopularAdapter productsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ▼▼▼ هذا هو السطر الذي تم تصحيحه ▼▼▼
        binding = FragmentManageProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupRecyclerView();
        loadProducts();
        setupButtons();
    }

    private void setupRecyclerView() {
        binding.productsAdminView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productsAdapter = new PopularAdapter(new ArrayList<>(), this);
        binding.productsAdminView.setAdapter(productsAdapter);
    }

    private void loadProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getPopular().observe(getViewLifecycleOwner(), itemsModels -> {
            if (itemsModels != null) {
                productsAdapter.updateList(itemsModels);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    private void setupButtons() {
        binding.backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.addProductBtn.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddEditProductFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onProductClick(ItemsModel item) {
        if (getParentFragmentManager() != null) {
            AddEditProductFragment editFragment = AddEditProductFragment.newInstance(item);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}