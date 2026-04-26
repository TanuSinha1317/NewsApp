package com.example.newsapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "news_app_channel";
    private static final int COUNTDOWN_START_SECONDS = 10;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    private static final int NOTIFICATION_ID = 2001;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<NewsItem> newsItems = new ArrayList<>();
    private final Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            countdownTextView.setText(getString(R.string.refreshing_countdown, remainingSeconds));

            if (remainingSeconds == 0) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                reloadNewsData();
                loadingProgressBar.setVisibility(View.GONE);
                remainingSeconds = COUNTDOWN_START_SECONDS;
            } else {
                remainingSeconds--;
            }

            handler.postDelayed(this, 1000);
        }
    };

    private TextView countdownTextView;
    private ProgressBar loadingProgressBar;
    private NewsAdapter newsAdapter;
    private int remainingSeconds = COUNTDOWN_START_SECONDS;
    private int refreshCycle = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countdownTextView = findViewById(R.id.countdownTextView);
        RecyclerView newsRecyclerView = findViewById(R.id.newsRecyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        Button notificationButton = findViewById(R.id.notificationButton);

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(newsItems, this::showNewsOptionsDialog);
        newsRecyclerView.setAdapter(newsAdapter);

        createNotificationChannel();
        notificationButton.setOnClickListener(v -> showNotificationWithPermission());

        countdownTextView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.GONE);

        reloadNewsData();
        handler.post(countdownRunnable);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification();
            } else {
                Toast.makeText(this, getString(R.string.notification_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void reloadNewsData() {
        int previousSize = newsItems.size();
        addSampleNews();

        if (previousSize == 0) {
            newsAdapter.notifyItemRangeInserted(0, newsItems.size());
        } else {
            newsAdapter.notifyItemRangeChanged(0, newsItems.size());
        }
    }

    private void showNotificationWithPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            showNotification();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION
            );
        }
    }

    private void showNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        try {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException ignored) {
            Toast.makeText(this, getString(R.string.notification_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(getString(R.string.notification_channel_description));

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void addSampleNews() {
        newsItems.clear();
        int cycleNumber = refreshCycle++;

        newsItems.add(new NewsItem(
                "Breaking News: Android Java (Update " + cycleNumber + ")",
                "RecyclerView data refreshed for cycle " + cycleNumber + "."
        ));
        newsItems.add(new NewsItem(
                "Local Update (Update " + cycleNumber + ")",
                "Tap any news item to open details or save it during refresh cycle " + cycleNumber + "."
        ));
        newsItems.add(new NewsItem(
                "Tech Story (Update " + cycleNumber + ")",
                "The list reloads every 10 seconds while the countdown updates above it."
        ));
    }

    private void showNewsOptionsDialog(NewsItem item) {
        new AlertDialog.Builder(this)
                .setTitle(item.getTitle())
                .setMessage(item.getDescription())
                .setPositiveButton(getString(R.string.action_open), (dialog, which) ->
                        startActivity(NewsDetailsActivity.newIntent(
                                MainActivity.this,
                                item.getTitle(),
                                item.getDescription()
                        )))
                .setNegativeButton(getString(R.string.action_save), (dialog, which) ->
                        Toast.makeText(MainActivity.this,
                                getString(R.string.saved_message, item.getTitle()),
                                Toast.LENGTH_SHORT).show())
                .show();
    }
}