package com.cmput.feelsbook;
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
 * Homepage where feed of moods/Posts will be seen
 * comprises of a scrollable recyclerView
 */
public class MainActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener{
    User currentUser;
    RecyclerView feedView;
    Feed feedAdapter;
    RecyclerView.LayoutManager layoutManager;

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

        currentUser = (User) getIntent().getExtras().get("User");

        Button dotButton = findViewById(R.id.dot_button);


        // App crashes if there is no post present
        // need to have if statment ... if there is a post then implement this onclick
        if (currentUser.getPosts().getItemCount() > 0)
//        if (feedAdapter.getItemCount() > 0)
        {
            Toast.makeText(this, "Delete selected",
                    Toast.LENGTH_SHORT).show();

            dotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new PostDotFragment().show(getSupportFragmentManager(), "DotButton");
                }
            });
        }


        final FloatingActionButton addPostBttn = findViewById(R.id.addPostButton);
        addPostBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When floating action button is pressed instantiates the fragment so a Ride can be
                // added to the list
                //add post activity ;
                new AddMoodFragment().show(getSupportFragmentManager(), "ADD_MOOD");
            }
        });


    }
    /**
     * Takes a mood from the implemented fragment and adds it to the feedAdapter
     * @param newMood
     */
    public void onSubmit(Mood newMood){
        feedAdapter.addPost(newMood);
    }

    /**
     * will eventually be used to edit mood
     */
    public void edited(){
        //Code for editing mood
    }

    /**
     * will be used to delete passed in mood once implemented
     * @param delete
     */
    public void deleted(Mood delete){
        //For deleting mood
    }
}