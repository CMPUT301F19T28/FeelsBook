package com.cmput.feelsbook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.cmput.feelsbook.AddMoodFragment;
import com.cmput.feelsbook.R;
import com.cmput.feelsbook.User;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class AddMoodActivity extends AppCompatActivity {

    private EditText input;
    private Bitmap picture;
    private Bitmap dp;
    private static final int REQUEST_IMAGE_CAPTURE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        /**
         * spinner currently displays text for moodtype, want to display emoji
         */
        input = findViewById(R.id.editText);
        Spinner spinner = findViewById(R.id.mood_spinner);
        Spinner socialSpinner = findViewById(R.id.social_spinner);

        MoodType moodTypes[] = {MoodType.HAPPY, MoodType.SAD,MoodType.ANGRY, MoodType.ANNOYED,MoodType.SLEEPY, MoodType.SEXY};
        ArrayList<MoodType> moodList = new ArrayList<MoodType>();
        moodList.addAll(Arrays.asList(moodTypes));
        ArrayAdapter<MoodType> moodTypeAdapter = new ArrayAdapter<MoodType>(this, android.R.layout.simple_spinner_item, moodList);
        moodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(moodTypeAdapter);

        //creates social situation spinner drop down menu
        SocialSituation socialSits[] = {SocialSituation.ALONE, SocialSituation.ONEPERSON, SocialSituation.SEVERAL, SocialSituation.CROWD };
        ArrayList<SocialSituation> socialSitList = new ArrayList<SocialSituation>();
        socialSitList.addAll(Arrays.asList(socialSits));
        ArrayAdapter<SocialSituation> socialAdapter = new ArrayAdapter<SocialSituation>(this, android.R.layout.simple_spinner_item, socialSitList);
        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSpinner.setAdapter(socialAdapter);
        socialSpinner.setVisibility(View.GONE); //sets the view to be gone because it is optional


        //if the social situatiion button is pressed then shows the drop down
        Button socialBttn = findViewById(R.id.social_situation_button);
        socialBttn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (socialSpinner.getVisibility() == View.VISIBLE) {
                    socialSpinner.setVisibility(View.INVISIBLE);
                }
                else {
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

//This post button shiet aint workin yet lul 

        Button postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moodText = input.getText().toString();
                Object selectedMood = spinner.getSelectedItem();
                MoodType selected_type = MoodType.class.cast(selectedMood);

                if (!moodText.isEmpty()) {
                    Mood newMood = new Mood(selected_type, null).withReason(moodText).withPhoto(picture);

                } else {

                    SocialSituation selectedSocial = SocialSituation.class.cast(socialSpinner.getSelectedItem());

                    if (!moodText.isEmpty()) {

                        if (socialSpinner.getVisibility() == View.VISIBLE) {
                            new Mood(selected_type, null).withReason(moodText).withSituation(selectedSocial);
                        } else {
                            new Mood(selected_type, null).withReason(moodText);
                        }
                    } else {
                        Toast.makeText(AddMoodActivity.this, "Must fill required text",
                                Toast.LENGTH_SHORT).show();
                    }
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
                picture = (Bitmap) data.getExtras().get("data");
            }
        }
    }

}
