package com.albrince.net;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // استلام البيانات مع التحقق من null
        String url = getIntent().getStringExtra("URL");
        String title = getIntent().getStringExtra("TITLE");

        // التحقق من صحة الرابط
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "الرابط غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ربط العناصر
        TextView tvTitle = findViewById(R.id.tvTitle);
        Button btnBack = findViewById(R.id.btnBack);
        webView = findViewById(R.id.webView);

        // تعيين العنوان
        tvTitle.setText(title != null ? title : "صفحة الدخول");

        // إعداد WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // إعداد WebViewClient (الطريقة الحديثة)
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        // تحميل الرابط
        webView.loadUrl(url);

        // زر العودة
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}