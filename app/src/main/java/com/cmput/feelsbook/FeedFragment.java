package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import javax.annotation.Nullable;

/**
 * Creates the feed list from the user's own posts and following users' posts.
 * recyclerView - tied to the RecyclerView object to display the feed
 * Feed recyclerAdapter - contains the list of posts to display and other methods
 */
public class FeedFragment extends Fragment{

    private RecyclerView recyclerView;
    private Feed recyclerAdapter;

    public FeedFragment(Feed adapter) {
        this.recyclerAdapter = adapter;
    }

    public FeedFragment(){
        // default constructor
        recyclerAdapter = new Feed();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        recyclerView = view.findViewById(R.id.feed_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);
        return view;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public Feed getRecyclerAdapter() {
        return recyclerAdapter;
    }
}
