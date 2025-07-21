package com.example.onlineshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.Helper.ManagmentCart;
import com.example.onlineshop.databinding.ViewholderCartBinding;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
    private ArrayList<ItemsModel> listItemSelected;
    private ManagmentCart managmentCart;

    // تم تبسيط الـ Constructor
    public CartAdapter(ArrayList<ItemsModel> listItemSelected, Context context) {
        this.listItemSelected = listItemSelected;
        this.managmentCart = new ManagmentCart(context);
    }

    @NonNull
    @Override
    public CartAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.Viewholder holder, int position) {
        ItemsModel item = listItemSelected.get(position);

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText("$" + item.getPrice());
        holder.binding.totalEachItem.setText("$" + Math.round((item.getNumberinCart() * item.getPrice())));
        holder.binding.numberItemTxt.setText(String.valueOf(item.getNumberinCart()));

        // التأكد من أن قائمة الصور ليست فارغة قبل تحميل الصورة
        if (item.getPicUrl() != null && !item.getPicUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getPicUrl().get(0))
                    .into(holder.binding.pic);
        }

        // ----> التعديل الرئيسي هنا <----
        holder.binding.plusCartBtn.setOnClickListener(v -> {
            managmentCart.plusItem(item);
            // لا نحتاج لـ notifyDataSetChanged() هنا، لأن LiveData ستتكفل بالتحديث
        });

        holder.binding.minusCartBtn.setOnClickListener(v -> {
            managmentCart.minusItem(item);
            // لا نحتاج لـ notifyDataSetChanged() هنا أيضاً
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    // دالة جديدة لتحديث بيانات الـ Adapter عندما تتغير في LiveData
    public void updateList(ArrayList<ItemsModel> newList) {
        this.listItemSelected = newList;
        notifyDataSetChanged();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public Viewholder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}