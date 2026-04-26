package com.example.newsapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openDetailsButton = findViewById(R.id.openDetailsButton);
        openDetailsButton.setOnClickListener(v -> {
            startActivity(NewsDetailsActivity.newIntent(
                    MainActivity.this,
                    "Sample News Title",
                    "This description was passed through the intent and displayed in the details screen."
            ));
        });
    }
}