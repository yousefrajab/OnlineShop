// CategoryAdapter.java - النسخة المصححة

package com.example.onlineshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater; // <-- إضافة مهمة
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // <-- إضافة للتحسين
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlineshop.Domain.CategoryModel;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ViewholderCategoryBinding;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<CategoryModel> items;
    private Context context;
    private int selectedPosition = -1;
    private int lastSelectedPosition = -1;

    public CategoryAdapter(ArrayList<CategoryModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // <-- الخطوة 1: التصحيح هنا، استخدم LayoutInflater
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.binding.titleTxt.setText(items.get(position).getTitle());

        // في النسخة الأصلية، كان getAdapterPosition() هو الحل الأمثل لتجنب المشاكل
        // لكن بما أنك تستخدم position مباشرة في onClick، فهذا يعمل لكنه أقل أماناً
        // الطريقة التالية آمنة
        holder.binding.getRoot().setOnClickListener(view -> {
            lastSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);
        });

        if (selectedPosition == position) {
            holder.binding.titleTxt.setBackgroundResource(R.drawable.orange_bg);
            // <-- الخطوة 2 (تحسين): استخدام ContextCompat
            holder.binding.titleTxt.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.binding.titleTxt.setBackgroundResource(R.drawable.stroke_bg);
            holder.binding.titleTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCategoryBinding binding;

        public ViewHolder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}