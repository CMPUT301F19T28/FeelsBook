package com.cmput.feelsbook;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.cmput.feelsbook.AddMoodFragment;
import com.cmput.feelsbook.R;
import com.cmput.feelsbook.User;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class AddMoodActivity extends AppCompatActivity{

    private EditText input;
    private Bitmap picture;
    private Bitmap dp;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private User currentUser;
    private Feed historyAdapter;
    private FirebaseFirestore db;
    private Bitmap BitmapProfilePicture;
    CollectionReference MoodCollection;
    DocumentReference UserDocument;
    Spinner spinner;
    Spinner socialSpinner;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        /**
         * spinner currently displays text for moodtype, want to display emoji
         */
        input = findViewById(R.id.editText);
        spinner = findViewById(R.id.mood_spinner);
        socialSpinner = findViewById(R.id.social_spinner);

        MoodType moodTypes[] = { MoodType.HAPPY, MoodType.SAD, MoodType.ANGRY, MoodType.ANNOYED, MoodType.SLEEPY, MoodType.SEXY};
        ArrayList<MoodType> moodList = new ArrayList<MoodType>();
        moodList.addAll(Arrays.asList(moodTypes));
        ArrayAdapter<MoodType> moodTypeAdapter = new ArrayAdapter<MoodType>(this, android.R.layout.simple_spinner_item, moodList);

        moodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(moodTypeAdapter);

        //creates social situation spinner drop down menu
        SocialSituation socialSits[] = { SocialSituation.ALONE, SocialSituation.ONEPERSON, SocialSituation.SEVERAL, SocialSituation.CROWD};
        ArrayList<SocialSituation> socialSitList = new ArrayList<SocialSituation>();
        socialSitList.addAll(Arrays.asList(socialSits));
        ArrayAdapter<SocialSituation> socialAdapter = new ArrayAdapter<SocialSituation>(this, android.R.layout.simple_spinner_item, socialSitList);

        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSpinner.setAdapter(socialAdapter);
        socialSpinner.setVisibility(View.GONE); //sets the view to be gone because it is optional

        Bundle bundle = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();

        if (bundle != null) {
            currentUser = (User) bundle.get("User");
            if((boolean) bundle.get("editMood")){
                setValues(((Mood) bundle.getSerializable("Mood")).Serialize(false), moodTypes, socialSits);
            }
        }

        //Sets the document to that of the current user
        UserDocument = db.collection("users").document(currentUser.getUserName());

        //Sets the collectionReference to that of the current users moods
        MoodCollection = UserDocument.collection("Moods");

        // sets users profile picture
        BitmapProfilePicture = currentUser.getProfilePic();
        ImageView profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setImageBitmap(BitmapProfilePicture);

        //if the social situatiion button is pressed then shows the drop down
        Button socialBttn = findViewById(R.id.social_situation_button);
        socialBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socialSpinner.getVisibility() == View.VISIBLE) {
                    socialSpinner.setVisibility(View.INVISIBLE);
                } else {
                    socialSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        /**
         * Camera button in case of add mood, used is able to attach a picture to post/mood
         */
        Button cameraButtonAdd = findViewById(R.id.add_picture_button);
        cameraButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(AddMoodActivity.this.getPackageManager()) != null)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moodText = input.getText().toString();
                MoodType selected_type = (MoodType) spinner.getSelectedItem();
                SocialSituation selectedSocial = null;

                if (socialSpinner.getVisibility() == View.VISIBLE)
                    selectedSocial = (SocialSituation) socialSpinner.getSelectedItem();

//                Bundle bundle = new Bundle();

                if (picture == null) {
                    Mood newMood = new Mood(selected_type, null).withReason(moodText).withSituation(selectedSocial);
//                    bundle.putSerializable("Mood", newMood);
                    onSubmit(newMood);
                }
                else{
//                    ProxyBitmap proxyBitmap = new ProxyBitmap(picture);
                    Mood newMood = new Mood(selected_type, null).withPhoto(picture).withReason(moodText).withSituation(selectedSocial);
//                    bundle.putSerializable("Mood",newMood);
                    onSubmit(newMood);
                }
                Intent intent = new Intent(); //.putExtras(bundle);
//                setResult(RESULT_OK, intent);
                finish();
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
                picture = (Bitmap) data.getExtras().get("data");
            }
        }
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

        MoodCollection
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

    public void setValues(Mood editMood, MoodType[] moodTypes, SocialSituation[] socialSits){
        input.setText(editMood.getReason());
        for(int i = 0; i < moodTypes.length; i++){
            if(moodTypes[i] == editMood.getMoodType()){
                spinner.setSelection(i);
            }
        }

        //checks to see if the editmood has a social situation
        // if makes dropdown visible and sets the social situation
        if(editMood.hasSituation()){
            for(int i = 0; i < socialSits.length; i++){
                if(socialSits[i] == editMood.getSituation()){
                    socialSpinner.setVisibility(View.VISIBLE);
                    socialSpinner.setSelection(i);
                }
            }
        }
    }
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

}
