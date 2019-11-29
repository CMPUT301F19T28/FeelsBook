package com.cmput.feelsbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
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
    private MapFragment mapFragment;
    private FirebaseFirestore db;
    private CollectionReference MoodCollection;
    private Feed.OnItemClickListener listener;
    private List<MoodType> filteredMoods;
    private List<Post> historyCopy;
    private FilterFragment filter;
    private boolean filterClicked = false;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int ADD_MOOD = 2;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private Bitmap profilePicBitmap;
    private DocumentReference UserDocument;

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
        postCount = 0;


        if (bundle != null) {
            currentUser = (User) bundle.get("User");

            if( currentUser.getProfilePic() != null)
                profilePicture.setImageBitmap(currentUser.profilePicBitmap());
        }

        listener = new Feed.OnItemClickListener() {
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
                userBundle.putSerializable("Mood", post);
                intent.putExtras(userBundle);
                startActivityForResult(intent, 1);
            }
        };


        if(currentUser == null){
            throw new AssertionError("User is null from MainActivity.");
        }

        //Sets the document to that of the current user
        UserDocument = db.collection("users").document(currentUser.getUserName());

        //Sets the collection to that of the current user's moods
        MoodCollection = UserDocument.collection("Moods");

        historyFragment = new FeedFragment();
        historyFragment.getRecyclerAdapter().setOnItemClickListener(listener);
        mapFragment = new MapFragment();
        filteredMoods = new ArrayList<>();
        historyCopy = new ArrayList<>();
        viewPagerAdapter.AddFragment(historyFragment, "History");
        viewPagerAdapter.AddFragment(mapFragment, "Map");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Button backButton = findViewById(R.id.exit_profile);
        Button editProfilePictureButton = findViewById(R.id.edit_profile_pic_button);
        TextView fullName = findViewById(R.id.full_name);
        TextView userName = findViewById(R.id.username);
        TextView followText = findViewById(R.id.follower_count);
        TextView followingText = findViewById(R.id.following_count);
        TextView postsText = findViewById(R.id.total_posts);
        ImageView profilePicture = findViewById(R.id.profile_picture);
        fullName.setText(currentUser.getName());
        userName.setText("@" + currentUser.getUserName());




        MoodCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot doc = task.getResult();
                    if (doc != null) {
                        postCount = doc.size();
                        if (postCount > 1 || postCount == 0) {
                            postsText.setText(postCount + " total posts");
                        } else if (postCount == 1) {
                            postsText.setText(postCount + " total post");
                        }
                        Log.d("Profile", "Total posts retrieved: " + postCount);
                    } else {
                        Log.d("Profile", "No document found");
                    }
                } else {
                    Log.d("Profile", "Document retrieval failed: " + task.getException());
                }
            }
        });

        backButton.setOnClickListener(view -> {
            if (filterClicked) {
                // reset filtered feed if filter was clicked at least once
                filter.resetFilterButtons();
                filteredMoods.clear();
                updateFeed();
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
            startActivityForResult(intent, ADD_MOOD);
        });

        final ImageButton filterButton = findViewById(R.id.profile_filter_button);
        filterButton.setOnClickListener(view -> {
            // creates filter window
            filter = new FilterFragment();
            filter.show(getSupportFragmentManager(), "MAIN_FILTER");
            filterClicked = true;
        });

        final ImageButton logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> {
            LogoutFragment frag = new LogoutFragment();
            frag.show(getSupportFragmentManager(), "logout");
        });

        editProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                }else{
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(ProfileActivity.this.getPackageManager()) != null)
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


    }

    /**
     * Activity result for Camera activity, gets bitmap photo taken during activity and attaches to bitmap variable 'picture'
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                currentUser.setProfilePic(Mood.profilePicString((Bitmap)data.getExtras().get("data")));
                profilePicture.setImageBitmap(currentUser.profilePicBitmap());

                UserDocument.update("profilePic",currentUser.getProfilePic());

            }else {
                currentUser = (User) data.getSerializableExtra("User");

                if (currentUser.getProfilePic() != null)
                    profilePicture.setImageBitmap(currentUser.profilePicBitmap());
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView followersText = findViewById(R.id.follower_count);
        TextView followingText = findViewById(R.id.following_count);
        TextView postsText = findViewById(R.id.total_posts);
        db.collection("users").document(currentUser.getUserName()).collection("following").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                followCount = queryDocumentSnapshots.size();
                followingText.setText(followCount + " following");
            }
        });
        db.collection("users").document(currentUser.getUserName()).collection("followers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                followersCount = queryDocumentSnapshots.size();
                followersText.setText(followersCount + " followers");
            }
        });
        MoodCollection
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        postCount = queryDocumentSnapshots.size();
                        if (postCount > 1 || postCount == 0) {
                            postsText.setText(postCount + " total posts");
                        } else if (postCount == 1) {
                            postsText.setText(postCount + " total post");
                        }
                        Log.d("Profile", "Counter update successful");
                    }
                });


        updateFeed();
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
     * This method updates the FeedFragment whenever the remote database is updated
     */
    private void updateFeed(){
        MoodCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {

            //clears list
            while( historyFragment.getRecyclerAdapter().getItemCount() > 0) {
                historyFragment.getRecyclerAdapter().removePost(0);
                historyFragment.getRecyclerAdapter().notifyItemRemoved(0);
            }
            for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                if(doc.exists())
                    historyFragment.getRecyclerAdapter().addPost(doc.toObject(Mood.class));
            }

            historyFragment.getRecyclerAdapter().notifyDataSetChanged();
            historyCopy = new ArrayList<>(historyFragment.getRecyclerAdapter().getFeed());
            int postListCount = historyFragment.getRecyclerAdapter().getItemCount();

            // update total number of posts
            HashMap<String,Object> userUpdate = new HashMap<>();
            userUpdate.put("total_posts",String.valueOf(postListCount));
            UserDocument
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
        });

    }


    /**
     * Handles when a filter button is pressed.
     * Note that when a filter button is pressed, this means that all moods EXCEPT the currently
     * pressed mood/s will be shown in the feed.
     * @param moodType - the MoodType to be filtered
     */
    public void onSelect(MoodType moodType){
        if (historyCopy.size() > 0){
            filteredMoods.add(moodType);
            // log used for debugging
            Log.d("Filter","(SELECT-Profile)Current filtered mood size: "+filteredMoods.size());
            Iterator<Post> it = historyCopy.iterator();
            List<Post> result = new ArrayList<>();
            while (it.hasNext()){
                Mood m = (Mood)it.next();
                if (filteredMoods.contains(m.getMoodType())){
                    result.add(m);
                }
            }
            historyFragment.getRecyclerAdapter().setFeed(result);
            historyFragment.getRecyclerAdapter().notifyDataSetChanged();
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
        // log used for debugging
        Log.d("Filter","(DESELECT-Profile)Current filtered mood size: "+filteredMoods.size());
        if (filteredMoods.size() > 0){
            Iterator<Post> it = historyCopy.iterator();
            List<Post> result = new ArrayList<>();
            while (it.hasNext()){
                Mood m = (Mood)it.next();
                if (filteredMoods.contains(m.getMoodType())){
                    result.add(m);
                }
            }
            historyFragment.getRecyclerAdapter().setFeed(result);
            historyFragment.getRecyclerAdapter().notifyDataSetChanged();
        }
        else {
            updateFeed();
        }
    }

    /**
     * Logs out the current logged in user from the application.
     */
    public void onLogout(){
        Bundle userBundle = getIntent().getExtras();
        userBundle.remove("User");
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.putExtras(userBundle);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(ProfileActivity.this.getPackageManager()) != null)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
