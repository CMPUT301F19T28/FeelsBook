package com.cmput.feelsbook;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.cmput.feelsbook.post.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import static android.content.ContentValues.TAG;


/**
 * Homepage where a feed of moods/posts will be seen.
 * Comprised of a scrollable RecyclerView
 */
public class MainActivity extends AppCompatActivity{
    RecyclerView feedView;
    private User currentUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    FeedFragment feedFragment;
    private MapFragment mapFragment;
    private Feed.OnItemClickListener listener;
    private FirebaseFirestore db;
    private CollectionReference MoodCollection;
    private DocumentReference UserDocument;
    private Boolean locationPermissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        ImageButton profileButton = findViewById(R.id.profileButton);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
            locationPermissionGranted = bundle.getBoolean("locationPermission");
        }

        //Sets the document to that of the current user
        UserDocument = db.collection("users").document(currentUser.getUserName());

        //Sets the collectionReference to that of the current users moods
        MoodCollection = UserDocument.collection("Moods");

        feedFragment = new FeedFragment();
        viewPagerAdapter.AddFragment(feedFragment, "Feed");

        if (locationPermissionGranted) {
            mapFragment = new MapFragment();
            Bundle args = new Bundle();
            args.putSerializable("user", currentUser);
            args.putBoolean("locationPermission", locationPermissionGranted);
            mapFragment.setArguments(args);
            viewPagerAdapter.AddFragment(mapFragment,"Map");
        }

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
                userBundle.putSerializable("Mood", ((Mood) post).Serialize(true));
                intent.putExtras(userBundle);
                startActivityForResult(intent, 1);
            }
        };
        feedFragment.getRecyclerAdapter().setOnItemClickListener(listener);

        FloatingActionButton addPostBttn = findViewById(R.id.addPostButton);
        addPostBttn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddMoodActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putSerializable("User", currentUser);
            userBundle.putBoolean("editMood", false);
            intent.putExtras(userBundle);
            startActivityForResult(intent, 1);
        });

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putSerializable("User", currentUser);
            userBundle.putBoolean("locationPermission", locationPermissionGranted);
            intent.putExtras(userBundle);
            startActivity(intent);
        });

        updateFeed();
    }

    /**
     * This method updates the FeedFragment whenever the remote database is updated
     */
    private void updateFeed(){
        MoodCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {

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
                GeoPoint location = null;
                Bitmap profilePic = null;
                Date dateTime = null;
                String user = "null";


                try {
                    if (doc.contains("datetime"))
                        dateTime = ((Timestamp) doc.get("datetime")).toDate();

                    if (doc.contains("location"))
                        location = (GeoPoint) doc.get("location");

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

                    if(doc.contains("User")){
                        user = (String) doc.get("User");
                    }

                    Mood mood = new Mood(dateTime, moodType, profilePic).withUser(user);

                    if(reason != null)
                        mood = mood.withReason(reason);
                    if(situation != null)
                        mood = mood.withSituation(situation);
                    if(photo != null)
                        mood = mood.withPhoto(photo);
                    if(location != null)
                        mood.withLocation(location);

                    feedFragment.getRecyclerAdapter().addPost(mood);
                    mapFragment.addPost(mood);


                }catch(Exception error){
                    Log.d("-----UPLOAD SAMPLE-----",
                            "****MOOD DOWNLOAD FAILED: " + error);
                }
            }

            feedFragment.getRecyclerAdapter().notifyDataSetChanged();
        });


    }

    /**
     * Takes in a base64 string and converts it into a bitmap
     * @param photo
     *          photo to be converted in base64 String format format
     * @return
     *      returns bitmap of decoded photo returns null if base64 string was not passed in
     */
    private Bitmap getPhoto(String photo){
        try {
            @SuppressLint("NewApi") byte[] decoded = Base64.getDecoder()
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