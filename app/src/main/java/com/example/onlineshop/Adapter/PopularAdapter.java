package com.example.onlineshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.example.onlineshop.Activity.DetailActivity;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ViewholderPopularBinding;
import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {
    ArrayList<ItemsModel> items;
    Context context;
    private ProductAdminClickListener adminClickListener;

    // Constructor لواجهة العميل
    public PopularAdapter(ArrayList<ItemsModel> items) {
        this.items = items;
        this.adminClickListener = null;
    }

    // Constructor لواجهة الأدمن
    public PopularAdapter(ArrayList<ItemsModel> items, ProductAdminClickListener listener) {
        this.items = items;
        this.adminClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    // ▼▼▼ هذه هي الدالة التي تم إصلاحها بشكل جذري ▼▼▼
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsModel item = items.get(position);

        // --- التحقق الآمن من كل حقل قبل عرضه ---

        // العنوان (عادة لا يكون null)
        holder.binding.titleTxt.setText(item.getTitle());

        // الأرقام (آمنة)
        holder.binding.priceTxt.setText("$" + item.getPrice());
        holder.binding.oldPriceTxt.setText("$" + item.getOldPrice());
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.ratingTxt.setText("(" + item.getRating() + ")");

        // حقل نسبة الخصم (قد يكون null)
        if (item.getOffPercent() != null && !item.getOffPercent().isEmpty()) {
            holder.binding.offPercentTxt.setText(item.getOffPercent());
            holder.binding.offPercentTxt.setVisibility(View.VISIBLE);
        } else {
            // إذا كان null، قم بإخفاء العنصر لكي لا يشغل مساحة
            holder.binding.offPercentTxt.setVisibility(View.GONE);
        }

        // حقل الصور (قد يكون null)
        if (item.getPicUrl() != null && !item.getPicUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getPicUrl().get(0))
                    .apply(new RequestOptions().transform(new CenterInside()))
                    .into(holder.binding.pic);
        } else {
            // إذا لم تكن هناك صور، اعرض صورة افتراضية
            holder.binding.pic.setImageResource(R.drawable.pic_placeholder);
        }

        // OnClickListener يبقى كما هو
        holder.itemView.setOnClickListener(v -> {
            if (adminClickListener != null) {
                adminClickListener.onProductClick(item);
            } else {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("object", item);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateList(ArrayList<ItemsModel> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;
        public ViewHolder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}