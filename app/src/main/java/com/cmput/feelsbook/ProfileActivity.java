package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.Post;
import com.google.android.material.tabs.TabLayout;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Bundle bundle = getIntent().getExtras();
        tabLayout = findViewById(R.id.profile_tab);
        viewPager = findViewById(R.id.history_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
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

        ImageButton backButton = findViewById(R.id.exit_profile);
        TextView fullName = findViewById(R.id.full_name);
        TextView userName = findViewById(R.id.username);
        TextView followText = findViewById(R.id.follower_count);
        TextView followingText = findViewById(R.id.following_count);
        TextView postsText = findViewById(R.id.total_posts);

        // TO-DO: add profile picture taken from Firebase
        ImageView profilePicture = findViewById(R.id.profile_picture);
        // TO-DO: replace alias with the document name (username) inside of Firebase
        String alias = "testname123";


        // TO-DO: replace with actual count of posts instead of using historyAdapter
        postCount = historyAdapter.getItemCount();
        fullName.setText(currentUser.getName());
        followText.setText(followCount + " following");
        followingText.setText(followersCount + " followers");
        userName.setText("@"+alias);

        if (postCount > 1 || postCount == 0){postsText.setText(postCount + " total posts");}
        else if (postCount == 1){postsText.setText(postCount + " total post");}

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onSubmit (Post newMood){
        historyAdapter.addPost(newMood);
    }
    /**
     * will eventually be used to edit mood
     */
    public void edited () {
        //Code for editing mood
    }
    /**
     * will be used to delete passed in mood once implemented
     * @param delete
     */
    public void deleted (Post delete){
        //For deleting mood
    }

}

