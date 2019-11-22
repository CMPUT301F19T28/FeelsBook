package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Fragment that holds the user's following list
 * Feed followList - contains the following list to be displayed
 * List<User> following - list of other users the current user is following
 */
public class FollowList extends Fragment implements Serializable {
    private RecyclerView recView;
    private Feed followList;
    private List<User> following;

    public FollowList(){
        followList = new Feed();
    }

    public FollowList(Feed feed, List<User> following){
        this.followList = feed;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follow_activity, container, false);
        recView = view.findViewById(R.id.following_list);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recView.setAdapter(followList);
        return view;
    }
}
