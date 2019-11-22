package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Handles the display of followers/following list as well as follow requests.
 * User user - current user used to display information
 * RecyclerView follow_requests - list of follow requests to be displayed
 * FollowingRequests followingRequests - adapter used to populate list of following requests
 * from the user
 * FollowList followingList - used to display other users the current user is following
 */
public class FollowActivity extends AppCompatActivity {

    private User user;
    private RecyclerView follow_requests;
    private RecyclerView.LayoutManager layoutManager;
    private FollowingRequests followingRequests;
    private FollowList followingList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_activity);
        if (getIntent().getExtras()!= null){
            user = (User) getIntent().getExtras().get("user");
        }

        followingList = new FollowList();
        follow_requests = findViewById(R.id.follow_requests_list);
        layoutManager = new LinearLayoutManager(this);
        follow_requests.setLayoutManager(layoutManager);
        followingRequests = new FollowingRequests(user);
        follow_requests.setAdapter(followingRequests);

        Button back = findViewById(R.id.follow_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button search = findViewById(R.id.follow_search_button);
        search.setOnClickListener(new View.OnClickListener() {
            // launches a new search activity
            @Override
            public void onClick(View view) {
                SearchFragment.newInstance(user).show(getSupportFragmentManager(), "FriendRequest");
            }
        });
    }
}
