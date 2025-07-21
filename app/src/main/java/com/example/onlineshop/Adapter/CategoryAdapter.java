package com.example.onlineshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlineshop.Domain.CategoryModel;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ViewholderCategoryBinding;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final ArrayList<CategoryModel> items;
    private int selectedPosition = 0; // "All" (أول عنصر) هي المختارة افتراضيًا

    // ▼▼▼ المتغير الجديد للاحتفاظ بالـ Listener ▼▼▼
    private final CategoryClickListener listener;
    private Context context; // تمت إضافته ليكون متاحًا في كل الكلاس

    // ▼▼▼ تعديل الـ Constructor ليقبل الـ Listener ▼▼▼
    public CategoryAdapter(ArrayList<CategoryModel> items, CategoryClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel item = items.get(position);
        holder.binding.titleTxt.setText(item.getTitle());

        // ▼▼▼ تعديل الـ OnClickListener ليكون أكثر أمانًا وفعالية ▼▼▼
        holder.itemView.setOnClickListener(v -> {
            // التحقق من أن المستخدم لم يضغط على نفس العنصر مرة أخرى
            if (selectedPosition == holder.getAdapterPosition()) {
                return;
            }

            // تحديث المواقع القديمة والجديدة
            int lastSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // إعلام الـ RecyclerView بتحديث العنصرين فقط بدلاً من كل القائمة
            notifyItemChanged(lastSelected);
            notifyItemChanged(selectedPosition);

            // استدعاء دالة الـ listener في HomeFragment لتصفية المنتجات
            if (listener != null) {
                listener.onCategoryClick(item.getId());
            }
        });

        // تحديث مظهر العنصر بناءً على ما إذا كان محددًا أم لا
        if (selectedPosition == position) {
            holder.binding.titleTxt.setBackgroundResource(R.drawable.orange_bg);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCategoryBinding binding;

        public ViewHolder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}