package com.example.onlineshop.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemsModel implements Parcelable {
    private String key;
    private String title;
    private String description;
    private String offPercent;
    private ArrayList<String> size;
    private ArrayList<String> color;
    private ArrayList<String> picUrl;
    private double price;
    private double oldPrice;
    private int review;
    private double rating;
    private int NumberinCart;
    private int categoryId;

    public ItemsModel() {}

    // --- Getters and Setters ---
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOffPercent() { return offPercent; }
    public void setOffPercent(String offPercent) { this.offPercent = offPercent; }
    public ArrayList<String> getSize() { return size; }
    public void setSize(ArrayList<String> size) { this.size = size; }
    public ArrayList<String> getColor() { return color; }
    public void setColor(ArrayList<String> color) { this.color = color; }
    public ArrayList<String> getPicUrl() { return picUrl; }
    public void setPicUrl(ArrayList<String> picUrl) { this.picUrl = picUrl; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getOldPrice() { return oldPrice; }
    public void setOldPrice(double oldPrice) { this.oldPrice = oldPrice; }
    public int getReview() { return review; }
    public void setReview(int review) { this.review = review; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getNumberinCart() { return NumberinCart; }
    public void setNumberinCart(int numberinCart) { NumberinCart = numberinCart; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    // --- Parcelable Implementation ---
    protected ItemsModel(Parcel in) {
        key = in.readString();
        title = in.readString();
        description = in.readString();
        offPercent = in.readString();
        size = in.createStringArrayList();
        color = in.createStringArrayList();
        picUrl = in.createStringArrayList();
        price = in.readDouble();
        oldPrice = in.readDouble();
        review = in.readInt();
        rating = in.readDouble();
        NumberinCart = in.readInt();
        categoryId = in.readInt();
    }

    public static final Creator<ItemsModel> CREATOR = new Creator<ItemsModel>() {
        @Override
        public ItemsModel createFromParcel(Parcel in) { return new ItemsModel(in); }
        @Override
        public ItemsModel[] newArray(int size) { return new ItemsModel[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(offPercent);
        dest.writeStringList(size);
        dest.writeStringList(color);
        dest.writeStringList(picUrl);
        dest.writeDouble(price);
        dest.writeDouble(oldPrice);
        dest.writeInt(review);
        dest.writeDouble(rating);
        dest.writeInt(NumberinCart);
        dest.writeInt(categoryId);
    }
}