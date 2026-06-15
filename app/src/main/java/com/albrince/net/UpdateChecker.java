package com.albrince.net;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateChecker {

    private final Context context;
    private final String currentVersion;
    private String latestVersion;
    private String downloadUrl;
    private AlertDialog progressDialog;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpdateChecker(Context context, String currentVersion) {
        this.context = context;
        this.currentVersion = currentVersion;
    }

    public void checkForUpdate() {
        executor.execute(() -> {
            Integer result = checkUpdateInBackground();
            mainHandler.post(() -> onCheckComplete(result));
        });
    }

    private Integer checkUpdateInBackground() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.github.com/repos/ibra77353/albrince-net/releases/latest");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String json = convertStreamToString(connection.getInputStream());
                JSONObject jsonObject = new JSONObject(json);
                latestVersion = jsonObject.getString("tag_name").replaceAll("^v", "");
                downloadUrl = jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");

                String cleanCurrent = currentVersion.replaceAll("^v", "");
                return compareVersions(latestVersion, cleanCurrent) ? 1 : 0;
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private boolean compareVersions(String latest, String current) {
        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");
        int maxLength = Math.max(latestParts.length, currentParts.length);
        for (int i = 0; i < maxLength; i++) {
            int latestNum = extractNumber(i < latestParts.length ? latestParts[i] : "0");
            int currentNum = extractNumber(i < currentParts.length ? currentParts[i] : "0");
            if (latestNum != currentNum) return latestNum > currentNum;
        }
        return false;
    }

    private int extractNumber(String part) {
        try {
            String numOnly = part.replaceAll("[^0-9]", "");
            return numOnly.isEmpty() ? 0 : Integer.parseInt(numOnly);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void onCheckComplete(Integer result) {
        if (result == 1) {
            new AlertDialog.Builder(context)
                    .setTitle("📱 تحديث متاح")
                    .setMessage("الإصدار " + latestVersion + " متاح!\n\n" +
                            "الإصدار الحالي: " + currentVersion + "\n" +
                            "هل تريد تحديث التطبيق الآن؟")
                    .setPositiveButton("تحديث", (dialog, which) -> downloadAndInstall())
                    .setNegativeButton("ليس الآن", null)
                    .show();
        } else if (result == -1) {
            Toast.makeText(context, "فشل التحقق من التحديث، تأكد من اتصال الإنترنت", Toast.LENGTH_LONG).show();
        }
    }

    private void downloadAndInstall() {
        // حذف أي ملف APK قديم (لنفس التطبيق) قبل تنزيل الجديد
        deleteOldApkFiles();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("جاري التحميل 0%");  // ✅ التعديل 1
        builder.setCancelable(false);
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        builder.setView(progressBar);
        progressDialog = builder.show();

        executor.execute(() -> {
            File file = downloadFileInBackground(progressBar);
            mainHandler.post(() -> {
                // ✅ التعديل 2
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (file != null && file.exists()) {
                    installApk(file);
                } else {
                    Toast.makeText(context, "فشل التحميل، حاول مرة أخرى", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void deleteOldApkFiles() {
        File cacheDir = context.getCacheDir();
        File[] files = cacheDir.listFiles((dir, name) -> name != null && name.startsWith("albrince-net-v") && name.endsWith(".apk"));
        if (files != null) {
            for (File oldApk : files) {
                boolean deleted = oldApk.delete();
                if (deleted) android.util.Log.d("UpdateChecker", "Deleted old APK: " + oldApk.getName());
            }
        }
    }

    private File downloadFileInBackground(ProgressBar progressBar) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(downloadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int fileLength = connection.getContentLength();
            File outputFile = new File(context.getCacheDir(), "albrince-net-v" + latestVersion + ".apk");
            try (InputStream input = connection.getInputStream(); FileOutputStream output = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[4096];
                long total = 0;
                int count;
                int lastProgress = -1;  // ✅ التعديل 3
                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        int progress = (int) (total * 100 / fileLength);
                        if (progress != lastProgress) {  // ✅ التعديل 3
                            lastProgress = progress;
                            mainHandler.post(() -> {
                                progressBar.setProgress(progress);
                                if (progressDialog != null) {
                                    progressDialog.setTitle("جاري التحميل " + progress + "%");
                                }
                            });
                        }
                    }
                    output.write(buffer, 0, count);
                }
            }
            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(file);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}