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
 * * Creates a small window used to view users that an individual user tracks
 */

public class FollowingFragment extends Fragment {

    private RecyclerView recyclerView;
    private Following recyclerAdapter;

    public FollowingFragment(Following adapter) {
        this.recyclerAdapter = adapter;
    }

    public FollowingFragment(User user){
        // default constructor
        recyclerAdapter = new Following(user);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        recyclerView = view.findViewById(R.id.feed_list);
        LinearLayoutManager lm = (new LinearLayoutManager(getActivity()));
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(recyclerAdapter);
        return view;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public Following getRecyclerAdapter() {
        return recyclerAdapter;
    }
}
