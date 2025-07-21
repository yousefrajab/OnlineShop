package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlineshop.Adapter.PopularAdapter;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.databinding.FragmentFavoritesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private PopularAdapter adapter;
    private DatabaseReference favoritesRef;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // تهيئة Firebase
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
        }

        initRecyclerView();
        loadFavorites();
    }

    private void initRecyclerView() {
        // استخدام GridLayoutManager لعرض المنتجات في شبكة (عمودين)
        binding.favoritesView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // تهيئة الـ Adapter مع قائمة فارغة في البداية
        adapter = new PopularAdapter(new ArrayList<>());
        binding.favoritesView.setAdapter(adapter);
    }

    private void loadFavorites() {
        binding.progressBarFavorites.setVisibility(View.VISIBLE);

        if (favoritesRef != null) {
            favoritesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<ItemsModel> favoriteItems = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        favoriteItems.add(itemSnapshot.getValue(ItemsModel.class));
                    }

                    if (favoriteItems.isEmpty()) {
                        binding.emptyTxt.setVisibility(View.VISIBLE);
                        binding.favoritesView.setVisibility(View.GONE);
                    } else {
                        binding.emptyTxt.setVisibility(View.GONE);
                        binding.favoritesView.setVisibility(View.VISIBLE);
                    }

                    // تحديث بيانات الـ Adapter
                    adapter.updateList(favoriteItems);
                    binding.progressBarFavorites.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.progressBarFavorites.setVisibility(View.GONE);
                    // يمكنك إضافة رسالة خطأ هنا
                }
            });
        }
    }
}