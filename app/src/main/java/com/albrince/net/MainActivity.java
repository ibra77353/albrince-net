package com.albrince.net;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String APP_VERSION = "1.1";
    private static final String GITHUB_RELEASE_URL = "https://github.com/your-username/albrince-net/releases/latest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // زر صفحة الدخول
        Button btnLoginPage = findViewById(R.id.btnLoginPage);
        btnLoginPage.setOnClickListener(v -> {
            openWebView("http://b.net", "صفحة الدخول");
        });

        // زر البث المباشر
        Button btnLiveStream = findViewById(R.id.btnLiveStream);
        btnLiveStream.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://10.10.10.10"));
            startActivity(intent);
        });

        // زر واتساب
        Button btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnWhatsApp.setOnClickListener(v -> {
            Intent waIntent = new Intent(Intent.ACTION_VIEW);
            waIntent.setData(Uri.parse("https://wa.me/967773537707?text=مرحبا"));
            startActivity(waIntent);
        });

        // زر اتصال
        Button btnCall = findViewById(R.id.btnCall);
        btnCall.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:773537707"));
            startActivity(dialIntent);
        });

        // زر مشاركة التطبيق
        Button btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, 
                "📱 تطبيق البرنس نت الإصدار " + APP_VERSION + "\n" +
                "لتحميل التطبيق: " + GITHUB_RELEASE_URL);
            startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"));
        });

        // زر حول التطبيق
        Button btnAbout = findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(v -> {
            showAboutDialog();
        });
    }

    private void openWebView(String url, String title) {
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("TITLE", title);
        startActivity(intent);
    }

    private void showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("حول التطبيق");
        builder.setMessage(
            "📱 البرنس نت\n" +
            "الإصدار " + APP_VERSION + "\n\n" +
            "تطبيق مشاهدة الاستراحة والبث المباشر\n" +
            "شبكة البرنس نت\n\n" +
            "تم التطوير بواسطة:\n" +
            "فريق البرنس نت التقني\n\n" +
            "للدعم الفني:\n" +
            "واتساب: 773537707\n" +
            "اتصال: 773537707"
        );
        builder.setPositiveButton("حسناً", null);
        builder.show();
    }
}