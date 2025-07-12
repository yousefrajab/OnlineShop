package com.example.onlineshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.onlineshop.Domain.BannerModel;
import com.example.onlineshop.R;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private ArrayList<BannerModel> slideritems;
    private ViewPager2 viewPager2;
    private Context context;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            slideritems.addAll(slideritems);
            notifyDataSetChanged();
        }
    };

    public SliderAdapter(ArrayList<BannerModel> slideritems, ViewPager2 viewPager2) {
        this.slideritems = slideritems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();
        return new SliderViewHolder(LayoutInflater.from(context).inflate(R.layout.slider_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.SliderViewHolder holder, int position) {

        holder.setImage(slideritems.get(position));
        if (position==slideritems.size()-2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return slideritems.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.iamgeSlider);
        }
        void setImage(BannerModel bannerModel){
            Glide.with(context).load(bannerModel.getUrl()).into(imageView);

        }
    }
}
