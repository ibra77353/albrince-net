package com.albrince.net;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
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

        String url = getIntent().getStringExtra("URL");
        String title = getIntent().getStringExtra("TITLE");

        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "الرابط غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tvTitle);
        Button btnHome = findViewById(R.id.btnHome);
        Button btnRefresh = findViewById(R.id.btnRefresh);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);

        tvTitle.setText(title != null ? title : "صفحة الدخول");
        
        btnHome.setOnClickListener(v -> finish());
        
        btnRefresh.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                tvError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.reload();
            } else {
                showNoInternetMessage();
            }
        });

        setupWebView(url);
    }

    private void setupWebView(String url) {
        WebSettings webSettings = webView.getSettings();
        
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webSettings.setDatabasePath(this.getDir("database", Context.MODE_PRIVATE).getPath());
        }

        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        if (!isNetworkAvailable()) {
            showNoInternetMessage();
            return;
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
                String url = request.getUrl().toString();
                
                // الروابط التي تحتوي على b.net تفتح داخل WebView
                if (url.contains("b.net")) {
                    return false;  // false = تفتح داخل WebView
                }
                
                // جميع الروابط الأخرى (واتساب، روابط خارجية، الخ) تفتح في متصفح خارجي
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(WebViewActivity.this, "لا يوجد تطبيق لفتح هذا الرابط", Toast.LENGTH_SHORT).show();
                }
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
            public void onReceivedError(@NonNull WebView view, @NonNull WebResourceRequest request, @NonNull WebResourceError error) {
                if (request.isForMainFrame()) {
                    progressBar.setVisibility(View.GONE);
                    showErrorMessage();
                }
            }
        });

        webView.loadUrl(url);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            @SuppressWarnings("deprecation")
            android.net.NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
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
        finish();
    }
}