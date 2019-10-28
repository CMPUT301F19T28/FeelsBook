package com.cmput.feelsbook;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.post.Post;

<<<<<<< HEAD
=======
import java.util.ArrayList;
>>>>>>> 600051d28804c10d8677ea7f1984eb319ea7cf40
import java.util.List;

public class Feed extends RecyclerView.Adapter<Feed.ViewHolder> {

    private final String TAG = "Feed";

    private List<Post> feed;
<<<<<<< HEAD
    private User user;

    public Feed(List<Post> feed, User user) {
        this.feed = feed;
        this.user = user;
=======

    public Feed(){
        this.feed = new ArrayList<>();
    }

    public Feed(List<Post> feed) {
        this.feed = feed;
>>>>>>> 600051d28804c10d8677ea7f1984eb319ea7cf40
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
<<<<<<< HEAD
        ImageView profile_pic_feed = holder.itemView.findViewById(R.id.feedList);
        profile_pic_feed.setImageBitmap(user.getProfilePic());
    }

=======
    }
>>>>>>> 600051d28804c10d8677ea7f1984eb319ea7cf40
    @Override
    public int getItemCount() {
        return feed.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(final View view) {
            super(view);
        }
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 600051d28804c10d8677ea7f1984eb319ea7cf40
