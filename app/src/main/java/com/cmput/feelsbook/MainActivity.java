package com.cmput.feelsbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.cmput.feelsbook.post.Post;


/**
 * Homepage where a feed of moods/posts will be seen.
 * Comprised of a scrollable RecyclerView
 */
public class MainActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener{
    private ImageButton profileButton;
    RecyclerView feedView;
    User currentUser;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    FeedFragment feedFragment;
    MapFragment mapFragment;
    Feed.OnItemClickListener listener;

    //Location permission vars
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean locationPermisisionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * TO BE IMPLEMENTED:
         * - pass in Feed to be displayed and personalized in ProfileActivity
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermission();
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        profileButton = findViewById(R.id.profileButton);
        listener = new Feed.OnItemClickListener(){
            /**
             * Sets onItemClick to open a fragment in which the mood will be edited
             * @param post
             *          Post to be edited
             */

            @Override
            public void onItemClick(Post post){
                new AddMoodFragment().newInstance(post).show(getSupportFragmentManager(), "EDIT_MOOD");
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
        }
      
        feedFragment = new FeedFragment();
        mapFragment = new MapFragment();
        viewPagerAdapter.AddFragment(feedFragment, "Feed");
        viewPagerAdapter.AddFragment(mapFragment,"Map");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        final FloatingActionButton addPostBttn = findViewById(R.id.addPostButton);
        addPostBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when floating action button is pressed instantiates the fragment so a Ride can be
                // added to the list
                // add post activity:

                new AddMoodFragment().show(getSupportFragmentManager(), "ADD_MOOD");
            }
        });

        feedFragment.getRecyclerAdapter().setOnItemClickListener(listener);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
                feedFragment.getRecyclerAdapter().setOnItemClickListener(null);
                userBundle.putSerializable("Post_list",feedFragment.getRecyclerAdapter());
                intent.putExtras(userBundle);
                startActivity(intent);
            }
        });
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermisisionsGranted = true;
            }
            else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermisisionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermisisionsGranted = false;
                            return;
                        }
                    }
                    locationPermisisionsGranted = true;

                }
            }
        }
    }
    /**
     * Takes a mood from the implemented fragment and adds it to the feedAdapter
     * @param newMood
     *          mood that will be added to the feed
     */
    public void onSubmit(Post newMood){
        feedFragment.getRecyclerAdapter().addPost(newMood);
        feedFragment.getRecyclerAdapter().notifyDataSetChanged();
    }

    /**
     * notifies the adapter that the data set has changed
     */
    public void edited(){
        //Code for editing mood
        feedFragment.getRecyclerAdapter().notifyDataSetChanged();
    }

    /**
     * will be used to delete passed in mood once implemented
     * @param mood
     *      mood to be deleted
     */
    public void deleted(Post mood){
        //For deleting mood
        feedFragment.getRecyclerAdapter().removePost(mood);
        feedFragment.getRecyclerAdapter().notifyDataSetChanged();
    }
}
