package com.example.onlineshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.onlineshop.Domain.UserModel;
import com.example.onlineshop.Fragment.AdminDashboardFragment;
import com.example.onlineshop.Fragment.CartFragment;
import com.example.onlineshop.Fragment.ExploreFragment;
import com.example.onlineshop.Fragment.FavoritesFragment;
import com.example.onlineshop.Fragment.HomeFragment;
import com.example.onlineshop.Fragment.ProfileFragment;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private boolean isUserAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            goToLoginActivity();
            return;
        }

        setupTopBarButtons();
        checkIfUserIsAdmin();
        loadUserProfile();

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment(), false);
            binding.bottomNavigation.setItemSelected(R.id.home, true);
        }
        setupBottomNavigation();
    }

    private void checkIfUserIsAdmin() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Admins").child(userId);
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isUserAdmin = snapshot.exists() && Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                binding.adminBtn.setVisibility(isUserAdmin ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isUserAdmin = false;
                binding.adminBtn.setVisibility(View.GONE);
            }
        });
    }

    private void setupTopBarButtons() {
        binding.cartBtn.setOnClickListener(v -> {
            replaceFragment(new CartFragment(), false);
            binding.bottomNavigation.setItemSelected(R.id.cart, true);
        });

        // تأكد أن الـ ID في الـ XML هو R.id.profile_image
        binding.profileImage.setOnClickListener(v -> {
            replaceFragment(new ProfileFragment(), false);
            binding.bottomNavigation.setItemSelected(R.id.profile, true);
        });

        // تأكد أن الـ ID في الـ XML هو R.id.adminBtn
        binding.adminBtn.setOnClickListener(v -> {
            replaceFragment(new AdminDashboardFragment(), true);
        });
    }

    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null) {
                        // تأكد أن الـ ID في الـ XML هو R.id.userNameTxt
                        binding.userNameTxt.setText(user.getName());

                        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                            // تأكد أن الـ ID في الـ XML هو R.id.profile_image
                            Glide.with(MainActivity.this)
                                    .load(user.getProfileImageUrl())
                                    .placeholder(R.drawable.profile)
                                    .error(R.drawable.profile)
                                    .circleCrop()
                                    .into(binding.profileImage);
                        } else {
                            binding.profileImage.setImageResource(R.drawable.profile);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast.makeText(MainActivity.this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(i -> {
            if (i == R.id.home) {
                replaceFragment(new HomeFragment(), false);
            } else if (i == R.id.explore) {
                replaceFragment(new ExploreFragment(), false);
            } else if (i == R.id.favorites) {
                replaceFragment(new FavoritesFragment(), false);
            } else if (i == R.id.cart) {
                replaceFragment(new CartFragment(), false);
            } else if (i == R.id.profile) {
                replaceFragment(new ProfileFragment(), false);
            }
        });
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}