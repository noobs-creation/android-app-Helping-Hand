package com.example.helpinghand;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class NewsFeedViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView textView_details;
    public NewsFeedViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.image_news_feed);
        textView_details = itemView.findViewById(R.id.details_news_feed);

    }
}
