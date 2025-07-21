package com.example.onlineshop.Fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.onlineshop.Adapter.AdminImagesAdapter;
import com.example.onlineshop.Domain.CategoryModel;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.R;
import com.example.onlineshop.ViewModel.MainViewModel;
import com.example.onlineshop.databinding.FragmentAddEditProductBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
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

    // --- متغيرات جديدة لإدارة الصور ---
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private AdminImagesAdapter imagesAdapter;
    private ArrayList<String> uploadedImageUrls = new ArrayList<>();
    private int uploadCounter = 0;

    // --- Launcher جديد لاختيار الصور المتعددة ---
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUris.clear(); // مسح القائمة القديمة قبل إضافة الجديدة
                    if (result.getData().getClipData() != null) {
                        // اختار المستخدم عدة صور
                        ClipData clipData = result.getData().getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            selectedImageUris.add(clipData.getItemAt(i).getUri());
                        }
                    } else if (result.getData().getData() != null) {
                        // اختار المستخدم صورة واحدة
                        selectedImageUris.add(result.getData().getData());
                    }
                    imagesAdapter.notifyDataSetChanged();
                }
            }
    );

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

        setupImagesRecyclerView();
        loadCategories();
        setupButtons();

        if (productToEdit != null) {
            setupEditMode();
        }
    }

    private void setupImagesRecyclerView() {
        imagesAdapter = new AdminImagesAdapter(requireContext(), selectedImageUris);
        binding.imagesRecyclerView.setAdapter(imagesAdapter);
    }

    private void setupEditMode() {
        binding.pageTitleTxt.setText("Edit Product");
        binding.titleEdt.setText(productToEdit.getTitle());
        binding.titleEdt.setEnabled(true);
        binding.descriptionEdt.setText(productToEdit.getDescription());
        binding.priceEdt.setText(String.valueOf(productToEdit.getPrice()));
        binding.oldPriceEdt.setText(String.valueOf(productToEdit.getOldPrice()));
        binding.ratingEdt.setText(String.valueOf(productToEdit.getRating()));
        binding.offPercentEdt.setText(productToEdit.getOffPercent());
        binding.reviewEdt.setText(String.valueOf(productToEdit.getReview()));

        if (productToEdit.getColor() != null) {
            binding.colorsEdt.setText(String.join(",", productToEdit.getColor()));
        }
        if (productToEdit.getSize() != null) {
            binding.sizesEdt.setText(String.join(",", productToEdit.getSize()));
        }
        // ملاحظة: عرض الصور القديمة يتطلب منطقًا إضافيًا لتحميلها من الروابط
        // سنتخطاه الآن للتركيز على وظيفة الإضافة والتعديل

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

        binding.addImagesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePickerLauncher.launch(intent);
        });

        binding.saveProductBtn.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        binding.progressBar.setVisibility(View.VISIBLE);

        if (!selectedImageUris.isEmpty()) {
            uploadImagesAndThenSaveData();
        } else {
            // لا توجد صور جديدة، احفظ البيانات مباشرة
            ArrayList<String> imageUrls = (productToEdit != null && productToEdit.getPicUrl() != null)
                    ? productToEdit.getPicUrl()
                    : new ArrayList<>();
            saveDataToFirebase(imageUrls);
        }
    }

    private void uploadImagesAndThenSaveData() {
        uploadedImageUrls.clear();
        uploadCounter = 0;

        // إذا كان في وضع التعديل، أضف الروابط القديمة أولاً
        if (productToEdit != null && productToEdit.getPicUrl() != null) {
            uploadedImageUrls.addAll(productToEdit.getPicUrl());
        }

        if (selectedImageUris.isEmpty()) {
            saveDataToFirebase(uploadedImageUrls);
            return;
        }

        for (Uri uri : selectedImageUris) {
            MediaManager.get().upload(uri).callback(new UploadCallback() {
                @Override
                public void onSuccess(String requestId, Map resultData) {
                    uploadedImageUrls.add((String) resultData.get("secure_url"));
                    uploadCounter++;
                    if (uploadCounter == selectedImageUris.size()) {
                        saveDataToFirebase(uploadedImageUrls);
                    }
                }
                @Override
                public void onError(String requestId, ErrorInfo error) {
                    uploadCounter++;
                    if (uploadCounter == selectedImageUris.size()) {
                        saveDataToFirebase(uploadedImageUrls);
                    }
                }
                @Override public void onStart(String requestId) {}
                @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                @Override public void onReschedule(String requestId, ErrorInfo error) {}
            }).dispatch();
        }
    }

    private void saveDataToFirebase(ArrayList<String> finalImageUrls) {
        String title = binding.titleEdt.getText().toString().trim();
        String description = binding.descriptionEdt.getText().toString().trim();
        String priceStr = binding.priceEdt.getText().toString().trim();
        String oldPriceStr = binding.oldPriceEdt.getText().toString().trim();
        String ratingStr = binding.ratingEdt.getText().toString().trim();
        String offPercent = binding.offPercentEdt.getText().toString().trim();
        String reviewStr = binding.reviewEdt.getText().toString().trim();
        String colorsStr = binding.colorsEdt.getText().toString().trim();
        String sizesStr = binding.sizesEdt.getText().toString().trim();

        if (title.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Title and Price are required.", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

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

        ArrayList<String> colorsList = new ArrayList<>();
        if (!colorsStr.isEmpty()) colorsList.addAll(Arrays.asList(colorsStr.split("\\s*,\\s*")));

        ArrayList<String> sizesList = new ArrayList<>();
        if (!sizesStr.isEmpty()) sizesList.addAll(Arrays.asList(sizesStr.split("\\s*,\\s*")));

        if (productToEdit != null) {
            // وضع التعديل
            Map<String, Object> updates = new HashMap<>();
            updates.put("title", title);
            updates.put("description", description);
            updates.put("price", price);
            updates.put("oldPrice", oldPrice);
            updates.put("rating", rating);
            updates.put("categoryId", categoryId);
            updates.put("offPercent", offPercent);
            updates.put("review", review);
            updates.put("picUrl", finalImageUrls);
            updates.put("color", colorsList);
            updates.put("size", sizesList);

            itemsRef.child(productToEdit.getKey()).updateChildren(updates).addOnCompleteListener(task -> {
                handleSaveCompletion(task, "updated");
            });
        } else {
            // وضع الإضافة
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
            newProduct.setPicUrl(finalImageUrls);
            newProduct.setColor(colorsList);
            newProduct.setSize(sizesList);

            if (key != null) {
                itemsRef.child(key).setValue(newProduct).addOnCompleteListener(task -> {
                    handleSaveCompletion(task, "added");
                });
            }
        }
    }

    private void handleSaveCompletion(com.google.android.gms.tasks.Task<Void> task, String action) {
        binding.progressBar.setVisibility(View.GONE);
        if (task.isSuccessful()) {
            Toast.makeText(getContext(), "Product " + action + " successfully!", Toast.LENGTH_SHORT).show();
            if (getParentFragmentManager() != null) getParentFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "Failed to " + action + " product.", Toast.LENGTH_SHORT).show();
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