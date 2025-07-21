package com.example.onlineshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // استيراد Log للمساعدة في التصحيح
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshop.Domain.UserModel;
import com.example.onlineshop.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference; // استخدام اسم متغير أوضح

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        // الحصول على المرجع الرئيسي لقاعدة البيانات
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setVariable();
    }

    private void setVariable() {
        binding.registerBtn.setOnClickListener(v -> {
            String name = binding.nameEdt.getText().toString().trim();
            String email = binding.emailEdt.getText().toString().trim();
            String password = binding.passwordEdt.getText().toString().trim();

            if (name.isEmpty()) {
                binding.nameEdt.setError("Name is required");
                binding.nameEdt.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                binding.emailEdt.setError("Email is required");
                binding.emailEdt.requestFocus();
                return;
            }
            if (password.length() < 6) {
                binding.passwordEdt.setError("Password must be at least 6 characters");
                binding.passwordEdt.requestFocus();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.registerBtn.setVisibility(View.GONE);

            // إنشاء حساب المستخدم في Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            // إنشاء كائن المستخدم لتخزينه
                            UserModel user = new UserModel(name, email, userId);

                            // تخزين معلومات المستخدم في Realtime Database تحت عقدة "Users"
                            databaseReference.child("Users").child(userId).setValue(user).addOnCompleteListener(dbTask -> {
                                // تم إخفاء شريط التقدم هنا لضمان إخفائه سواء نجحت المهمة أو فشلت
                                binding.progressBar.setVisibility(View.GONE);
                                binding.registerBtn.setVisibility(View.VISIBLE);

                                if (dbTask.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    // الانتقال إلى الشاشة الرئيسية بعد التسجيل الناجح
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // خطأ في كتابة البيانات إلى قاعدة البيانات
                                    Toast.makeText(RegisterActivity.this, "Failed to save user data: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            // خطأ في إنشاء الحساب (مثل بريد إلكتروني موجود بالفعل)
                            Toast.makeText(RegisterActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            binding.progressBar.setVisibility(View.GONE);
                            binding.registerBtn.setVisibility(View.VISIBLE);
                        }
                    });
        });

        binding.loginTxt.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }
}