package com.cmput.feelsbook;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Homepage where a feed of moods/posts will be seen.
 * Comprised of a scrollable RecyclerView
 */
public class MainActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener{
    private ImageButton profileButton;
    User currentUser;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    FeedFragment feedFragment;
    MapFragment mapFragment;
    Feed.OnItemClickListener listener;
    FirebaseFirestore db;
    CollectionReference cr;
    FirebaseStorage storage;

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

        //Sets the document to that of the current user
        cr = db.collection("users").document(currentUser.getUserName())
                .collection("Moods");
        storage = FirebaseStorage.getInstance();

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

        /**
         * Listens for updates the the database and updates the recyclerView when updates
         */
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

                    HashMap<String,Object> data = (HashMap) doc.getData().get("Mood");
                    try {
                        for (Map.Entry mapElement : data.entrySet()) {
                            String key = (String) mapElement.getKey();

                            if (key.equals("dateTime"))
                                dateTime = ((Timestamp) mapElement.getValue()).toDate();

                            if (key.equals("location"))
                                location = (Location) mapElement.getValue();

                            if (key.equals("photo")) {

                                photo = BitmapFactory.decodeByteArray((byte[]) mapElement.getValue()
                                        , 0, ((byte[]) mapElement.getValue()).length);
                            }

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
     * takes a mood and puts it in the database. If the mood is new will create a new mood in the
     * database else will edit the mood in the database with the new parameters
     * @param newMood
     *      New mood object to be added or edited
     */
    public void onSubmit(Post newMood){

        HashMap<String, Object> data = new HashMap<>();
//        data.put("Mood", newMood);

        //puts image into hashmap
        Bitmap bitmap = ((Mood) newMood).getPhoto();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] picData = baos.toByteArray();

        data.put("datetime", newMood.getDateTime());
        data.put("location", ((Mood) newMood).getLocation());
        data.put("profilePic",  newMood.getProfilePic());
        data.put("reason", ((Mood) newMood).getReason());
        data.put("situation", ((Mood) newMood).getSituation());
        data.put("moodType", ((Mood) newMood).getMoodType());
        data.put("photo", picData);




//        //creates a storage reference
//        StorageReference sRef = storage.getReference();
//        StorageReference picRef = sRef.child("photo.jpg");
//        StorageReference picImageRef = sRef.child("images/photo.jpg");
//
//        UploadTask uploadTask = picRef.putBytes(picData);
////        data.put("photo", baos.toByteArray());

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
        Toast.makeText(MainActivity.this, mood.toString(), Toast.LENGTH_SHORT).show();
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
}