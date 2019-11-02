package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.post.Post;

import java.util.List;

import javax.annotation.Nullable;

public class FeedFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private Feed recyclerAdapter;

    public FeedFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.feed_fragment, container, false);
        recyclerView = view.findViewById(R.id.feedList);
        recyclerAdapter = new Feed();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);
        return view;
    }

    public Feed getRecyclerAdapter() {
        return recyclerAdapter;
    }
}
