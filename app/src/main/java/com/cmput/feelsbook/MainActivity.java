package com.cmput.feelsbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private ImageButton profileButton;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Main screen features:
         * - added Profile display
         *
         * TO BE IMPLEMENTED:
         * - display feed and make working Add Post button
         * - switch between feed and map
         * - click on profile < - current task
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileButton = findViewById(R.id.profileButton);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            user = (User)bundle.get("User");
        }


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, user.getName(), Toast.LENGTH_SHORT).show();
                /**
                 * TO-DO:
                 * - convert ProfileFragment to ProfileActivity
                 * - Fragments are meant for Maps
                 * - successfully start Profile
                 */

            }
        });

    }
}
