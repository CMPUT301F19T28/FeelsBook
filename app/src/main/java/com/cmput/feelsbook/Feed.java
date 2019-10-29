package com.cmput.feelsbook;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.post.Post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Feed extends RecyclerView.Adapter<Feed.ViewHolder> implements Serializable {

    private final String TAG = "Feed";

    private List<Post> feed;

    public Feed(){
        this.feed = new ArrayList<>();
    }

    public Feed(List<Post> feed) {
        this.feed = feed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "called onBindViewHolder");
        feed.get(position).displayPost(holder);
    }
    @Override
    public int getItemCount() {
        return feed.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements Serializable{


        public ViewHolder(final View view) {
            super(view);
        }
    }
}