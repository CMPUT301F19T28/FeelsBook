package com.cmput.feelsbook;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.post.Post;

import java.util.List;

public class Feed extends RecyclerView.Adapter<Feed.ViewHolder> {

    private final String TAG = "Feed";

    private List<Post> feed;
    private User user;

    public Feed(List<Post> feed, User user) {
        this.feed = feed;
        this.user = user;
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
        ImageView profile_pic_feed = holder.itemView.findViewById(R.id.feedList);
        profile_pic_feed.setImageBitmap(user.getProfilePic());
    }

    @Override
    public int getItemCount() {
        return feed.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(final View view) {
            super(view);
        }
    }
}