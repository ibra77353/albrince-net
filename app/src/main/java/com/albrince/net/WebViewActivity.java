package com.albrince.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // استلام البيانات
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
        Button btnRefresh = findViewById(R.id.btnRefresh);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);

        // تعيين العنوان
        tvTitle.setText(title != null ? title : "صفحة الدخول");

        // زر العودة
        btnBack.setOnClickListener(v -> finish());

        // زر تحديث الصفحة
        btnRefresh.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                tvError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.reload();
            } else {
                showNoInternetMessage();
            }
        });

        // إعداد WebView
        setupWebView(url);
    }

    private void setupWebView(String url) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // التحقق من وجود إنترنت
        if (!isNetworkAvailable()) {
            showNoInternetMessage();
            return;
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
                showErrorMessage();
            }
        });

        webView.loadUrl(url);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void showNoInternetMessage() {
        webView.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText("⚠️ لا يوجد اتصال بالإنترنت\nيرجى التحقق من الشبكة والمحاولة مرة أخرى");
    }

    private void showErrorMessage() {
        webView.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText("❌ حدث خطأ في تحميل الصفحة\nيرجى التحقق من اتصالك بالإنترنت وإعادة المحاولة");
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