package com.cmput.feelsbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.cmput.feelsbook.post.Post;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener{
    private int followCount = 0;
    private int followersCount = 0;
    private int postCount = 0;
    private ImageView profilePicture;
    private User currentUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Feed historyAdapter;
    private FeedFragment historyFragment;
    private MapFragment mapFragment;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Bundle bundle = getIntent().getExtras();
        tabLayout = findViewById(R.id.profile_tab);
        viewPager = findViewById(R.id.history_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        db = FirebaseFirestore.getInstance();

        if (bundle != null){
            currentUser = (User)bundle.get("User");
            historyAdapter = (Feed)bundle.get("Post_list");
        }

        historyFragment = new FeedFragment(historyAdapter);
        mapFragment = new MapFragment();
        viewPagerAdapter.AddFragment(historyFragment, "History");
        viewPagerAdapter.AddFragment(mapFragment,"Map");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Button backButton = findViewById(R.id.exit_profile);
        TextView fullName = findViewById(R.id.full_name);
        TextView userName = findViewById(R.id.username);
        TextView followText = findViewById(R.id.follower_count);
        TextView followingText = findViewById(R.id.following_count);
        TextView postsText = findViewById(R.id.total_posts);
        ImageView profilePicture = findViewById(R.id.profile_picture);

        postCount = historyAdapter.getItemCount();
        fullName.setText(currentUser.getName());
        followText.setText(followCount + " following");
        followingText.setText(followersCount + " followers");
        userName.setText("@"+currentUser.getUserName());

        if (postCount > 1 || postCount == 0){postsText.setText(postCount + " total posts");}
        else if (postCount == 1){postsText.setText(postCount + " total post");}

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Takes a mood from the implemented fragment and adds it to the feedAdapter
     * @param newMood
     *          mood that will be added to the feed
     */
    public void onSubmit(Post newMood){
        historyAdapter.addPost(newMood);
        historyAdapter.notifyDataSetChanged();
    }

    /**
     * notifies the adapter that the data set has changed
     */
    public void edited(){
        //Code for editing mood
        historyAdapter.notifyDataSetChanged();
    }

    /**
     * will be used to delete passed in mood once implemented
     * @param mood
     *      mood to be deleted
     */
    public void deleted(Post mood){
        //For deleting mood
        historyAdapter.removePost(mood);
        historyAdapter.notifyDataSetChanged();
    }

    /**
     * Launches follower list / following list activity
     * @param v
     */
    public void showFollow(View v){
        Intent intent = new Intent(ProfileActivity.this,FollowActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user",currentUser);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}

