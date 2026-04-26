package com.example.newsapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int COUNTDOWN_START_SECONDS = 10;

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

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(newsItems, this::showNewsOptionsDialog);
        newsRecyclerView.setAdapter(newsAdapter);

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

    private void reloadNewsData() {
        int previousSize = newsItems.size();
        addSampleNews();

        if (previousSize == 0) {
            newsAdapter.notifyItemRangeInserted(0, newsItems.size());
        } else {
            newsAdapter.notifyItemRangeChanged(0, newsItems.size());
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