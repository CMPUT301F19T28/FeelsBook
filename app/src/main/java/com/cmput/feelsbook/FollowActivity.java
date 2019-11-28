package com.cmput.feelsbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

/**
 * Handles the display of followers/following list as well as follow requests.
 * User user - current user used to display information
 * RecyclerView follow_requests - list of follow requests to be displayed
 * FollowingRequests followingRequests - adapter used to populate list of following requests
 * from the user
 */
public class FollowActivity extends AppCompatActivity {

    private User user;
    private FollowFragment followFragment;
    private FollowersFragment followersFragment;
    private FollowingFragment followingFragment;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_activity);
        user = (User) getIntent().getExtras().get("user");

        followFragment = new FollowFragment(user);
        tabLayout = findViewById(R.id.follow_tabs);
        viewPager = findViewById(R.id.followPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        followFragment = new FollowFragment(user);
        followersFragment = new FollowersFragment(user);
        followingFragment = new FollowingFragment(user);
        viewPagerAdapter.AddFragment(followFragment,"Follow Requests");
        viewPagerAdapter.AddFragment(followersFragment, "Followers");
        viewPagerAdapter.AddFragment(followingFragment, "Following");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


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
                Intent intent = new Intent(FollowActivity.this, SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
