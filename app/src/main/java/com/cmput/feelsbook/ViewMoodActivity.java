package com.cmput.feelsbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

    private User currentUser;

    private TextView moodText;
    private TextView reasonText;
    private TextView socialSituationText;
    private ImageView photo;
    private ImageView profilePicture;
    private Post post;
    private static final int REQUEST_EDIT_MOOD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_mood_activity);


        moodText = findViewById(R.id.mood_text);
        reasonText = findViewById(R.id.reason_text);
        socialSituationText = findViewById(R.id.social_text);
        photo = findViewById((R.id.post_picture));
        profilePicture = findViewById(R.id.profile_picture);
        Button editButton = findViewById(R.id.edit_button);




        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
            post = ((Mood) bundle.getSerializable("Mood"));
            setValues((Mood)post);

            if(((Mood) post).getUser().equals(currentUser.getUserName())){
                editButton.setVisibility(View.VISIBLE);
            }
        }

        // sets users profile picture
        Bitmap bitmapProfilePicture = currentUser.profilePicBitmap();
        profilePicture.setImageBitmap(bitmapProfilePicture);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMoodActivity.this, AddMoodActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
                userBundle.putBoolean("editMood", true);
                userBundle.putSerializable("Mood", ((Mood) post));
                intent.putExtras(userBundle);
                startActivityForResult(intent, REQUEST_EDIT_MOOD);

            }
        });

    }

    private void setValues(Mood editMood){

        moodText.setText(getString(editMood.getMoodType().getEmoticon()));
        if (editMood.getReason() != ""){
            reasonText.setText("Reason: " + editMood.getReason());
        } else {
            reasonText.setVisibility(View.INVISIBLE);
        }
        if(editMood.hasSituation()){
            socialSituationText.setText(editMood.getSituation().toString());
        } else{
            socialSituationText.setVisibility(View.INVISIBLE);
        }
        if(editMood.hasPhoto()){
            photo.setImageBitmap(editMood.photoBitmap());
        }
    }

    /**
     * Activity result for edit mood activity, gets mood edited during activity and displays changes
     * @param requestCode
     *     the code sent requesting results
     * @param resultCode
     *     the code returned upon completion
     * @param data
     *     the data sent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_MOOD) {
            if(resultCode == Activity.RESULT_OK){
                Mood mood = ((Mood) data.getSerializableExtra("Mood"));
                setValues(mood);
            }

        }
    }


}
