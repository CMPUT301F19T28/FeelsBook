package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton backButton;
    private int followCount;
    private int followersCount;
    private int postCount;
    private ImageView profilePicture;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * TO BE IMPLEMENTED:
         * - display feed and make working Add Post button
         * - switch between feed and map
         * - click on profile < - current task
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        backButton = findViewById(R.id.exit_profile);
        TextView fullName = findViewById(R.id.full_name);
        TextView userName = findViewById(R.id.username);
        ImageView profilePicture = findViewById(R.id.profile_picture);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            currentUser = (User)bundle.get("User");
        }
        fullName.setText(currentUser.getName());
        //profilePicture.setImageDrawable();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
