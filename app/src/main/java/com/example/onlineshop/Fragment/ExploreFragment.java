package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlineshop.Adapter.CategoryAdapter;
import com.example.onlineshop.Adapter.CategoryClickListener;
import com.example.onlineshop.Adapter.PopularAdapter;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.ViewModel.MainViewModel;
import com.example.onlineshop.databinding.FragmentExploreBinding;

import java.util.ArrayList;

public class ExploreFragment extends Fragment implements CategoryClickListener {

    private FragmentExploreBinding binding;
    private MainViewModel viewModel;
    private PopularAdapter productsAdapter;
    private final ArrayList<ItemsModel> allProducts = new ArrayList<>();
    private int selectedCategoryId = 0; // لتتبع الفئة المختارة

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupRecyclerViews();
        setupSearch(); // <-- إضافة دالة البحث
        loadData();
    }

    private void setupRecyclerViews() {
        binding.categoryView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.productsView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productsAdapter = new PopularAdapter(new ArrayList<>());
        binding.productsView.setAdapter(productsAdapter);
    }

    // --- دالة البحث الجديدة ---
    private void setupSearch() {
        binding.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        binding.progressBarExplore.setVisibility(View.VISIBLE);

        viewModel.getCategories().observe(getViewLifecycleOwner(), categoryModels -> {
            if (categoryModels != null && !categoryModels.isEmpty()) {
                binding.categoryView.setAdapter(new CategoryAdapter(categoryModels, this));
            }
        });

        viewModel.getPopular().observe(getViewLifecycleOwner(), itemsModels -> {
            if (itemsModels != null && !itemsModels.isEmpty()) {
                allProducts.clear();
                allProducts.addAll(itemsModels);
                filterProducts(""); // عرض كل المنتجات في البداية
            }
            binding.progressBarExplore.setVisibility(View.GONE);
        });
    }

    @Override
    public void onCategoryClick(int categoryId) {
        selectedCategoryId = categoryId;
        binding.searchEdt.setText(""); // إفراغ البحث عند تغيير الفئة
        filterProducts("");
    }

    // --- دالة التصفية الموحدة الجديدة ---
    private void filterProducts(String searchText) {
        // 1. التصفية حسب الفئة
        ArrayList<ItemsModel> filteredByCategory = new ArrayList<>();
        if (selectedCategoryId == 0) {
            filteredByCategory.addAll(allProducts);
        } else {
            for (ItemsModel item : allProducts) {
                if (item.getCategoryId() == selectedCategoryId) {
                    filteredByCategory.add(item);
                }
            }
        }

        // 2. التصفية حسب نص البحث
        ArrayList<ItemsModel> finalFilteredList = new ArrayList<>();
        if (searchText.isEmpty()) {
            finalFilteredList.addAll(filteredByCategory);
        } else {
            for (ItemsModel item : filteredByCategory) {
                if (item.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                    finalFilteredList.add(item);
                }
            }
        }

        // 3. تحديث الواجهة
        productsAdapter.updateList(finalFilteredList);
    }
}