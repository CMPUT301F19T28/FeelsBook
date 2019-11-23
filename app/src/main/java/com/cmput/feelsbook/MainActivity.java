package com.cmput.feelsbook;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import static android.content.ContentValues.TAG;


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

    //Location permission vars

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private Boolean locationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;


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
                    GeoPoint location = null;
                    Bitmap profilePic = null;
                    Date dateTime = null;


                    try {
                        if (doc.contains("datetime"))
                            dateTime = ((Timestamp) doc.get("datetime")).toDate();

                        if (doc.contains("location"))
                            location = (GeoPoint) doc.get("location");

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
            }
        });
    }
//
    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(locationPermissionGranted){
                    getLastKnownLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }
    public void getLastKnownLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()) {
            if(locationPermissionGranted) {
                getLastKnownLocation();
            }
            else {
                getLocationPermission();
            }
        }
    }

    //
}