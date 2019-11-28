package com.cmput.feelsbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import io.opencensus.tags.Tag;

/**
 * Handles the profile activities and displays the user profile information.
 * int followCount, followersCount, postCount - displays number of following users, followers, and
 * number of posts respectively
 * ImageView profilePicture - displays user's profile picture
 * User currentUser - current user whose profile is displayed
 * Feed historyAdapter - contains user's personal posts
 * TabLayout tabLayout - used to display clickable tab items
 * ViewPager viewPager - contains the area where MapFragment and FeedFragment is displayed
 * MapFragment mapFragment - contains the map activity to be displayed
 * FeedFragment feedFragment - contains the feed activity to be displayed
 * FirebaseFirestore db - created instance of the database where data is being pulled from
 */
public class ProfileActivity extends AppCompatActivity{
    private int followCount;
    private int followersCount;
    private int postCount;
    private ImageView profilePicture;
    private User currentUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private FeedFragment historyFragment;
    private MapFragment mapFragment;
    private FirebaseFirestore db;
    private CollectionReference MoodCollection;
    private Feed.OnItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Bundle bundle = getIntent().getExtras();
        tabLayout = findViewById(R.id.profile_tab);
        viewPager = findViewById(R.id.history_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        db = FirebaseFirestore.getInstance();

        postCount = 0;


        if (bundle != null){
            currentUser = (User)bundle.get("User");
        }

        listener = new Feed.OnItemClickListener(){
            /**
             * Sets onItemClick to open a fragment in which the mood will be edited
             * @param post
             *          Post to be edited
             */

            @Override
            public void onItemClick(Post post){
                Intent intent = new Intent(getApplicationContext(), AddMoodActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
                userBundle.putBoolean("editMood", true);
                userBundle.putSerializable("Mood", post);
                intent.putExtras(userBundle);
                startActivityForResult(intent, 1);
            }
        };

        //Sets the document to that of the current user
        MoodCollection = db.collection("users").document(currentUser.getUserName())
                .collection("Moods");

        historyFragment = new FeedFragment();
        historyFragment.getRecyclerAdapter().setOnItemClickListener(listener);
        mapFragment = new MapFragment();
        viewPagerAdapter.AddFragment(historyFragment, "History");
        viewPagerAdapter.AddFragment(mapFragment,"Map");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Button backButton = findViewById(R.id.exit_profile);
        TextView fullName = findViewById(R.id.full_name);
        TextView userName = findViewById(R.id.username);
        TextView postsText = findViewById(R.id.total_posts);
        ImageView profilePicture = findViewById(R.id.profile_picture);

        postCount = historyFragment.getRecyclerAdapter().getItemCount();
        fullName.setText(currentUser.getName());
        userName.setText("@"+currentUser.getUserName());


        updateFeed();
        postCount = historyFragment.getRecyclerAdapter().getItemCount();

        postsText.setText(postCount + " total post");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView followersText = findViewById(R.id.follower_count);
        TextView followingText = findViewById(R.id.following_count);
        db.collection("users").document(currentUser.getUserName()).collection("following").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                followCount = queryDocumentSnapshots.size();
                followingText.setText(followCount + " following");
            }
        });
        db.collection("users").document(currentUser.getUserName()).collection("followers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                followersCount = queryDocumentSnapshots.size();
                followersText.setText(followersCount + " followers");
            }
        });
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

    /**
     * This method updates the FeedFragment whenever the remote database is updated
     */
    private void updateFeed(){
        MoodCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {

            //clears list
            while( historyFragment.getRecyclerAdapter().getItemCount() > 0) {
                historyFragment.getRecyclerAdapter().removePost(0);
                historyFragment.getRecyclerAdapter().notifyItemRemoved(0);
            }
            for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                if(doc.exists())
                    historyFragment.getRecyclerAdapter().addPost(doc.toObject(Mood.class));
            }

            historyFragment.getRecyclerAdapter().notifyDataSetChanged();
        });


    }
}

