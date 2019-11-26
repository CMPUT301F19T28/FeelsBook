package com.cmput.feelsbook;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.post.Post;
import java.io.Serializable;
import java.util.ArrayList;

public class Feed extends RecyclerView.Adapter<Feed.ViewHolder> implements Serializable {

    private final String TAG = "Feed";

    private ArrayList<Post> feedList;

    private OnItemClickListener listener;

    public interface OnItemClickListener{ //define OnClickListener for when a post is clicked
        void onItemClick(Post post);
    }

    public Feed() {
        this.feedList = new ArrayList<>();
        setOnItemClickListener(null);
    }

    public Feed(ArrayList<Post> feed) {

        this.feedList = feed;
        setOnItemClickListener(null);
    }

    public void addPost(Post post) {
        feedList.add(post);
    }

    public void removePost(Post post) {
        feedList.remove(post);
    }

    public void removePost(int pos) {
        feedList.remove(pos);
    }

    public Post getPost(int pos) {
        return feedList.get(pos);
    }

    public ArrayList<Post> getFeed(){ return this.feedList; }

    public void setFeed(ArrayList<Post> feedList){ this.feedList = feedList;}

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

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
        return new ViewHolder(view) {};

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
        feedList.get(position).displayPost(holder);
        holder.bind(feedList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  implements Serializable{

        public ViewHolder(final View view) {
            super(view);
        }

        public void bind(final Post post, final Feed.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    listener.onItemClick(post);
                }
            });
        }
    }
}