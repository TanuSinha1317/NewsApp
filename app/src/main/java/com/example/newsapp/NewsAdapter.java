package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

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
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = newsItems.get(position);
        Glide.with(holder.itemView)
                .load(item.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.itemImageView);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        final ImageView itemImageView;
        final TextView titleTextView;
        final TextView descriptionTextView;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            titleTextView = itemView.findViewById(R.id.itemTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.itemDescriptionTextView);
        }
    }
}

