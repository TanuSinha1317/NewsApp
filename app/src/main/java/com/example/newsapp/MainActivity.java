package com.example.newsapp;

import android.content.Intent;
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

    private static final String EXTRA_SKIP_COUNTDOWN = "extra_skip_countdown";
    private static final int COUNTDOWN_START_SECONDS = 3;

    private final List<NewsItem> newsItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView countdownTextView = findViewById(R.id.countdownTextView);
        RecyclerView newsRecyclerView = findViewById(R.id.newsRecyclerView);
        ProgressBar loadingProgressBar = findViewById(R.id.loadingProgressBar);

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        boolean skipCountdown = getIntent().getBooleanExtra(EXTRA_SKIP_COUNTDOWN, false);

        if (skipCountdown) {
            showNewsList(newsRecyclerView, loadingProgressBar, countdownTextView);
        } else {
            startCountdownThenRefresh(newsRecyclerView, loadingProgressBar, countdownTextView);
        }
    }

    private void startCountdownThenRefresh(RecyclerView newsRecyclerView,
                                           ProgressBar loadingProgressBar,
                                           TextView countdownTextView) {
        newsRecyclerView.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);
        countdownTextView.setVisibility(View.VISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());
        final int[] remainingSeconds = {COUNTDOWN_START_SECONDS};

        Runnable countdownRunnable = new Runnable() {
            @Override
            public void run() {
                countdownTextView.setText(getString(R.string.loading_countdown, remainingSeconds[0]));

                if (remainingSeconds[0] == 0) {
                    refreshActivity();
                    return;
                }

                remainingSeconds[0]--;
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(countdownRunnable);
    }

    private void showNewsList(RecyclerView newsRecyclerView,
                              ProgressBar loadingProgressBar,
                              TextView countdownTextView) {
        addSampleNews();

        NewsAdapter adapter = new NewsAdapter(newsItems, this::showNewsOptionsDialog);
        newsRecyclerView.setAdapter(adapter);

        loadingProgressBar.setVisibility(View.GONE);
        countdownTextView.setVisibility(View.GONE);
        newsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void refreshActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_SKIP_COUNTDOWN, true);
        startActivity(intent);
        finish();
    }

    private void addSampleNews() {
        newsItems.add(new NewsItem(
                "Breaking News: Android Java",
                "Show a simple alert dialog with Open and Save options from a RecyclerView item click."
        ));
        newsItems.add(new NewsItem(
                "Local Update",
                "This item opens a detail screen or can be saved from the dialog."
        ));
        newsItems.add(new NewsItem(
                "Tech Story",
                "Tap any news item to see the Open and Save actions."
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