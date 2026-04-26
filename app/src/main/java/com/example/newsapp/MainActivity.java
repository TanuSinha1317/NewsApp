package com.example.newsapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<NewsItem> newsItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addSampleNews();

        NewsAdapter adapter = new NewsAdapter(newsItems, this::showNewsOptionsDialog);
        newsRecyclerView.setAdapter(adapter);
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