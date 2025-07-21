package com.example.onlineshop.Activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.onlineshop.Adapter.ColorAdapter;
import com.example.onlineshop.Adapter.PicListAdapter;
import com.example.onlineshop.Adapter.SizeAdapter;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.Helper.ManagmentCart;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private ItemsModel object;
    private int numberOrder = 1;
    private ManagmentCart managmentCart;

    private DatabaseReference favoritesRef;
    private FirebaseAuth mAuth;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
        }

        getBundles();
    }

    private void getBundles() {
        // ▼▼▼ هذا هو السطر الوحيد الذي تم تغييره ▼▼▼
        // استخدمنا getParcelableExtra بدلاً من getSerializableExtra
        object = (ItemsModel) getIntent().getParcelableExtra("object");

        if (object == null) {
            Toast.makeText(this, "Error: Item not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.oldPriceTxt.setText("$" + object.getOldPrice());
        binding.oldPriceTxt.setPaintFlags(binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.descriptionTxt.setText(object.getDescription());

        initPicList();
        initSize();
        initColor();

        binding.addToCartBtn.setOnClickListener(v -> {
            object.setNumberinCart(numberOrder);
            managmentCart.insertItem(object);
        });

        binding.backBtn.setOnClickListener(v -> finish());

        if (favoritesRef != null) {
            checkIfFavorite();
            binding.favBtn.setOnClickListener(v -> toggleFavorite());
        }
    }

    private void checkIfFavorite() {
        DatabaseReference itemRef = favoritesRef.child(object.getTitle());
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavorite = snapshot.exists();
                updateFavoriteIcon();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Error checking favorite status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFavorite() {
        DatabaseReference itemRef = favoritesRef.child(object.getTitle());
        if (isFavorite) {
            itemRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            itemRef.setValue(object).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            binding.favBtn.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            binding.favBtn.setImageResource(R.drawable.fav);
        }
    }

    private void initColor() {
        if (object.getColor() != null && !object.getColor().isEmpty()) {
            binding.recyclerColor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerColor.setAdapter(new ColorAdapter(object.getColor()));
        }
    }

    private void initSize() {
        if (object.getSize() != null && !object.getSize().isEmpty()) {
            binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerSize.setAdapter(new SizeAdapter(object.getSize()));
        }
    }

    private void initPicList() {
        if (object.getPicUrl() != null && !object.getPicUrl().isEmpty()) {
            Glide.with(this).load(object.getPicUrl().get(0)).into(binding.pic);
            binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.picList.setAdapter(new PicListAdapter(object.getPicUrl(), binding.pic));
        }
    }
}