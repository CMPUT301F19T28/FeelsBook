package com.cmput.feelsbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.cmput.feelsbook.post.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
    FirebaseFirestore db;
    CollectionReference cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * TO BE IMPLEMENTED:
         * - pass in Feed to be displayed and personalized in ProfileActivity
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();


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
        cr = db.collection("users").document(currentUser.getUserName())
                .collection("Moods");

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

        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                for(int i = 0; i < feedFragment.getRecyclerAdapter().)
//                feedFragment.getRecyclerAdapter().;
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
//                    Log.d("Sample", S)
                    MoodType moodType = null;
                    String reason = null;
                    SocialSituation situation = null;
                    Bitmap photo = null;
                    Location location = null;
                    Bitmap profilePic = null;
                    Date dateTime = null;

                    HashMap<String,Object> data = (HashMap) doc.getData().get("Mood");
                    try {
                        for (Map.Entry mapElement : data.entrySet()) {
                            String key = (String) mapElement.getKey();

                            if (key.equals("dateTime"))
                                dateTime = ((Timestamp) mapElement.getValue()).toDate();

                            if (key.equals("location"))
                                location = (Location) mapElement.getValue();

                            if (key.equals("photo"))
                                photo = (Bitmap) mapElement.getValue();

                            if (key.equals("profilePic"))
                                profilePic = (Bitmap) mapElement.getValue();

                            if (key.equals("reason"))
                                reason = (String) mapElement.getValue();

                            if (key.equals("situation") & (mapElement.getValue() != null)) {
                                situation = SocialSituation.getSocialSituation((String) mapElement.getValue());
                            }

                            if (key.equals("moodType") & (mapElement.getValue() != null)) {
                                moodType = MoodType.getMoodType((String) mapElement.getValue());
                            }
                        }
                        Mood mood = new Mood(dateTime, moodType, profilePic);

                        if(reason != null)
                            mood = mood.withReason(reason);
                        if(situation != null)
                            mood = mood.withSituation(situation);
                        if(photo != null)
                            mood = mood.withPhoto(photo);
                        if(location != null)
                            mood.withLocation(location);

                        feedFragment.getRecyclerAdapter().addPost(mood);


                    }catch(Exception error){
                        Log.d("-----UPLOAD SAMPLE-----",
                                "****DATABASE UPLOAD FAILED: " + error);
                    }
                }

                feedFragment.getRecyclerAdapter().notifyDataSetChanged();
            }
        });
    }

    /**
     * Adds a post/mood object to the feed list.
     * @param newMood
     * New mood object to be added
     */
    public void onSubmit(Post newMood){

        HashMap<String, Post> data = new HashMap<>();
        data.put("Mood", newMood);

        cr
                .document(newMood.toString())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Sample", "Data addition successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Sample", "Data addition failed" + e.toString());
                    }
                });

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