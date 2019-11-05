package com.cmput.feelsbook;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.cmput.feelsbook.post.Post;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


/**
 * Homepage where feed of moods/Posts will be seen
 * comprises of a scrollable recyclerView
 */
public class MainActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener{
    private ImageButton profileButton;
    User currentUser;
    RecyclerView feedView;
    Feed feedAdapter;
    RecyclerView.LayoutManager layoutManager;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    FeedFragment feedFragment;
    MapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutManager = new LinearLayoutManager(this);


        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        profileButton = findViewById(R.id.profileButton);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
        }

        feedFragment = new FeedFragment();
        mapFragment = new MapFragment();
        viewPagerAdapter.AddFragment(feedFragment, "Feed");
        viewPagerAdapter.AddFragment(mapFragment, "Map");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        final FloatingActionButton addPostBttn = findViewById(R.id.addPostButton);
        addPostBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When floating action button is pressed instantiates the fragment so a Ride can be
                // added to the list
                //add post activity ;
                new AddMoodFragment().show(getSupportFragmentManager(), "ADD_MOOD");
            }
        });

        feedFragment.getRecyclerAdapter().setOnItemClickListener(new Feed.OnItemClickListener(){
            /**
             * Sets onItemClick to open a fragment in which the mood will be edited
             * @param post
             *          Post to be edited
             */
            @Override
            public void onItemClick(Post post){
                new AddMoodFragment().newInstance(post).show(getSupportFragmentManager(), "EDIT_MOOD");
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * TO-DO:
                 * - convert ProfileFragment to ProfileActivity
                 * - Fragments are meant for Maps
                 * - successfully start Profile
                 */
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                Bundle userBundle = new Bundle();
                feedFragment.getRecyclerAdapter().setOnItemClickListener(null);
                currentUser.setPosts(feedFragment.getRecyclerAdapter());
                userBundle.putSerializable("User", currentUser);
                userBundle.putSerializable("Post_list", feedFragment.getRecyclerAdapter().getFeed());
                intent.putExtras(userBundle);
                startActivity(intent);
            }
        });

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