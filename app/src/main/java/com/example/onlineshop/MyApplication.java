package com.example.onlineshop;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // تهيئة Cloudinary
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dq8wxstwb"); // استبدل هنا
        config.put("api_key", "516278749937659");       // استبدل هنا
        config.put("api_secret", "Q-ymsDKoIPgxWWXnRCpn2LY7xuE"); // استبدل هنا
        MediaManager.init(this, config);
    }
}