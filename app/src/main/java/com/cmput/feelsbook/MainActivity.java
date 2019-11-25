package com.cmput.feelsbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.cmput.feelsbook.AddMoodActivity;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.SocialSituation;
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

import java.util.Base64;
import java.util.Date;


/**
 * Homepage where a feed of moods/posts will be seen.
 * Comprised of a scrollable RecyclerView
 */
public class MainActivity extends AppCompatActivity{
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
    CollectionReference MoodCollection;
    DocumentReference UserDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * TO BE IMPLEMENTED:
         * - pass in Feed to be displayed and personalized in ProfileActivity
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        profileButton = findViewById(R.id.profileButton);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
        }

        //Sets the document to that of the current user
        UserDocument = db.collection("users").document(currentUser.getUserName());

        //Sets the collectionReference to that of the current users moods
        MoodCollection = UserDocument.collection("Moods");

        feedFragment = new FeedFragment();
        mapFragment = new MapFragment();
        viewPagerAdapter.AddFragment(feedFragment, "Feed");
        viewPagerAdapter.AddFragment(mapFragment,"Map");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        listener = new Feed.OnItemClickListener(){
            /**
             * Sets onItemClick to open a fragment in which the mood will be edited
             * @param post
             *          Post to be edited
             */
            @Override
            public void onItemClick(Post post){
//                new AddMoodFragment().newInstance(post).show(getSupportFragmentManager(), "EDIT_MOOD");
                Intent intent = new Intent(getApplicationContext(), AddMoodActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
                userBundle.putBoolean("editMood", true);
                Mood editMood = (Mood) post;
                userBundle.putSerializable("Mood", editMood.Serialize(true));
                intent.putExtras(userBundle);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        };
        feedFragment.getRecyclerAdapter().setOnItemClickListener(listener);

        FloatingActionButton addPostBttn = findViewById(R.id.addPostButton);
        addPostBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMoodActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
                userBundle.putBoolean("editMood", false);
                intent.putExtras(userBundle);
                startActivityForResult(intent, 1);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
//                feedFragment.getRecyclerAdapter().setOnItemClickListener(null);
//                userBundle.putSerializable("Post_list",feedFragment.getRecyclerAdapter());
                intent.putExtras(userBundle);
                startActivity(intent);
            }
        });

        updateFeed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode,resultCode,result);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
//                Mood mood = (Mood) result.getExtras().get("Mood");
//                feedFragment.getRecyclerAdapter().addPost(mood);
//                feedFragment.getRecyclerAdapter().notifyDataSetChanged();
            }
        }
    }

//    /**
//     * Adds a post/mood object to the feed list.
//     * @param newMood
//     * New mood object to be added
//     */
//    public void onSubmit(Post newMood){
//        feedFragment.getRecyclerAdapter().addPost(newMood);
//        feedFragment.getRecyclerAdapter().notifyDataSetChanged();
//    }
//
//    /**
//     * notifies the adapter that the data set has changed
//     */
//    public void edited(){
//        //Code for editing mood
//        feedFragment.getRecyclerAdapter().notifyDataSetChanged();
//    }
//
//    /**
//     * will be used to delete passed in mood once implemented
//     * @param mood
//     *      mood to be deleted
//     */
//    public void deleted(Post mood){
//        //For deleting mood
//        feedFragment.getRecyclerAdapter().removePost(mood);
//        feedFragment.getRecyclerAdapter().notifyDataSetChanged();
//    }

    public void updateFeed(){
        MoodCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                //clears list
                while( feedFragment.getRecyclerAdapter().getItemCount() > 0) {
                    feedFragment.getRecyclerAdapter().removePost(0);
                    feedFragment.getRecyclerAdapter().notifyItemRemoved(0);
                }

                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){

                    MoodType moodType = null;
                    String reason = null;
                    SocialSituation situation = null;
                    Bitmap photo = null;
                    Location location = null;
                    Bitmap profilePic = null;
                    Date dateTime = null;


                    try {
                        if (doc.contains("datetime"))
                            dateTime = ((Timestamp) doc.get("datetime")).toDate();

                        if (doc.contains("location"))
                            location = (Location) doc.get("location");

                        if (doc.contains("photo")) {
                            photo = getPhoto((String)  doc.get("photo"));
                        }

                        if (doc.contains("profilePic")) {
                            profilePic = getPhoto((String)  doc.get("profilePic"));
                        }

                        if (doc.contains("reason"))
                            reason = (String) doc.get("reason");

                        if (doc.contains("situation") & (doc.get("situation") != null)) {
                            situation = SocialSituation.getSocialSituation((String) doc.get("situation"));
                        }

                        if (doc.contains("moodType") & (doc.get("moodType") != null)) {
                            moodType = MoodType.getMoodType((String) doc.get("moodType"));
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
                                "****MOOD DOWNLOAD FAILED: " + error);
                    }
                }

                feedFragment.getRecyclerAdapter().notifyDataSetChanged();
            }
        });


    }

    /**
     * Takes in a base64 string and converts it into a bitmap
     * @param photo
     *          photo to be converted in base64 String format format
     * @return
     *      returns bitmap of decoded photo returns null if base64 string was not passed in
     */
    public Bitmap getPhoto(String photo){
        try {
            byte[] decoded = Base64.getDecoder()
                    .decode(photo);
            return BitmapFactory.decodeByteArray(decoded
                    , 0, decoded.length);
        }catch(Exception e){
            Log.d("-----CONVERT PHOTO-----",
                    "****NO PHOTO CONVERTED: " + e);
            return null;
        }
    }
}