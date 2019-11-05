package com.cmput.feelsbook;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FollowActivity extends AppCompatActivity {

    private User user;
    private RecyclerView follow_requests;
    private RecyclerView.LayoutManager layoutManager;
    private FollowingRequests followingRequests;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_activity);
        user = (User) getIntent().getExtras().get("user");

        follow_requests = findViewById(R.id.follow_requests_list);
        layoutManager = new LinearLayoutManager(this);
        follow_requests.setLayoutManager(layoutManager);
        followingRequests = new FollowingRequests(user);
        follow_requests.setAdapter(followingRequests);

        Button back = findViewById(R.id.follow_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button search = findViewById(R.id.follow_search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchFragment.newInstance(user).show(getSupportFragmentManager(), "FriendRequest");
            }
        });
    }
}
