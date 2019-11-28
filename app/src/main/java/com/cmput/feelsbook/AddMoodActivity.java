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

import com.cmput.feelsbook.R;
import com.cmput.feelsbook.User;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private User currentUser;
    private FirebaseFirestore db;
    private CollectionReference MoodCollection;
    private DocumentReference UserDocument;
    private Spinner spinner;
    private Spinner socialSpinner;
    private boolean edit = false;
    private Mood edited;
    private Mood mood = new Mood();


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
        ArrayAdapter<SocialSituation> socialAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(SocialSituation.values()));
        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSpinner.setAdapter(socialAdapter);

        Bundle bundle = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();

        Button deleteButton = findViewById(R.id.delete_button);

        if (bundle != null) {
            currentUser = (User) bundle.get("User");
            if ((boolean) bundle.get("editMood")) {
                mood = (Mood) bundle.get("Mood");
                spinner.setSelection(moodTypeAdapter.getPosition(mood.getMoodType()));
                socialSpinner.setSelection(socialAdapter.getPosition(mood.getSituation()));
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(view -> {
                    delete(mood);
                    finish();
                });
            }
        }

        if(currentUser == null){
            throw new AssertionError("Calling a null user object");
        }

        //Sets the document to that of the current user
        UserDocument = db.collection("users").document(currentUser.getUserName());

        //Sets the collectionReference to that of the current users moods
        MoodCollection = UserDocument.collection("Moods");

        // sets users profile picture
        Bitmap bitmapProfilePicture = currentUser.getProfilePic();
        ImageView profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setImageBitmap(bitmapProfilePicture);

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
            mood.setMoodType(moodTypeAdapter.getItem(spinner.getSelectedItemPosition()));
            mood.setPhoto(Mood.photoString(picture));
            mood.setReason(input.getText().toString());
            mood.setSituation(socialAdapter.getItem(socialSpinner.getSelectedItemPosition()));
            mood.setProfilePic(Mood.profilePicString(bitmapProfilePicture));
            onSubmit(mood);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("Mood", mood);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
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
        MoodCollection
                .document(newMood.toString())
                .set(newMood)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                        updateMostRecent();
                });

    }

    /**
     * will be used to delete passed in mood once implemented
     * @param mood
     *      mood to be deleted
     */
    private void delete(Post mood){
        Toast.makeText(getApplicationContext(), "Mood Deleted", Toast.LENGTH_SHORT).show();
        MoodCollection
                .document(mood.toString())
                .delete()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                        updateMostRecent();
                });
    }

    public void updateMostRecent(){
        MoodCollection.orderBy("dateTime", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0){
                            db.collection("mostRecent")
                                    .document(currentUser.getUserName())
                                    .set(queryDocumentSnapshots.getDocuments().get(0).getData());
                        } else {
                            db.collection("mostRecent").document(currentUser.getUserName()).delete();
                        }
                    }
                });
    }

//

}
