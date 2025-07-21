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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.onlineshop.Adapter.CategoryAdapter;
import com.example.onlineshop.Adapter.CategoryClickListener;
import com.example.onlineshop.Adapter.PopularAdapter;
import com.example.onlineshop.Adapter.SliderAdapter;
import com.example.onlineshop.Domain.BannerModel;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.ViewModel.MainViewModel;
import com.example.onlineshop.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements CategoryClickListener {

    private FragmentHomeBinding binding;
    private MainViewModel viewModel;
    private PopularAdapter popularAdapter;
    private final ArrayList<ItemsModel> allProducts = new ArrayList<>();
    private int selectedCategoryId = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. تهيئة الـ ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 2. إعداد الواجهات الفارغة أولاً
        setupRecyclerViews();
        setupSearch();

        // 3. ثم تحميل البيانات
        loadDataFromServer();
    }

    private void setupRecyclerViews() {
        // إعداد RecyclerView الخاص بالفئات
        binding.categoryView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // إعداد RecyclerView الخاص بالمنتجات الشائعة
        binding.popularView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularAdapter = new PopularAdapter(new ArrayList<>());
        binding.popularView.setAdapter(popularAdapter);
    }

    private void setupSearch() {
        binding.editTextText.addTextChangedListener(new TextWatcher() {
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

    private void loadDataFromServer() {
        // إظهار كل مؤشرات التحميل
        binding.progressBarSlider.setVisibility(View.VISIBLE);
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        binding.progressBarPopular.setVisibility(View.VISIBLE);

        // تحميل البانرات
        viewModel.getBanners().observe(getViewLifecycleOwner(), bannerModels -> {
            if (bannerModels != null && !bannerModels.isEmpty()) {
                setupBannerSlider(bannerModels);
            }
            binding.progressBarSlider.setVisibility(View.GONE);
        });

        // تحميل الفئات
        viewModel.getCategories().observe(getViewLifecycleOwner(), categoryModels -> {
            if (categoryModels != null && !categoryModels.isEmpty()) {
                binding.categoryView.setAdapter(new CategoryAdapter(categoryModels, this));
            }
            binding.progressBarCategory.setVisibility(View.GONE);
        });

        // تحميل المنتجات
        viewModel.getPopular().observe(getViewLifecycleOwner(), itemsModels -> {
            if (itemsModels != null && !itemsModels.isEmpty()) {
                allProducts.clear();
                allProducts.addAll(itemsModels);
                filterProducts(""); // عرض كل المنتجات في البداية
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
    }

    private void setupBannerSlider(ArrayList<BannerModel> bannerModels) {
        binding.ViewPagerSlider.setAdapter(new SliderAdapter(bannerModels, binding.ViewPagerSlider));
        binding.ViewPagerSlider.setClipToPadding(false);
        binding.ViewPagerSlider.setClipChildren(false);
        binding.ViewPagerSlider.setOffscreenPageLimit(3);
        binding.ViewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.ViewPagerSlider.setPageTransformer(compositePageTransformer);
    }

    @Override
    public void onCategoryClick(int categoryId) {
        selectedCategoryId = categoryId;
        binding.editTextText.setText("");
        filterProducts("");
    }

    private void filterProducts(String searchText) {
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

        popularAdapter.updateList(finalFilteredList);
    }
}