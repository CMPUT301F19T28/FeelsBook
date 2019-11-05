package com.cmput.feelsbook;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.post.Post;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Feed extends RecyclerView.Adapter<Feed.ViewHolder> implements Serializable {

    private final String TAG = "Feed";

    private List<Post> feed;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{ //define OnClickListener for when a post is clicked
        void onItemClick(Post post);
    }

    public Feed(OnItemClickListener listener) {

        this.feed = new ArrayList<>();
        this.listener = listener;
    }

    public Feed(List<Post> feed, OnItemClickListener listener) {

        this.feed = feed;
        this.listener = listener;
    }

    public void addPost(Post post) {
        feed.add(post);
    }

    public void removePost(Post post) {
        feed.remove(post);
    }

    public void removePost(int pos) {
        feed.remove(pos);
    }

    public Post getPost(int pos) {
        return feed.get(pos);
    }

    public Serializable getFeed(){ return (Serializable)this.feed; }


    /**
     * Create a view holder of item post layout
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    /**
     * When binding a viewholder to the recycler view calls the post to fill the fields.
     *
     * @param holder   The viewholder which will display the item
     * @param position The position of the item in the data source
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "called onBindViewHolder");
        feed.get(position).displayPost(holder);
        holder.bind(this.feed.get(position), listener);
    }


    @Override
    public int getItemCount() {
        return feed.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(final View view) {
            super(view);
        }

        public void bind(final Post post, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    listener.onItemClick(post);
                }
            });
        }
    }
}