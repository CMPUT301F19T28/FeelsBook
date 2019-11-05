package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.post.Post;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private int followCount = 0;
    private int followersCount = 0;
    private int postCount = 0;
    private ImageView profilePicture;
    private User currentUser;
    private RecyclerView historyList;
    private Feed historyAdapter;
    private List<Post> history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * TO BE IMPLEMENTED:
         * - switch between feed and map
         * - click on profile < - current task
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            currentUser = (User)bundle.get("User");
            history = (List<Post>)bundle.get("Post_list");
        }

        ImageButton backButton = findViewById(R.id.exit_profile);
        TextView fullName = findViewById(R.id.full_name);
        TextView userName = findViewById(R.id.username);
        TextView followText = findViewById(R.id.follower_count);
        TextView followingText = findViewById(R.id.following_count);
        TextView postsText = findViewById(R.id.total_posts);

//        historyList = findViewById(R.id.history);
        historyList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        historyList.setLayoutManager(layoutManager);
        historyAdapter = new Feed(history);
        historyList.setAdapter(historyAdapter);

        // TO-DO: add profile picture taken from Firebase
        ImageView profilePicture = findViewById(R.id.profile_picture);
        // TO-DO: replace alias with the document name (username) inside of Firebase
        String alias = "testname123";

        postCount = historyAdapter.getItemCount();
        fullName.setText(currentUser.getName());
        followText.setText(followCount + " following");
        followingText.setText(followersCount + " followers");
        userName.setText("@"+alias);

        if (postCount > 1 || postCount == 0){postsText.setText(postCount + " total posts");}
        else if (postCount == 1){postsText.setText(postCount + " total post");}

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
