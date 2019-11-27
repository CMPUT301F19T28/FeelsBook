package com.cmput.feelsbook;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
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
    private FirebaseFirestore db;
    private CollectionReference MoodCollection;
    private DocumentReference UserDocument;
    private Spinner spinner;
    private Spinner socialSpinner;
    private boolean edit = false;
    private Mood edited;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        input = findViewById(R.id.editText);
        spinner = findViewById(R.id.mood_spinner);
        socialSpinner = findViewById(R.id.social_spinner);

        MoodTypeAdapter moodTypeAdapter = new MoodTypeAdapter(this, Arrays.asList(MoodType.values()));
        spinner.setAdapter(moodTypeAdapter);

        //creates social situation spinner drop down menu
        SocialSituation[] socialSits = {SocialSituation.ALONE, SocialSituation.ONEPERSON, SocialSituation.SEVERAL, SocialSituation.CROWD};
        ArrayList<SocialSituation> socialSitList = new ArrayList<>(Arrays.asList(socialSits));
        ArrayAdapter<SocialSituation> socialAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, socialSitList);

        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSpinner.setAdapter(socialAdapter);
        socialSpinner.setVisibility(View.GONE); //sets the view to be gone because it is optional

        Bundle bundle = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();

        Button deleteButton = findViewById(R.id.delete_button);


        if (bundle != null) {
            currentUser = (User) bundle.get("User");
            edit  = (boolean) bundle.get("editMood");
            if(edit){
                setValues(((Mood) bundle.getSerializable("Mood")).Serialize(false), MoodType.values(), socialSits);
                edited = ((Mood) bundle.getSerializable("Mood")).Serialize(false);
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(view -> {
                    deleted(((Mood) bundle.getSerializable("Mood")).Serialize(false));
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    Bundle userBundle = new Bundle();
                    userBundle.putSerializable("User", currentUser);
                    intent.putExtras(userBundle);
                    startActivity(intent);
                });
            }
        }

        //Sets the document to that of the current user
        UserDocument = db.collection("users").document(currentUser.getUserName());

        //Sets the collectionReference to that of the current users moods
        MoodCollection = UserDocument.collection("Moods");

        // sets users profile picture
        Bitmap bitmapProfilePicture = currentUser.getProfilePic();
        ImageView profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setImageBitmap(bitmapProfilePicture);

        //if the social situatiion button is pressed then shows the drop down
        Button socialBttn = findViewById(R.id.social_situation_button);
        socialBttn.setOnClickListener(v -> {
            if (socialSpinner.getVisibility() == View.VISIBLE) {
                socialSpinner.setVisibility(View.INVISIBLE);
            } else {
                socialSpinner.setVisibility(View.VISIBLE);
            }
        });

        Button cameraButtonAdd = findViewById(R.id.add_picture_button);
        cameraButtonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(AddMoodActivity.this.getPackageManager()) != null)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        Button postButton = findViewById(R.id.edit_button);
        postButton.setOnClickListener(v -> {
            onSubmit(getValues());
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putSerializable("User", currentUser);
            intent.putExtras(userBundle);
            startActivity(intent);
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

        db.collection("mostRecent")
                .document(currentUser.getUserName())
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
     * Sets Edittext and spinner values  to that of the passed in mood
     * @param editMood
     *     Mood who's values are to taken
     * @param moodTypes
     *     array of moodtypes that corresponds to the spinner values
     * @param socialSits
     *     array of social situations that corresponds to the spinner values
     */
    private void setValues(Mood editMood, MoodType[] moodTypes, SocialSituation[] socialSits){
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

    /**
     * will be used to delete passed in mood once implemented
     * @param mood
     *      mood to be deleted
     */
    private void deleted(Post mood){
        Toast.makeText(getApplicationContext(), "Mood Deleted", Toast.LENGTH_SHORT).show();
        MoodCollection
                .document(mood.toString())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("--DELETE OPERATION---: ",
                        "Data removal successful"))
                .addOnFailureListener(e -> Log.d("--DELETE OPERATION---: ",
                        "Data removal failed" + e.toString()));
        updateMostRecent();
    }

    public void updateMostRecent(){
        MoodCollection.orderBy("datetime", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0){
                            for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                                db.collection("mostRecent")
                                        .document(currentUser.getUserName())
                                        .set(doc.getData())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("AddMood", "Most recent successfully set");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("AddMood", "Failure to set most recent document with " + e);
                                            }
                                        });
                            }
                        } else {
                            db.collection("mostRecent").document(currentUser.getUserName()).delete();
                        }
                    }
                });
    }

    private Mood getValues(){
        String moodText = input.getText().toString();
        MoodType selected_type = (MoodType) spinner.getSelectedItem();
        SocialSituation selectedSocial = null;

        if (socialSpinner.getVisibility() == View.VISIBLE)
            selectedSocial = (SocialSituation) socialSpinner.getSelectedItem();


        if (picture == null) {
            if(edit){
                return new Mood(edited.getDateTime(), selected_type, null)
                        .withReason(moodText).withSituation(selectedSocial)
                        .withUser(currentUser.getUserName());
            }
            return new Mood(selected_type, null).withReason(moodText)
                    .withSituation(selectedSocial).withUser(currentUser.getUserName());
        }
        else{
            if(edit){
                return new Mood(edited.getDateTime(), selected_type, null)
                        .withPhoto(picture).withReason(moodText).withSituation(selectedSocial)
                        .withUser(currentUser.getUserName());
            }
            return new Mood(selected_type, null).withPhoto(picture).withReason(moodText)
                    .withSituation(selectedSocial).withUser(currentUser.getUserName());
        }

    }


}
