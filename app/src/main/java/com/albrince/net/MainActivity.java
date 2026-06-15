package com.albrince.net;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String GITHUB_RELEASE_URL = "https://github.com/ibra77353/albrince-net/releases/latest";

    private final ActivityResultLauncher<Intent> installPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!getPackageManager().canRequestPackageInstalls()) {
                        Toast.makeText(this, "يلزم قبول صلاحية التثبيت للتحديث التلقائي", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // تم إزالة استدعاء requestInstallPermissionIfNeeded() من هنا
        checkForUpdate(getAppVersion());

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

        Button btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "📱 تطبيق البرنس نت الإصدار " + getAppVersion() + "\n" +
                            "لتحميل التطبيق: " + GITHUB_RELEASE_URL);
            startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"));
        });

        Button btnAbout = findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(v -> showAboutDialog());
    }

    private String getAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.1";
        }
    }

    private void openWebView(String url, String title) {
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("TITLE", title);
        startActivity(intent);
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("حول التطبيق")
                .setMessage("📱 البرنس نت\n" +
                        "الإصدار " + getAppVersion() + "\n\n" +
                        "تطبيق مشاهدة الاستراحة والبث المباشر\n" +
                        "شبكة البرنس نت\n\n" +
                        "تم التطوير بواسطة:\n" +
                        "فريق البرنس نت التقني\n\n" +
                        "للدعم الفني:\n" +
                        "واتساب: 773537707\n" +
                        "اتصال: 773537707")
                .setPositiveButton("حسناً", null)
                .show();
    }

    private void checkForUpdate(String version) {
        new UpdateChecker(this, version).checkForUpdate();
    }

    // الدالة باقية كما هي، ولكن لم نعد نستدعيها عند onCreate
    private void requestInstallPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent.setData(Uri.parse("package:" + getPackageName()));
                installPermissionLauncher.launch(intent);
            }
        }
    }
}