package com.example.onlineshop.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

// ▼▼▼ تم تغيير Serializable إلى Parcelable لواجهة الأندرويد الأصلية ▼▼▼
public class OrderModel implements Parcelable {
    private String orderId;
    private long date; // لتخزين تاريخ الطلب
    private double totalAmount;
    private ArrayList<ItemsModel> items; // قائمة بالمنتجات في الطلب

    // Constructor فارغ ضروري لـ Firebase
    public OrderModel() {
    }

    public OrderModel(String orderId, long date, double totalAmount, ArrayList<ItemsModel> items) {
        this.orderId = orderId;
        this.date = date;
        this.totalAmount = totalAmount;
        this.items = items;
    }


    // --- Getters and Setters (تبقى كما هي) ---

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ArrayList<ItemsModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemsModel> items) {
        this.items = items;
    }

    // ▼▼▼ كل الكود التالي جديد وهو لتطبيق واجهة Parcelable ▼▼▼

    protected OrderModel(Parcel in) {
        orderId = in.readString();
        date = in.readLong();
        totalAmount = in.readDouble();
        // هذا السطر يتطلب أن يكون ItemsModel هو الآخر Parcelable
        items = in.createTypedArrayList(ItemsModel.CREATOR);
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0; // عادة ما تكون 0
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeLong(date);
        dest.writeDouble(totalAmount);
        dest.writeTypedList(items);
    }
}