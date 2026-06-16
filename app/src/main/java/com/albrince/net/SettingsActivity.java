package com.albrince.net;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchDarkMode;
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // تحميل الوضع المحفوظ
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        int savedTheme = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        switchDarkMode = findViewById(R.id.switchDarkMode);
        tvVersion = findViewById(R.id.tvVersion);
        
        LinearLayout rowCheckUpdate = findViewById(R.id.rowCheckUpdate);
        LinearLayout rowShareApp = findViewById(R.id.rowShareApp);
        LinearLayout rowAbout = findViewById(R.id.rowAbout);
        LinearLayout rowSupport = findViewById(R.id.rowSupport);

        // تعيين رقم الإصدار
        tvVersion.setText(BuildConfig.VERSION_NAME);

        // تعيين حالة الـ Switch
        boolean isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        switchDarkMode.setChecked(isDarkMode);

        // مستمع الـ Switch
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            setAppTheme(mode);
        });

        // مستمع الصفوف
        rowCheckUpdate.setOnClickListener(v -> checkForUpdate());
        rowShareApp.setOnClickListener(v -> shareApp());
        rowAbout.setOnClickListener(v -> showAboutDialog());
        rowSupport.setOnClickListener(v -> openSupport());
    }

    private void setAppTheme(int nightMode) {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("theme_mode", nightMode);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(nightMode);
        recreate();
    }

    private void checkForUpdate() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            new UpdateChecker(this, BuildConfig.VERSION_NAME).checkForUpdateManual();
        } else {
            Toast.makeText(this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "📱 تطبيق البرنس نت الإصدار " + BuildConfig.VERSION_NAME + "\n" +
                "لتحميل التطبيق: https://github.com/ibra77353/albrince-net/releases/latest");
        startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"));
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("ℹ️ حول التطبيق")
                .setMessage("📱 البرنس نت\n" +
                        "الإصدار " + BuildConfig.VERSION_NAME + "\n\n" +
                        "تطبيق مشاهدة الاستراحة والبث المباشر\n" +
                        "شبكة البرنس نت\n\n" +
                        "تم التطوير بواسطة:\n" +
                        "فريق البرنس نت التقني")
                .setPositiveButton("حسناً", null)
                .show();
    }

    private void openSupport() {
        Intent waIntent = new Intent(Intent.ACTION_VIEW);
        waIntent.setData(Uri.parse("https://wa.me/967773537707?text=مرحباً، أحتاج مساعدة في تطبيق البرنس نت"));
        startActivity(waIntent);
    }
}