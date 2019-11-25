package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Homepage where a feed of moods/posts will be seen.
 * Comprised of a scrollable RecyclerView
 */
public class MainActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener,
        FilterFragment.OnMoodSelectListener{
    private ImageButton profileButton;
    private User currentUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private FeedFragment feedFragment;
    private MapFragment mapFragment;
    private Feed.OnItemClickListener listener;
    private FirebaseFirestore db;
    private CollectionReference cr;
    private ArrayList<Post> feedCopy;
    private ArrayList<MoodType> filteredMoods;
    private FilterFragment filter;
    private boolean filterClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        profileButton = findViewById(R.id.profile_button);
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

        //Sets the document to that of the current user
        cr = db.collection("users").document(currentUser.getUserName())
                .collection("Moods");
        feedFragment = new FeedFragment();
        mapFragment = new MapFragment();
        filteredMoods = new ArrayList<>();
        viewPagerAdapter.AddFragment(feedFragment, "Feed");
        viewPagerAdapter.AddFragment(mapFragment,"Map");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        final FloatingActionButton addPostBttn = findViewById(R.id.addPostButton);
        addPostBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creates a new add mood activity
                new AddMoodFragment().show(getSupportFragmentManager(), "ADD_MOOD");
            }
        });

        final ImageButton filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // creates filter window
                filterClicked = true;
                filter = new FilterFragment();
                filter.show(getSupportFragmentManager(),"MAIN_FILTER");
            }
        });

        feedFragment.getRecyclerAdapter().setOnItemClickListener(listener);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterClicked){
                    // reset filtered feed if filter was clicked at least once
                    filter.resetFilterButtons();
                    updateFeed();
                }
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
                intent.putExtras(userBundle);
                startActivity(intent);
            }
        });

        updateFeed();

    }

    /**
     * takes a mood and puts it in the database. If the mood is new will create a new mood in the
     * database else will edit the mood in the database with the new parameters
     * @param newMood
     *      New mood object to be added or edited
     */
    public void onSubmit(Post newMood){

        HashMap<String, Object> data = new HashMap<>();

        /*
        If the newMood contains a photo will convert it into a Base64 String to be stored in the
        database if no photo is present sets the field to null
         */
        try {
            //puts photo into hashmap
            Bitmap bitmap = ((Mood) newMood).getPhoto();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] picData = baos.toByteArray();
            data.put("photo", Base64.getEncoder().encodeToString(picData));
        }catch (Exception e) {
            Log.d("-----UPLOAD PHOTO-----",
                    "****NO PHOTO UPLOADED: " + e);
            data.put("photo", null);
        }

        /*
        If the newMood contains a profilePic will convert it into a Base64 String to be stored in the
        database if no profilePic is present sets the field to null
         */
        try {
            //puts profilePic into hashmap
            Bitmap bitmap = ((Mood) newMood).getProfilePic();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] picData = baos.toByteArray();
            data.put("profilePic", Base64.getEncoder().encodeToString(picData));
        }catch (Exception e) {
            Log.d("-----UPLOAD PHOTO-----",
                    "****NO profilepic UPLOADED: " + e);
            data.put("profilePic", null);
        }

        /*
        puts the other parameters into the hashmap to be sent to the database
         */
        data.put("datetime", newMood.getDateTime());
        data.put("location", ((Mood) newMood).getLocation());
        data.put("reason", ((Mood) newMood).getReason());
        data.put("situation", ((Mood) newMood).getSituation());
        data.put("moodType", ((Mood) newMood).getMoodType());

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
     * will be used to delete passed in mood once implemented
     * @param mood
     *      mood to be deleted
     */
    public void deleted(Post mood){
        Toast.makeText(MainActivity.this, "Mood Deleted", Toast.LENGTH_SHORT).show();
        //For deleting mood
        cr
                .document(mood.toString())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("--DELETE OPERATION---: ",
                                "Data removal successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("--DELETE OPERATION---: ",
                                "Data removal failed" + e.toString());
                    }
                });
//        feedFragment.getRecyclerAdapter().removePost(mood);
        feedFragment.getRecyclerAdapter().notifyDataSetChanged();
    }
    /**
     * Listens for updates the the database and updates the recyclerView when updates
     */
    public void updateFeed(){
        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                            /*
                            converts the photo is present converts from a base64 string to a byte[]
                            and then into a bitmap if no photo is present sets photo to null
                             */
                            try {
                                byte[] decoded = Base64.getDecoder()
                                        .decode((String)  doc.get("photo"));
                                photo = BitmapFactory.decodeByteArray(decoded
                                        , 0, decoded.length);
                            }catch(Exception error) {
                                Log.d("-----UPLOAD PHOTO-----",
                                        "****NO PHOTO DOWNLOADED: " + e);
                                photo = null;
                            }
                        }

                        if (doc.contains("profilePic")) {

                            /*
                            converts the profilePic is present converts from a base64 string to a byte[]
                            and then into a bitmap if no photo is present sets profilePic to null
                             */
                            try {
                                byte[] decoded = Base64.getDecoder()
                                        .decode((String)  doc.get("profilePic"));
                                profilePic = BitmapFactory.decodeByteArray(decoded
                                        , 0, decoded.length);
                            }catch(Exception error) {
                                Log.d("-----UPLOAD PHOTO-----",
                                        "****NO PHOTO DOWNLOADED: " + e);
                                profilePic = null;
                            }
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
                feedCopy = new ArrayList<>(feedFragment.getRecyclerAdapter().getFeed());
            }
        });
    }

    /**
     * Handles when a filter button is pressed.
     * Note that when a filter button is pressed, this means that all moods EXCEPT the currently
     * pressed mood/s will be shown in the feed.
     * @param moodType - the MoodType to be filtered
     */
    public void onSelect(MoodType moodType){
        filteredMoods.add(moodType);
        Log.d("Filter","(SELECT-Main)Current filtered mood size: "+filteredMoods.size());
        Iterator<Post> it = feedCopy.iterator();
        ArrayList<Post> result = new ArrayList<>();
        while (it.hasNext()){
            Mood m = (Mood)it.next();
            if (filteredMoods.contains(m.getMoodType())){
                result.add(m);
            }
        }
        feedFragment.getRecyclerAdapter().setFeed(result);
        feedFragment.getRecyclerAdapter().notifyDataSetChanged();
    }

    /**
     * Handles when a filter button is unpressed.
     * When a filter button is unpressed, all moods that are currently unpressed will be hidden
     * in the feed. If there is one mood left to be unpressed, when that same mood is unpressed,
     * the feed will be restored to show all moods.
     * @param moodType - the MoodType to be unfiltered.
     */
    public void onDeselect(MoodType moodType){
        filteredMoods.remove(moodType);
        Log.d("Filter","(DESELECT-Main)Current filtered mood size: "+filteredMoods.size());
        if (filteredMoods.size() > 0){
            Iterator<Post> it = feedCopy.iterator();
            ArrayList<Post> result = new ArrayList<>();
            while (it.hasNext()){
                Mood m = (Mood)it.next();
                if (filteredMoods.contains(m.getMoodType())){
                    result.add(m);
                }
            }
            feedFragment.getRecyclerAdapter().setFeed(result);
            feedFragment.getRecyclerAdapter().notifyDataSetChanged();
        }
        else {
            updateFeed();
        }
    }
}