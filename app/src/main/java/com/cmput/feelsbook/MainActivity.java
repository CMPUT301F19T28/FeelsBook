package com.cmput.feelsbook;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cmput.feelsbook.post.Mood;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * Homepage where a feed of moods/posts will be seen.
 * Comprised of a scrollable RecyclerView
 */
public class MainActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener {
    private ImageButton profileButton;
    RecyclerView feedView;
    Feed feedAdapter;
    RecyclerView.LayoutManager layoutManager;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * TO BE IMPLEMENTED:
         * - display feed and make working Add Post button
         * - switch between feed and map
         * - click on profile < - current task
         * - pass in Feed to be displayed and personalized in ProfileActivity
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feedView = findViewById(R.id.feedList);
        feedView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        feedView.setLayoutManager(layoutManager);

        feedAdapter = new Feed();
        feedView.setAdapter(feedAdapter);

        profileButton = findViewById(R.id.profileButton);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
        }

        final FloatingActionButton addPostBttn = findViewById(R.id.addPostButton);
        addPostBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when floating action button is pressed instantiates the fragment so a Ride can be
                // added to the list
                // add post activity:
                new AddMoodFragment().show(getSupportFragmentManager(), "ADD_MOOD");
            }
        });

        /**
         * extra code pulled from masterPrototype. I do not have ProfileActivity in this project yet
         */
//        profileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                /**
//                 * TO-DO:
//                 * - convert ProfileFragment to ProfileActivity
//                 * - Fragments are meant for Maps
//                 * - successfully start Profile
//                 */
//                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
//                Bundle userBundle = new Bundle();
//                currentUser.setPosts(feedAdapter);
//                userBundle.putSerializable("User", currentUser);
//                userBundle.putSerializable("Post_list",feedAdapter.getFeed());
//                intent.putExtras(userBundle);
//                startActivity(intent);
//            }
//        });
    }
    /**
     * Takes a mood from the implemented fragment and adds it to the feedAdapter
     * @param newMood
     */
    public void onSubmit (Mood newMood){
        feedAdapter.addPost(newMood);
    }
    /**
     * will eventually be used to edit mood
     */
    public void edited () {
        //Code for editing mood
    }
    /**
     * will be used to delete passed in mood once implemented
     * @param delete
     */
    public void deleted (Mood delete){
        //For deleting mood
    }
}
