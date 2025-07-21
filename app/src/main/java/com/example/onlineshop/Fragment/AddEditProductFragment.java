package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlineshop.Domain.CategoryModel;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.R;
import com.example.onlineshop.ViewModel.MainViewModel;
import com.example.onlineshop.databinding.FragmentAddEditProductBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddEditProductFragment extends Fragment {

    private FragmentAddEditProductBinding binding;
    private MainViewModel viewModel;
    private DatabaseReference itemsRef;
    private List<CategoryModel> categoryList = new ArrayList<>();
    private ItemsModel productToEdit;

    public static AddEditProductFragment newInstance(ItemsModel product) {
        AddEditProductFragment fragment = new AddEditProductFragment();
        Bundle args = new Bundle();
        args.putParcelable("product_to_edit", product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productToEdit = getArguments().getParcelable("product_to_edit");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        itemsRef = FirebaseDatabase.getInstance().getReference("Items");

        loadCategories();
        setupButtons();

        if (productToEdit != null) {
            setupEditMode();
        }
    }

    private void setupEditMode() {
        binding.pageTitleTxt.setText("Edit Product");
        binding.titleEdt.setText(productToEdit.getTitle());
        // في وضع التعديل، نسمح بتغيير العنوان
        binding.titleEdt.setEnabled(true);

        binding.descriptionEdt.setText(productToEdit.getDescription());
        binding.priceEdt.setText(String.valueOf(productToEdit.getPrice()));
        binding.oldPriceEdt.setText(String.valueOf(productToEdit.getOldPrice()));
        binding.ratingEdt.setText(String.valueOf(productToEdit.getRating()));
        binding.offPercentEdt.setText(productToEdit.getOffPercent());
        binding.reviewEdt.setText(String.valueOf(productToEdit.getReview()));

        addDeleteButton();
    }

    private void loadCategories() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                categoryList = categories;
                List<String> categoryNames = categories.stream().map(CategoryModel::getTitle).collect(Collectors.toList());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.categorySpinner.setAdapter(adapter);

                if (productToEdit != null) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getId() == productToEdit.getCategoryId()) {
                            binding.categorySpinner.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void setupButtons() {
        binding.backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        binding.saveProductBtn.setOnClickListener(v -> saveProduct());
    }

    // --- دالة الحفظ النهائية والصحيحة التي تعتمد على المفتاح الفريد (Key) ---
    private void saveProduct() {
        String title = binding.titleEdt.getText().toString().trim();
        String description = binding.descriptionEdt.getText().toString().trim();
        String priceStr = binding.priceEdt.getText().toString().trim();
        String oldPriceStr = binding.oldPriceEdt.getText().toString().trim();
        String ratingStr = binding.ratingEdt.getText().toString().trim();
        String offPercent = binding.offPercentEdt.getText().toString().trim();
        String reviewStr = binding.reviewEdt.getText().toString().trim();

        if (title.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Title and Price are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        double price = 0, oldPrice = 0, rating = 0;
        int review = 0;
        try {
            price = Double.parseDouble(priceStr);
            if (!oldPriceStr.isEmpty()) oldPrice = Double.parseDouble(oldPriceStr);
            if (!ratingStr.isEmpty()) rating = Double.parseDouble(ratingStr);
            if (!reviewStr.isEmpty()) review = Integer.parseInt(reviewStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers.", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        int selectedCategoryPosition = binding.categorySpinner.getSelectedItemPosition();
        int categoryId = (selectedCategoryPosition >= 0) ? categoryList.get(selectedCategoryPosition).getId() : -1;

        if (productToEdit != null) {
            // --- وضع التعديل (باستخدام المفتاح الحقيقي للمنتج) ---
            Map<String, Object> updates = new HashMap<>();
            updates.put("title", title);
            updates.put("description", description);
            updates.put("price", price);
            updates.put("oldPrice", oldPrice);
            updates.put("rating", rating);
            updates.put("categoryId", categoryId);
            updates.put("offPercent", offPercent);
            updates.put("review", review);

            itemsRef.child(productToEdit.getKey()).updateChildren(updates).addOnCompleteListener(task -> {
                binding.progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show();
                    if (getParentFragmentManager() != null) getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Failed to update product.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // --- وضع الإضافة (باستخدام مفتاح جديد وفريد) ---
            String key = itemsRef.push().getKey();

            ItemsModel newProduct = new ItemsModel();
            newProduct.setKey(key);
            newProduct.setTitle(title);
            newProduct.setDescription(description);
            newProduct.setPrice(price);
            newProduct.setOldPrice(oldPrice);
            newProduct.setRating(rating);
            newProduct.setCategoryId(categoryId);
            newProduct.setOffPercent(offPercent);
            newProduct.setReview(review);
            newProduct.setPicUrl(new ArrayList<>());
            newProduct.setColor(new ArrayList<>());
            newProduct.setSize(new ArrayList<>());

            if (key != null) {
                itemsRef.child(key).setValue(newProduct).addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();
                        if (getParentFragmentManager() != null) getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Failed to add product.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void addDeleteButton() {
        androidx.appcompat.widget.AppCompatButton deleteBtn = new androidx.appcompat.widget.AppCompatButton(requireContext());
        deleteBtn.setText("Delete Product");
        deleteBtn.setTextColor(getResources().getColor(android.R.color.white));

        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        shape.setColor(getResources().getColor(R.color.red));
        shape.setCornerRadius(16f);
        deleteBtn.setBackground(shape);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) (16 * getResources().getDisplayMetrics().density), 0, 0);
        deleteBtn.setLayoutParams(params);

        ViewGroup parentView = (ViewGroup) binding.saveProductBtn.getParent();
        parentView.addView(deleteBtn, parentView.indexOfChild(binding.progressBar));

        deleteBtn.setOnClickListener(v -> deleteProduct());
    }

    private void deleteProduct() {
        if (productToEdit != null) {
            // --- الحذف باستخدام المفتاح الحقيقي للمنتج ---
            itemsRef.child(productToEdit.getKey()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                    if (getParentFragmentManager() != null) getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}