package com.example.onlineshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshop.R; // تأكد من استيراد هذا
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int SPLASH_DELAY = 2000; // 2 ثانية

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // لتوفير تجربة بصرية متسقة مع باقي التطبيق
        EdgeToEdge.enable(this);

        // عرض واجهة البداية الرسومية
        setContentView(R.layout.activity_splash);

        // تهيئة Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // استخدام Handler لتأخير الانتقال والتحقق من حالة تسجيل الدخول
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // التحقق مما إذا كان هناك مستخدم مسجل دخوله حالياً
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                // المستخدم مسجل دخوله، انتقل إلى الشاشة الرئيسية
                goToActivity(MainActivity.class);
            } else {
                // لا يوجد مستخدم، انتقل إلى شاشة تسجيل الدخول
                // ملاحظة: سننشئ LoginActivity في الخطوة التالية
                goToActivity(LoginActivity.class);
            }

        }, SPLASH_DELAY);
    }

    /**
     * دالة مساعدة للانتقال بين الشاشات وإغلاق الشاشة الحالية.
     * @param activityClass الـ Activity التي سيتم الانتقال إليها.
     */
    private void goToActivity(Class<?> activityClass) {
        Intent intent = new Intent(SplashActivity.this, activityClass);
        startActivity(intent);
        finish(); // إغلاق SplashActivity لمنع الرجوع إليها
    }
}