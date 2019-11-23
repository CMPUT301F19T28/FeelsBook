package com.cmput.feelsbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

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
public class ProfileActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener{
    private int followCount = 0;
    private int followersCount = 0;
    private int postCount;
    private ImageView profilePicture;
    private User currentUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private FeedFragment historyFragment;
    private MapFragment mapFragment;
    private FirebaseFirestore db;
    private CollectionReference cr;
    private Feed.OnItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Bundle bundle = getIntent().getExtras();
        tabLayout = findViewById(R.id.profile_tab);
        viewPager = findViewById(R.id.history_pager);
        //profilePicture = findViewById(R.drawable.);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        db = FirebaseFirestore.getInstance();
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

        if (bundle != null){
            currentUser = (User)bundle.get("User");
        }

        //Sets the document to that of the current user
        cr = db.collection("users").document(currentUser.getUserName())
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
        TextView followText = findViewById(R.id.follower_count);
        TextView followingText = findViewById(R.id.following_count);
        TextView postsText = findViewById(R.id.total_posts);
        ImageView profilePicture = findViewById(R.id.profile_picture);
        fullName.setText(currentUser.getName());
        followText.setText(followCount + " following");
        followingText.setText(followersCount + " followers");
        userName.setText("@"+currentUser.getUserName());

        updateFeed();

        // document reference used to fetch total number of posts field inside of the database
        DocumentReference dr = db.collection("users")
                .document(currentUser.getUserName());

        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        postCount = Integer.valueOf(doc.getString("total_posts"));
                        if (postCount > 1 || postCount ==0){postsText.setText(postCount + " total posts");}
                        else if (postCount == 1){postsText.setText(postCount + " total post");}
                        Log.d("Profile","Total posts retrieved: "+postCount);
                    }
                    else {
                        Log.d("Profile","No document found");
                    }
                }
                else{
                    Log.d("Profile","Document retrieval failed: "+task.getException());
                }
            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final FloatingActionButton profileButton = findViewById(R.id.profile_float_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddMoodFragment().show(getSupportFragmentManager(), "ADD_MOOD");
            }
        });
        final ImageButton filterButton = findViewById(R.id.profile_filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // creates small popup window to filter
                Log.d("message","Clicked filter");
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
        If the newMood contains a profilePic, it will convert it into a Base64 String to be stored
        in the database if no profilePic is present sets the field to null
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
        Puts the other parameters into the hashmap to be sent to the database
         */
        data.put("datetime", newMood.getDateTime());
        data.put("location", ((Mood) newMood).getLocation());
        data.put("reason", ((Mood) newMood).getReason());
        data.put("situation", ((Mood) newMood).getSituation());
        data.put("moodType", ((Mood) newMood).getMoodType());
        cr.document(newMood.toString())
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
     * Will be used to delete passed in mood once implemented
     * @param mood
     * Post mood - mood to be deleted
     */
    public void deleted(Post mood){
        Toast.makeText(ProfileActivity.this, "Mood Deleted", Toast.LENGTH_SHORT).show();
        //For deleting mood
        cr.document(mood.toString())
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
        historyFragment.getRecyclerAdapter().notifyDataSetChanged();
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
     * Listens for updates the the database and updates the recyclerView when updates
     */
    public void updateFeed(){
        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                //clears list
                while( historyFragment.getRecyclerAdapter().getItemCount() > 0) {
                    historyFragment.getRecyclerAdapter().removePost(0);
                    historyFragment.getRecyclerAdapter().notifyItemRemoved(0);
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

                        historyFragment.getRecyclerAdapter().addPost(mood);


                    }catch(Exception error){
                        Log.d("-----UPLOAD SAMPLE-----",
                                "****MOOD DOWNLOAD FAILED: " + error);
                    }
                }
                historyFragment.getRecyclerAdapter().notifyDataSetChanged();
                int postListCount = historyFragment.getRecyclerAdapter().getItemCount();

                // update total number of posts
                HashMap<String,Object> userUpdate = new HashMap<>();
                userUpdate.put("total_posts",String.valueOf(postListCount));
                db.collection("users").document(currentUser.getUserName())
                        .update(userUpdate)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Profile", "Counter update successful");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Profile", "Counter update failed: " + e);
                            }
                        });
            }
        });
    }

}

