package com.example.onlineshop.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlineshop.Domain.BannerModel;
import com.example.onlineshop.Domain.CategoryModel;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.Respository.MainRepository;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    private final MainRepository repository = new MainRepository();

    // استخدم أسماء دوال أكثر وضوحاً
    public LiveData<ArrayList<CategoryModel>> getCategories() {
        return repository.loadCategory();
    }

    public LiveData<ArrayList<BannerModel>> getBanners() {
        return repository.loadBanner();
    }

    public LiveData<ArrayList<ItemsModel>> getPopular() {
        return repository.loadPopular();
    }
}