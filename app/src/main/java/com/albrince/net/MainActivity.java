package com.albrince.net;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private static final String GITHUB_RELEASE_URL = "https://github.com/ibra77353/albrince-net/releases/latest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // تحميل الوضع المحفوظ قبل عرض أي شيء
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        int savedTheme = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (NetworkUtils.isNetworkAvailable(this)) {
            checkForUpdateSilent();
        }

        Button btnLoginPage = findViewById(R.id.btnLoginPage);
        btnLoginPage.setOnClickListener(v -> openWebView("http://b.net", "صفحة الدخول"));

        Button btnLiveStream = findViewById(R.id.btnLiveStream);
        btnLiveStream.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://10.10.10.10"));
            startActivity(browserIntent);
        });

        Button btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnWhatsApp.setOnClickListener(v -> {
            Intent waIntent = new Intent(Intent.ACTION_VIEW);
            waIntent.setData(Uri.parse("https://wa.me/967773537707?text=مرحبا"));
            startActivity(waIntent);
        });

        Button btnCall = findViewById(R.id.btnCall);
        btnCall.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:773537707"));
            startActivity(dialIntent);
        });

        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void checkForUpdateSilent() {
        new UpdateChecker(this, BuildConfig.VERSION_NAME).checkForUpdateSilent();
    }

    private void openWebView(String url, String title) {
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("TITLE", title);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("خروج")
                .setMessage("هل تريد الخروج من التطبيق؟")
                .setPositiveButton("نعم", (dialog, which) -> finish())
                .setNegativeButton("إلغاء", null)
                .show();
    }
}