package com.example.onlineshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshop.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. تهيئة Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. إعداد الأزرار
        setVariable();
    }

    private void setVariable() {
        // عند الضغط على زر "Login"
        binding.loginBtn.setOnClickListener(v -> {
            String email = binding.emailEdt.getText().toString().trim();
            String password = binding.passwordEdt.getText().toString().trim();

            // التحقق من أن الحقول ليست فارغة
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // إظهار شريط التقدم
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.loginBtn.setVisibility(View.GONE);

            // 3. تسجيل دخول المستخدم باستخدام Firebase Auth
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // نجحت عملية تسجيل الدخول، انتقل إلى الشاشة الرئيسية
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish(); // إغلاق هذه الشاشة
                            } else {
                                // فشلت عملية تسجيل الدخول
                                Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                // إعادة إظهار الزر وإخفاء شريط التقدم
                                binding.progressBar.setVisibility(View.GONE);
                                binding.loginBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        });

        // للانتقال إلى شاشة إنشاء حساب جديد
        binding.registerTxt.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}