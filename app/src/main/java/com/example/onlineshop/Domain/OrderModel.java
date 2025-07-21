package com.example.onlineshop.Domain;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class OrderModel implements Parcelable {
    private String orderId;
    private long date;
    private double totalAmount;
    private ArrayList<ItemsModel> items;

    // --- الحقول الجديدة ---
    private String userId;
    private String userName;
    private String userProfileImageUrl;

    public OrderModel() {}

    // --- Getters and Setters ---
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public ArrayList<ItemsModel> getItems() { return items; }
    public void setItems(ArrayList<ItemsModel> items) { this.items = items; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserProfileImageUrl() { return userProfileImageUrl; }
    public void setUserProfileImageUrl(String userProfileImageUrl) { this.userProfileImageUrl = userProfileImageUrl; }

    // --- Parcelable Implementation ---
    protected OrderModel(Parcel in) {
        orderId = in.readString();
        date = in.readLong();
        totalAmount = in.readDouble();
        items = in.createTypedArrayList(ItemsModel.CREATOR);
        userId = in.readString();
        userName = in.readString();
        userProfileImageUrl = in.readString();
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) { return new OrderModel(in); }
        @Override
        public OrderModel[] newArray(int size) { return new OrderModel[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeLong(date);
        dest.writeDouble(totalAmount);
        dest.writeTypedList(items);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userProfileImageUrl);
    }
}