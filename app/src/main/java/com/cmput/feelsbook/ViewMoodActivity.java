package com.cmput.feelsbook;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class ViewMoodActivity extends AppCompatActivity{

    private EditText input;
    private Bitmap picture;
    private Bitmap dp;
    private User currentUser;
    private CollectionReference MoodCollection;
    private DocumentReference UserDocument;
//    private Spinner spinner;
//    private Spinner socialSpinner;

    private TextView moodText;
    private TextView reasonText;
    private TextView socialSituationText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_mood_activity);


        moodText = findViewById(R.id.moodText);
        reasonText = findViewById(R.id.reason_text);
        socialSituationText = findViewById(R.id.social_situation_text);


        Button editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




//        input = findViewById(R.id.editText);
//        spinner = findViewById(R.id.mood_spinner);
//        socialSpinner = findViewById(R.id.social_spinner);

//        MoodType[] moodTypes = {MoodType.HAPPY, MoodType.SAD, MoodType.ANGRY, MoodType.ANNOYED, MoodType.SLEEPY, MoodType.SEXY};
//        ArrayList<MoodType> moodList = new ArrayList<>(Arrays.asList(moodTypes));
//        ArrayAdapter<MoodType> moodTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moodList);
//
//        moodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(moodTypeAdapter);

        //creates social situation spinner drop down menu
//        SocialSituation[] socialSits = {SocialSituation.ALONE, SocialSituation.ONEPERSON, SocialSituation.SEVERAL, SocialSituation.CROWD};
//        ArrayList<SocialSituation> socialSitList = new ArrayList<>(Arrays.asList(socialSits));
//        ArrayAdapter<SocialSituation> socialAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, socialSitList);
//
//        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        socialSpinner.setAdapter(socialAdapter);
//        socialSpinner.setVisibility(View.GONE); //sets the view to be gone because it is optional

        Bundle bundle = getIntent().getExtras();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        Button deleteButton = findViewById(R.id.delete_button);


//        if (bundle != null) {
//            currentUser = (User) bundle.get("User");
//            if((boolean) bundle.get("editMood")){
//                setValues(((Mood) bundle.getSerializable("Mood")).Serialize(false), moodTypes, socialSits);
////                deleteButton.setVisibility(View.VISIBLE);
////                deleteButton.setOnClickListener(view -> {
////                    deleted(((Mood) bundle.getSerializable("Mood")).Serialize(false));
////                    finish();
////                });
//            }
//        }

        //Sets the document to that of the current user
        UserDocument = db.collection("users").document(currentUser.getUserName());

        //Sets the collectionReference to that of the current users moods
        MoodCollection = UserDocument.collection("Moods");

        // sets users profile picture
        Bitmap bitmapProfilePicture = currentUser.getProfilePic();
        ImageView profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setImageBitmap(bitmapProfilePicture);

        //if the social situatiion button is pressed then shows the drop down
//        Button socialBttn = findViewById(R.id.social_situation_button);
//        socialBttn.setOnClickListener(v -> {
//            if (socialSpinner.getVisibility() == View.VISIBLE) {
//                socialSpinner.setVisibility(View.INVISIBLE);
//            } else {
//                socialSpinner.setVisibility(View.VISIBLE);
//            }
//        });



        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

//        Button postButton = findViewById(R.id.edit_button);
//        postButton.setOnClickListener(v -> {
//            onSubmit(getValues());
////                Intent intent = new Intent();
//            finish();
//        });
    }


    /**
     * takes a mood and puts it in the database. If the mood is new will create a new mood in the
     * database else will edit the mood in the database with the new parameters
     * @param newMood
     *      New mood object to be added or edited
     */
    @SuppressLint("NewApi")
    private void onSubmit(Post newMood){

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
            Bitmap bitmap = newMood.getProfilePic();
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
        data.put("User", currentUser.getUserName());

        MoodCollection
                .document(newMood.toString())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d("Sample", "Data addition successful"))
                .addOnFailureListener(e -> Log.d("Sample", "Data addition failed" + e.toString()));

    }

//    /**
//     * Sets Edittext and spinner values  to that of the passed in mood
//     * @param editMood
//     *     Mood who's values are to taken
//     * @param moodTypes
//     *     array of moodtypes that corresponds to the spinner values
//     * @param socialSits
//     *     array of social situations that corresponds to the spinner values
//     */
//    private void setValues(Mood editMood, MoodType[] moodTypes, SocialSituation[] socialSits){
//        input.setText(editMood.getReason());
//        for(int i = 0; i < moodTypes.length; i++){
//            if(moodTypes[i] == editMood.getMoodType()){
//                moodText.setText(moodTypes[i].toString());
//            }
//        }
//
//        //checks to see if the editmood has a social situation
//        // if makes dropdown visible and sets the social situation
//        if(editMood.hasSituation()){
//            for(int i = 0; i < socialSits.length; i++){
//                if(socialSits[i] == editMood.getSituation()){
//                    socialSituationText.setVisibility(View.VISIBLE);
//                    socialSituationText.setText(socialSits[i].toString());
//                }
//            }
//        }
//    }


//    private Mood getValues(){
//        String moodText = input.getText().toString();
//        MoodType selected_type = (MoodType) spinner.getSelectedItem();
//        SocialSituation selectedSocial = null;
//
//        if (socialSpinner.getVisibility() == View.VISIBLE) {
////            selectedSocial = (SocialSituation) socialSpinner.getSelectedItem();
//
//        }
//
//        if (picture == null) {
//            return new Mood(selected_type, null).withReason(moodText)
//                    .withSituation(selectedSocial).withUser(currentUser.getUserName());
//        }
//        else{
//            return new Mood(selected_type, null).withPhoto(picture).withReason(moodText)
//                    .withSituation(selectedSocial).withUser(currentUser.getUserName());
//        }
//
//    }


}
