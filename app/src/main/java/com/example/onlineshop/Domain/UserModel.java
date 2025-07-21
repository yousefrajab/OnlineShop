package com.example.onlineshop.Domain;

import java.io.Serializable;

// من الجيد جعل الـ Model يطبق Serializable إذا كنت ستنقلها بين الأنشطة
public class UserModel implements Serializable {

    private String name;
    private String email;
    private String uid;

    // ▼▼▼ هذا هو الحقل الجديد الذي كان ناقصًا ▼▼▼
    private String profileImageUrl;

    // Constructor فارغ ضروري لـ Firebase
    public UserModel() {
    }

    // Constructor كامل لسهولة الاستخدام
    public UserModel(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    // --- Getters and Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // ▼▼▼ هذه هي الدوال الجديدة التي كانت ناقصة ▼▼▼
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}