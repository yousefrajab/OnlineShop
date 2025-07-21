package com.example.onlineshop.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.onlineshop.Activity.LoginActivity;
import com.example.onlineshop.Domain.UserModel;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private UserModel currentUser;
    private Uri imageUri;

    // نفس الـ Launcher الذي استخدمناه من قبل
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    binding.profileImageView.setImageURI(imageUri);
                    uploadImageToCloudinary();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        loadUserProfile();
        setupButtons();
        setupImagePicker();
    }

    private void setupImagePicker() {
        binding.profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(intent);
        });
    }

    // --- هذه الدالة أصبحت تستخدم Cloudinary ---
    private void uploadImageToCloudinary() {
        if (imageUri != null) {
            Toast.makeText(getContext(), "Uploading picture...", Toast.LENGTH_SHORT).show();
            MediaManager.get().upload(imageUri).callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    // الرفع بدأ
                }
                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    // يمكنك استخدام هذا لعرض شريط تقدم
                }
                @Override
                public void onSuccess(String requestId, Map resultData) {
                    // نجح الرفع!
                    // نحصل على الرابط الآمن من نتيجة الرفع
                    String imageUrl = (String) resultData.get("secure_url");
                    // حفظ الرابط في Realtime Database
                    saveImageUrlToDatabase(imageUrl);
                }
                @Override
                public void onError(String requestId, ErrorInfo error) {
                    // فشل الرفع
                    Toast.makeText(getContext(), "Upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    // تم إعادة جدولة الرفع
                }
            }).dispatch();
        }
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        userRef.child("profileImageUrl").setValue(imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to save image URL.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserProfile() {
        // نستخدم addValueEventListener للاستماع للتحديثات الفورية
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUser = snapshot.getValue(UserModel.class);
                    if (currentUser != null) {
                        binding.profileNameTxt.setText(currentUser.getName());
                        binding.profileEmailTxt.setText(currentUser.getEmail());
                        binding.profileNameEdt.setText(currentUser.getName());

                        // نفس الكود لعرض الصورة باستخدام Glide
                        if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
                            // ... داخل onDataChange ...
                            Glide.with(getContext())
                                    .load(currentUser.getProfileImageUrl())
                                    .placeholder(R.drawable.profile)
                                    .error(R.drawable.profile)
                                    .circleCrop() // <-- أضف نفس السطر السحري هنا أيضًا
                                    .into(binding.profileImageView);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- باقي الدوال (setupButtons, toggleEditMode, updateUserName) تبقى كما هي ---
    // (الكود موجود في ردنا السابق، تأكد من أنه موجود هنا)
    private void setupButtons() {

        binding.myOrdersBtn.setOnClickListener(v -> {
            // استدعاء دالة لفتح OrderHistoryFragment
            goToOrderHistory();
        });
        binding.editProfileBtn.setOnClickListener(v -> toggleEditMode(true));
        binding.saveChangesBtn.setOnClickListener(v -> updateUserName());
        binding.logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getContext(), "You have been logged out.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void goToOrderHistory() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OrderHistoryFragment())
                    .addToBackStack(null) // هذا السطر مهم للسماح بالعودة
                    .commit();
        }
    }

    private void toggleEditMode(boolean isEditing) {
        binding.profileNameTxt.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        binding.profileNameEdt.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        binding.editProfileBtn.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        binding.saveChangesBtn.setVisibility(isEditing ? View.VISIBLE : View.GONE);
    }

    private void updateUserName() {
        String newName = binding.profileNameEdt.getText().toString().trim();
        if (newName.isEmpty()) {
            binding.profileNameEdt.setError("Name cannot be empty");
            return;
        }
        userRef.child("name").setValue(newName).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                binding.profileNameTxt.setText(newName);
                toggleEditMode(false);
            } else {
                Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}