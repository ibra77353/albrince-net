package com.albrince.net;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // زر صفحة الدخول -> يفتح داخل التطبيق (WebView)
        Button btnLoginPage = findViewById(R.id.btnLoginPage);
        btnLoginPage.setOnClickListener(v -> {
            openWebView("http://b.net", "صفحة الدخول");
        });

        // زر البث المباشر -> يفتح في المتصفح الخارجي
        Button btnLiveStream = findViewById(R.id.btnLiveStream);
        btnLiveStream.setOnClickListener(
                v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://10.10.10.10"));
                    startActivity(intent);
                });

        // زر واتساب
        Button btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnWhatsApp.setOnClickListener(v -> {
            Intent waIntent = new Intent(Intent.ACTION_VIEW);
            waIntent.setData(Uri.parse("https://wa.me/967773537707?text=مرحبا")); // غيّر الرقم
            startActivity(waIntent);
        });

        // زر اتصال
        Button btnCall = findViewById(R.id.btnCall);
        btnCall.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:773537707")); // غيّر الرقم
            startActivity(dialIntent);
        });
    }

    private void openWebView(String url, String title) {
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("TITLE", title);
        startActivity(intent);
    }
}