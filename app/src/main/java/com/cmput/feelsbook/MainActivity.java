package com.cmput.feelsbook;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Context;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Homepage where a feed of moods/posts will be seen.
 * Comprised of a scrollable RecyclerView
 */
public class MainActivity extends AppCompatActivity implements FilterFragment.OnMoodSelectListener{
    private ImageButton profileButton;
    private User currentUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    protected FeedFragment feedFragment;
    protected MapFragment mapFragment;
    private Feed.OnItemClickListener listener;
    private FirebaseFirestore db;
    private List<Post> feedCopy;
    private List<MoodType> filteredMoods;
    private FilterFragment filter;
    private boolean filterClicked = false;
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
        profileButton = findViewById(R.id.profile_button);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
            locationPermissionGranted = bundle.getBoolean("locationPermission");
        }

        if(currentUser == null){
            throw new AssertionError("User is not set from login.");
        }

        //Sets the document to that of the current user
        UserDocument = db.collection("users").document(currentUser.getUserName());

        //Sets the collectionReference to that of the current users moods
        MoodCollection = UserDocument.collection("Moods");

        feedFragment = new FeedFragment();
        filteredMoods = new ArrayList<>();
        feedCopy = new ArrayList<>();
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

        listener = new Feed.OnItemClickListener() {
            /**
             * Sets onItemClick to open a fragment in which the mood will be edited
             *
             * @param post Post to be edited
             */
            @Override
            public void onItemClick(Post post) {
                Intent intent = new Intent(getApplicationContext(), AddMoodActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
                userBundle.putBoolean("editMood", true);
                userBundle.putSerializable("Mood", post);
                intent.putExtras(userBundle);
                startActivityForResult(intent, 1);
                }
            };

        feedFragment.getRecyclerAdapter().setOnItemClickListener(listener);

        final FloatingActionButton addPostBttn = findViewById(R.id.addPostButton);
        addPostBttn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddMoodActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putSerializable("User", currentUser);
            userBundle.putBoolean("editMood", false);
            intent.putExtras(userBundle);
            startActivityForResult(intent, 1);
        });

        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(view -> {
            if (filterClicked){
                // reset filtered feed if filter was clicked at least once
                filter.resetFilterButtons();
                filteredMoods.clear();
                updateFeed();
            }
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putSerializable("User", currentUser);
            intent.putExtras(userBundle);
            startActivity(intent);
        });

        final ImageButton filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(view -> {
                // before creating a window, clears the toggle button saved states if the
                // filter button is clicked for the first time
                if (!filterClicked) {
                    SharedPreferences prefs = getSharedPreferences("filterKey", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    if (prefs.contains("happy")) { edit.remove("happy").apply();}
                    if (prefs.contains("sad")) { edit.remove("sad").apply(); }
                    if (prefs.contains("angry")) { edit.remove("angry").apply(); }
                    if (prefs.contains("sleepy")) { edit.remove("sleepy").apply(); }
                    if (prefs.contains("annoyed")) { edit.remove("annoyed").apply(); }
                    if (prefs.contains("sexy")) { edit.remove("sexy").apply(); }
                }

                // display filter window
                filter = new FilterFragment();
                filter.show(getSupportFragmentManager(), "MAIN_FILTER");
                filterClicked = true;
            });

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putSerializable("User", currentUser);
            userBundle.putBoolean("locationPermission", locationPermissionGranted);
            intent.putExtras(userBundle);
            startActivity(intent);
        });

        //setLoading()
        getFollowing();
    }

    private void getFollowing() {
        List<FollowUser> following = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUserName())
                .collection("following")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc :task.getResult().getDocuments()) {
                            following.add(doc.toObject(FollowUser.class));
                        }
                        currentUser.setFollowingList(following);
                        updateFeed();
                    }
                });
    }
    /**
     * This method updates the FeedFragment whenever the remote database is updated
     */
    private void updateFeed() {
        List<FollowUser> followingList = currentUser.getFollowingList();
        for (int i = 0; i < followingList.size(); i++) {
            db.collection("mostRecent")
                    .document(followingList.get(i).getUserName())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                DocumentSnapshot doc = task.getResult();
                                feedFragment.getRecyclerAdapter().addPost(doc.toObject(Mood.class));
                            }
                            mapFragment.setFeed(feedFragment.getRecyclerAdapter().getFeed());
                            Log.d("Main","Feed size: "+feedFragment.getRecyclerAdapter().getItemCount());
                            feedFragment.getRecyclerAdapter().notifyDataSetChanged();
                            mapFragment.updateMap();
                            feedCopy = new ArrayList<>(feedFragment.getRecyclerAdapter().getFeed());
                        }
                    });
        }
    }


    /**
     * Handles when a filter button is pressed.
     * Note that when a filter button is pressed, this means that all moods EXCEPT the currently
     * pressed mood/s will be shown in the feed.
     * @param moodType - the MoodType to be filtered
     */
    public void onSelect(MoodType moodType){
        if(feedCopy.size() > 0){
            filteredMoods.add(moodType);
            Log.d("Filter","(SELECT-Main)Current filtered mood size: "+filteredMoods.size());
            Iterator<Post> it = feedCopy.iterator();
            List<Post> result = new ArrayList<>();
            while (it.hasNext()){
                Mood m = (Mood)it.next();
                if (filteredMoods.contains(m.getMoodType())){
                    result.add(m);
                }
            }
            feedFragment.getRecyclerAdapter().setFeed(result);
            feedFragment.getRecyclerAdapter().notifyDataSetChanged();
        }
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
            List<Post> result = new ArrayList<>();
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
            feedFragment.getRecyclerAdapter().setFeed(feedCopy);
            feedFragment.getRecyclerAdapter().notifyDataSetChanged();
        }
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
