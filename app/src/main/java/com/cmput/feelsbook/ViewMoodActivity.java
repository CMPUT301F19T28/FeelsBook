package com.cmput.feelsbook;

import android.annotation.SuppressLint;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_mood_activity);


        moodText = findViewById(R.id.Mood_text);
        reasonText = findViewById(R.id.reason_text);
        socialSituationText = findViewById(R.id.social_situation_text);
        photo = findViewById((R.id.post_picture));
        profilePicture = findViewById(R.id.profile_picture);
        Button editButton = findViewById(R.id.edit_button);




        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
            post = ((Mood) bundle.getSerializable("Mood")).Serialize(false);
            setValues((Mood)post);

            if(((Mood) post).getUser().equals(currentUser.getUserName())){
                editButton.setVisibility(View.VISIBLE);
            }
        }

        // sets users profile picture
        Bitmap bitmapProfilePicture = currentUser.getProfilePic();
        profilePicture.setImageBitmap(bitmapProfilePicture);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMoodActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("User", currentUser);
                userBundle.putBoolean("editMood", true);
                userBundle.putSerializable("Mood", ((Mood) post).Serialize(true));
                intent.putExtras(userBundle);
                startActivity(intent);
            }
        });

    }

    private void setValues(Mood editMood){

        moodText.setText(editMood.getMoodType().toString());
        reasonText.setText(editMood.getReason());

        if(editMood.hasSituation()){
            socialSituationText.setText(editMood.getSituation().toString());
        }else{
            socialSituationText.setText("N/A");
        }

        if(editMood.hasPhoto()){
            photo.setImageBitmap(editMood.getPhoto());
        }
    }


}
