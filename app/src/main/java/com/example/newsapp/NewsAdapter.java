package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

@SuppressWarnings("unused")
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(NewsItem item);
    }

    private final List<NewsItem> newsItems;
    private final OnItemClickListener listener;

    public NewsAdapter(List<NewsItem> newsItems, OnItemClickListener listener) {
        this.newsItems = newsItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = newsItems.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;
        final TextView descriptionTextView;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(android.R.id.text1);
            descriptionTextView = itemView.findViewById(android.R.id.text2);
        }
    }
}

