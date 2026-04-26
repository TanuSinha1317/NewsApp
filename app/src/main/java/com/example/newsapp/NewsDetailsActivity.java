package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewsDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESCRIPTION = "extra_description";

    public static Intent newIntent(Context context, String title, String description) {
        Intent intent = new Intent(context, NewsDetailsActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);

        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);

        if (title == null || title.trim().isEmpty()) {
            title = getString(R.string.news_detail_default_title);
        }
        if (description == null || description.trim().isEmpty()) {
            description = getString(R.string.news_detail_default_description);
        }

        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }
}

