package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlineshop.Adapter.CartAdapter;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.Domain.OrderModel;
import com.example.onlineshop.Helper.ManagmentCart;
import com.example.onlineshop.databinding.FragmentCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private ManagmentCart managmentCart;
    private CartAdapter cartAdapter;
    private ArrayList<ItemsModel> currentCartItems = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        managmentCart = new ManagmentCart(getContext());

        initCartList();
        observeCartData();

        setupCheckoutButton();
    }

    private void setupCheckoutButton() {
        binding.checkoutBtn.setOnClickListener(v -> {
            // 1. تحقق من أن السلة ليست فارغة
            if (currentCartItems == null || currentCartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. إنشاء الطلب
            placeOrder();
        });
    }

    private void placeOrder() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);

        // إنشاء ID فريد للطلب باستخدام push()
        String orderId = ordersRef.push().getKey();

        // حساب السعر الإجمالي
        double totalAmount = Double.parseDouble(binding.totalTxt.getText().toString().replace("$", ""));

        // إنشاء كائن الطلب
        OrderModel newOrder = new OrderModel(orderId, System.currentTimeMillis(), totalAmount, currentCartItems);

        // 3. حفظ الطلب في قاعدة البيانات
        if (orderId != null) {
            ordersRef.child(orderId).setValue(newOrder).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // 4. إذا نجح الحفظ، قم بمسح سلة المشتريات
                    clearCart();
                    Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clearCart() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(userId);
        cartRef.removeValue(); // حذف كل المنتجات من سلة المستخدم
    }

    private void initCartList() {
        binding.cartView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        // تهيئة الـ Adapter مع قائمة فارغة
        cartAdapter = new CartAdapter(new ArrayList<>(), getContext());
        binding.cartView.setAdapter(cartAdapter);
    }

    private void observeCartData() {
        managmentCart.getCartList().observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                binding.emptyTxt.setVisibility(View.VISIBLE);
                binding.scrollViewCart.setVisibility(View.GONE);
            } else {
                binding.emptyTxt.setVisibility(View.GONE);
                binding.scrollViewCart.setVisibility(View.VISIBLE);
            }
            currentCartItems = items;

            cartAdapter.updateList(items);
            calculateCart(items);
        });
    }

    private void calculateCart(ArrayList<ItemsModel> list) {
        double percentTax = 0.02;
        double delivery = 10;
        double itemTotal = managmentCart.getTotalFee(list);
        double tax = Math.round((itemTotal * percentTax) * 100.0) / 100.0;
        double total = Math.round((itemTotal + tax + delivery) * 100.0) / 100.0;

        binding.totalFeeTxt.setText("$" + String.format("%.2f", itemTotal));
        binding.taxTxt.setText("$" + String.format("%.2f", tax));
        binding.deliveryTxt.setText("$" + String.format("%.2f", delivery));
        binding.totalTxt.setText("$" + String.format("%.2f", total));
    }
}