package com.cmput.feelsbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;


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

public class ProfileActivity extends AppCompatActivity implements FilterFragment.OnMoodSelectListener, LogoutFragment.OnLogoutListener{
    private int followCount;
    private int followersCount;
    private int postCount;
    private ImageView profilePicture;
    private User currentUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private FeedFragment historyFragment;
    private FirebaseFirestore db;
    private MapFragment mapFragment;
    private Feed.OnItemClickListener listener;
    private Boolean locationPermissionGranted;
    private List<MoodType> filteredMoods;
    private List<Post> historyCopy;
    private FilterFragment filter;
    private Bitmap bitmapProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Bundle bundle = getIntent().getExtras();
        tabLayout = findViewById(R.id.profile_tab);
        viewPager = findViewById(R.id.history_pager);
        profilePicture = findViewById(R.id.profile_picture);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        db = FirebaseFirestore.getInstance();
        filter = new FilterFragment();

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
        postCount = 0;
        followCount = 0;
        followersCount = 0;


        if (bundle != null){
            currentUser = (User)bundle.get("User");
            locationPermissionGranted = bundle.getBoolean("locationPermission");
        }

        if(currentUser == null){
            throw new AssertionError("User is null from MainActivity.");
        }

        historyFragment = new FeedFragment();
        historyFragment.getRecyclerAdapter().setOnItemClickListener(listener);
        viewPagerAdapter.AddFragment(historyFragment, "History");
        filteredMoods = new ArrayList<>();
        historyCopy = new ArrayList<>();
        
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

        Button backButton = findViewById(R.id.exit_profile);
        TextView fullName = findViewById(R.id.full_name);
        TextView userName = findViewById(R.id.username);
        TextView followersText = findViewById(R.id.follower_count);
        TextView followingText = findViewById(R.id.following_count);
        TextView postsText = findViewById(R.id.total_posts);
        fullName.setText(currentUser.getName());
        userName.setText("@" + currentUser.getUserName());
        String photo = currentUser.getProfilePic();
        byte[] decodePhoto = Base64.getDecoder().decode(photo);
        bitmapProfilePicture = BitmapFactory.decodeByteArray(decodePhoto, 0, decodePhoto.length);


        if(bitmapProfilePicture != null){
            profilePicture.setImageBitmap(bitmapProfilePicture);
        }

        db.collection("users")
                .document(currentUser.getUserName())
                .collection("Moods")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if(e == null) {
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (doc.getType()) {
                                case ADDED:
                                    historyFragment.getRecyclerAdapter().addPost(doc.getDocument().toObject(Mood.class));
                                    historyFragment.getRecyclerAdapter().notifyItemInserted(doc.getNewIndex());
                                    mapFragment.addPost(doc.getDocument().toObject(Mood.class));
                                    mapFragment.updateMap();
                                    break;
                                case REMOVED:
                                    historyFragment.getRecyclerAdapter().removePost(doc.getOldIndex());
                                    historyFragment.getRecyclerAdapter().notifyItemRemoved(doc.getOldIndex());
                                    historyFragment.getRecyclerAdapter().notifyItemRangeChanged(doc.getOldIndex(), historyFragment.getRecyclerAdapter().getItemCount());
                                    mapFragment.getFeed().stream().filter(post -> post.getUser().equals(doc.getDocument().getId())).findFirst().ifPresent(post -> mapFragment.removePost(post));
                                    mapFragment.updateMap();
                                    break;
                                case MODIFIED:
                                    historyFragment.getRecyclerAdapter().removePost(doc.getOldIndex());
                                    historyFragment.getRecyclerAdapter().addPost(doc.getDocument().toObject(Mood.class));
                                    historyFragment.getRecyclerAdapter().notifyItemChanged(doc.getOldIndex());

                            }
                        }
                    }
                });

        // document reference used to fetch total number of posts field inside of the database
        CollectionReference cr = db.collection("users")
                .document(currentUser.getUserName()).collection("Moods");

        cr.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(queryDocumentSnapshots != null) {
                postCount = queryDocumentSnapshots.getDocuments().size();
                if (postCount > 1 || postCount == 0) {
                    postsText.setText(postCount + " total posts");
                } else if (postCount == 1) {
                    postsText.setText(postCount + " total post");
                }
            }
        });

        db.collection("users")
                .document(currentUser.getUserName())
                .collection("followers")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if(queryDocumentSnapshots != null) {
                        followersCount = queryDocumentSnapshots.getDocuments().size();
                        if(followersCount > 1 || followersCount == 0)
                            followersText.setText(followersCount + " followers");
                        else if(followersCount == 1)
                            followersText.setText(followersCount + " follower");

                    }
                });
        db.collection("users")
                .document(currentUser.getUserName())
                .collection("following")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if(queryDocumentSnapshots != null) {
                        followCount = queryDocumentSnapshots.getDocuments().size();
                        followingText.setText(followCount + " following");
                    }
                });

        backButton.setOnClickListener(view -> {
            if(filter.prefs != null) {
                filter.reset();
                historyFragment.getRecyclerAdapter().clearMoods();
                mapFragment.clearMoods();
            }
            finish();
        });


        final FloatingActionButton profileButton = findViewById(R.id.profile_float_button);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddMoodActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putSerializable("User", currentUser);
            userBundle.putBoolean("editMood", false);
            intent.putExtras(userBundle);
            startActivityForResult(intent, 1);
        });

        final ImageButton filterButton = findViewById(R.id.profile_filter_button);
        filterButton.setOnClickListener(view -> {
            filter.show(getSupportFragmentManager(), "MAIN_FILTER");
        });

        final ImageButton logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> {
                LogoutFragment frag = new LogoutFragment();
                frag.show(getSupportFragmentManager(), "logout");
        });
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
        if(filter.prefs != null) {
            filter.reset();
            historyFragment.getRecyclerAdapter().clearMoods();
            mapFragment.clearMoods();
        }
        startActivity(intent);
    }

    /**
     * Handles when a filter button is pressed.
     * Note that when a filter button is pressed, this means that all moods EXCEPT the currently
     * pressed mood/s will be shown in the feed.
     * @param moodType - the MoodType to be filtered
     */
    public void onSelect(MoodType moodType){
        historyFragment.getRecyclerAdapter().toggleMoodFilter(moodType);
        historyFragment.getRecyclerAdapter().getFilter().filter(null);
        mapFragment.toggleMoodFilter(moodType);
        mapFragment.getFilter().filter(null);
    }

    /**
     * Logs out the current logged in user from the application.
     */
    public void onLogout(){
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(filter.prefs != null) {
            filter.reset();
            historyFragment.getRecyclerAdapter().clearMoods();
            mapFragment.clearMoods();
        }
        startActivity(intent);
    }
}
