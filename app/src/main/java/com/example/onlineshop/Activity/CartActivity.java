package com.example.onlineshop.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlineshop.Adapter.CartAdapter;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.Helper.ManagmentCart;
import com.example.onlineshop.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private ManagmentCart managmentCart;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        setVariable();
        initCartList();
        observeCartData(); // <-- دالة جديدة لمراقبة LiveData
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initCartList() {
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // تهيئة الـ Adapter مع قائمة فارغة في البداية
        cartAdapter = new CartAdapter(new ArrayList<>(), this);
        binding.cartView.setAdapter(cartAdapter);
    }

    // ----> هذا هو قلب النظام الجديد <----
    private void observeCartData() {
        managmentCart.getCartList().observe(this, new Observer<ArrayList<ItemsModel>>() {
            @Override
            public void onChanged(ArrayList<ItemsModel> items) {
                // هذه الدالة سيتم استدعاؤها تلقائياً كلما تغيرت بيانات السلة في Firebase

                if (items == null || items.isEmpty()) {
                    // إذا كانت السلة فارغة
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                    binding.scrollView3.setVisibility(View.GONE);
                } else {
                    // إذا كانت السلة تحتوي على منتجات
                    binding.emptyTxt.setVisibility(View.GONE);
                    binding.scrollView3.setVisibility(View.VISIBLE);
                }

                // تحديث بيانات الـ Adapter بالقائمة الجديدة
                cartAdapter.updateList(items);

                // حساب وتحديث الإجمالي
                calculateCart(items);
            }
        });
    }

    // تم تعديل الدالة لتستقبل القائمة مباشرة
    private void calculateCart(ArrayList<ItemsModel> list) {
        double percentTax = 0.02;
        double delivery = 10;

        // استخدام الدالة الجديدة لحساب الإجمالي
        double itemTotal = managmentCart.getTotalFee(list);

        double tax = Math.round((itemTotal * percentTax) * 100.0) / 100.0;
        double total = Math.round((itemTotal + tax + delivery) * 100.0) / 100.0;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax); // تم تصحيح عرض الضريبة
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }
}