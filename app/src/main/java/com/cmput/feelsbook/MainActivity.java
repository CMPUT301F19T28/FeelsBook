package com.cmput.feelsbook;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.cmput.feelsbook.post.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * Homepage where feed of moods/Posts will be seen
 * comprises of a scrollable recyclerView
 */
public class MainActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener{
    private ImageButton profileButton;
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

        feedAdapter = new Feed( new Feed.OnItemClickListener(){

            /**
             * Sets onItemClick to open a fragment in which the mood will be edited
             * @param post
             *          Post to be edited
             */
             public void onItemClick(Post post){
                //.makeText(getApplicationContext(), "itemClicked", Toast.LENGTH_SHORT). show();
                new AddMoodFragment().newInstance(post).show(getSupportFragmentManager(), "EDIT_MOOD");
            }
        });

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
     *          mood that will be added to the feed
     */
    public void onSubmit(Post newMood){
        feedAdapter.addPost(newMood);
    }

    /**
     * notifies the adapter that the data set has changed
     */
    public void edited(){
        //Code for editing mood
        feedAdapter.notifyDataSetChanged();
    }

    /**
     * will be used to delete passed in mood once implemented
     * @param mood
     *      mood to be deleted
     */
    public void deleted(Post mood){
        //For deleting mood
        feedAdapter.removePost(mood);
        feedAdapter.notifyDataSetChanged();
    }
}