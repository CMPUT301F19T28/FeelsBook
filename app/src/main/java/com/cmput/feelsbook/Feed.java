package com.cmput.feelsbook;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Extends recycler view adapter in which a collection of post objects are displayed
 */

public class Feed extends RecyclerView.Adapter<Feed.ViewHolder> implements Serializable, Filterable {

    private final String TAG = "Feed";

    private List<Post> feedListFiltered;
    private List<Post> feedList;
    private List<MoodType> moods;

    private OnItemClickListener listener;

    public interface OnItemClickListener{ //define OnClickListener for when a post is clicked
        void onItemClick(Post post);
    }

    public Feed() {
        this.feedList = new ArrayList<>();
        this.feedListFiltered = new ArrayList<>();
        this.moods = new ArrayList<>();
        setOnItemClickListener(null);
    }

    public Feed(List<Post> feed) {
        this.feedList = feed;
        this.feedListFiltered = feed;
        this.moods = new ArrayList<>();
        setOnItemClickListener(null);
    }

    public void addPost(Post post) {
        feedList.add((int) feedList.stream().filter(post1 -> post.getDateTime().compareTo(post1.getDateTime()) > 0).count(), post);
        getFilter().filter(null);
    }

    public void removePost(Post post) {
        feedList.remove(post);
        getFilter().filter(null);
    }

    public void removePost(int pos) {
        feedList.remove(pos);
        getFilter().filter(null);
    }

    public Post getPost(int pos) {
        return feedListFiltered.get(pos);
    }

    public void toggleMoodFilter(MoodType moodType) {
        Optional<MoodType> mood = moods.stream().filter(moodType1 -> moodType1.equals(moodType)).findFirst();
        if(mood.isPresent())
            moods.remove(mood.get());
        else
            moods.add(moodType);

    }

    public void clearMoods() {
        moods.clear();
        getFilter().filter(null);
    }

    public List<MoodType> getMoods() {
        return moods;
    }

    public List<Post> getFeedFiltered() {
        return feedListFiltered;
    }

    public List<Post> getFeed(){ return this.feedList; }

    public void setFeed(List<Post> feedList){ this.feedList = feedList;}

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
        feedListFiltered.get(position).displayPost(holder);
        holder.bind(feedListFiltered.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return feedListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                if(moods.isEmpty())
                    feedListFiltered = feedList;
                else {
                    List<Post> filtered = new ArrayList<>();
                    for(Post post : feedList) {
                        if(moods.contains(((Mood) post).getMoodType()))
                            filtered.add(post);
                    }
                    feedListFiltered = filtered;
                }
                FilterResults results = new FilterResults();
                results.values = feedListFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                feedListFiltered = (ArrayList<Post>) filterResults.values;
                notifyDataSetChanged();

            }
        };
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