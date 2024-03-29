package com.cmput.feelsbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.cmput.feelsbook.post.Post;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;


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
    private FilterFragment filter;
    private boolean filterClicked = false;
    private CollectionReference MoodCollection;
    private DocumentReference UserDocument;
    private Boolean locationPermissionGranted;
    private Bitmap bitmapProfilePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        profileButton = findViewById(R.id.profile_button);
        db = FirebaseFirestore.getInstance();
        filter = new FilterFragment();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
            locationPermissionGranted = bundle.getBoolean("locationPermission");
        }

        if(currentUser == null){
            throw new AssertionError("User is not set from login.");
        }

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
             *
             * @param post Post to be edited
             */
            @Override
            public void onItemClick(Post post){
                Intent intent = new Intent(getApplicationContext(), ViewMoodActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
//                userBundle.putBoolean("editMood", true);
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
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putSerializable("User", currentUser);
            userBundle.putBoolean("locationPermission", locationPermissionGranted);
            intent.putExtras(userBundle);
            if(filter.prefs != null) {
                filter.reset();
                feedFragment.getRecyclerAdapter().clearMoods();
                mapFragment.clearMoods();
            }
            startActivity(intent);
        });

        final ImageButton filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(view -> {
                feedFragment.getRecyclerAdapter().getFilter().filter(null);
                filter.show(getSupportFragmentManager(), "MAIN_FILTER");
            });

        // decode profile picture string
        String photo = currentUser.getProfilePic();
        byte[] decodePhoto = Base64.getDecoder().decode(photo);
        bitmapProfilePicture = BitmapFactory.decodeByteArray(decodePhoto, 0, decodePhoto.length);

        if(bitmapProfilePicture != null){
            profileButton.setImageBitmap(Bitmap.createScaledBitmap(bitmapProfilePicture, 80,80,false));
        }


        db.collection("users")
                .document(currentUser.getUserName())
                .collection("following")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if(e == null) {
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (doc.getType()) {
                                case ADDED:
                                    currentUser.addUserToFollowing(doc.getDocument().toObject(FollowUser.class));
                                    db.collection("mostRecent")
                                            .document(doc.getDocument().getId())
                                            .addSnapshotListener((documentSnapshot, e2) -> {
                                                if(documentSnapshot != null && documentSnapshot.exists()) {
                                                    Mood mood = documentSnapshot.toObject(Mood.class);
                                                    if(currentUser.getFollowingList().stream().anyMatch(followUser -> followUser.getUserName().equals(mood.getUser()))) {
                                                        feedFragment.getRecyclerAdapter()
                                                                .getFeed()
                                                                .stream()
                                                                .filter(post -> post.getUser().equals(mood.getUser()))
                                                                .findFirst()
                                                                .ifPresent(post -> feedFragment.getRecyclerAdapter().removePost(post));
                                                        feedFragment.getRecyclerAdapter().addPost(documentSnapshot.toObject(Mood.class));
                                                        feedFragment.getRecyclerAdapter().notifyItemInserted(feedFragment.getRecyclerAdapter().getItemCount() - 1);
                                                        mapFragment.addPost(documentSnapshot.toObject(Mood.class));
                                                        mapFragment.updateMap();
                                                    }
                                                }
                                            });
                                    break;
                                case REMOVED:
                                    currentUser.removeUserFromFollowing(doc.getOldIndex());
                                    feedFragment.getRecyclerAdapter()
                                            .getFeed()
                                            .stream()
                                            .filter(post -> post.getUser().equals(doc.getDocument().getId()))
                                            .findFirst()
                                            .ifPresent(post -> feedFragment.getRecyclerAdapter().removePost(post));
                                    mapFragment.getFeed().stream().filter(post -> post.getUser().equals(doc.getDocument().getId())).findFirst().ifPresent(post -> mapFragment.removePost(post));
                                    mapFragment.updateMap();

                                    break;
                            }
                        }
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
        feedFragment.getRecyclerAdapter().toggleMoodFilter(moodType);
        feedFragment.getRecyclerAdapter().getFilter().filter(null);
        mapFragment.toggleMoodFilter(moodType);
        mapFragment.getFilter().filter(null);
    }
}
