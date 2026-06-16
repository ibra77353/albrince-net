package com.albrince.net;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // تحميل الوضع المحفوظ قبل عرض أي شيء
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        int savedTheme = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText("الإصدار " + BuildConfig.VERSION_NAME);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1500);
    }
}