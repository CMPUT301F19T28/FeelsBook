package com.cmput.feelsbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    User currentUser;
    RecyclerView feedView;
    Feed feedAdapter;
    RecyclerView.LayoutManager layoutManager;
    boolean loggedOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feedView = findViewById(R.id.feedList);
        feedView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        feedView.setLayoutManager(layoutManager);

        feedAdapter = new Feed();
        feedView.setAdapter(feedAdapter);

        /**
         * to-do: implement test RecyclerView
         * - add FloatingActionButton for post added
         * -
         */

        try {
            currentUser = (User) getIntent().getSerializableExtra(LoginPageActivity.USER);
        }
        catch(Exception e){
            //must be from signup page else couldn't be in main activity
            currentUser = (User) getIntent().getSerializableExtra(SignUpActivity.USER);
        }

        final FloatingActionButton addPostBttn = findViewById(R.id.addPostButton);
        addPostBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When floating action button is pressed instantiates the fragment so a Ride can be
                // added to the list
                //add post activity ;
            }
        });
        /**
         * Need to add more functionality for feed/ posts
         * may need to adjust classes to better represent the app
         */

    }
}