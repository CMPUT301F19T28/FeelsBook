package com.cmput.feelsbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private static final String TAG = "User";

    private String userName;
    private String name;
    private Feed posts;
    private FollowList followList;
    private Bitmap profilePicture;

    public User(String userName, String name, Feed posts, FollowList followsList){
        this.userName = userName;
        this.name = name;
        this.posts = posts;
        followList = followsList;
    }

    /**
     * Send a follow request to the specified user
     * @param context
     *      Provides the context to display information to the user
     * @param userId
     *      The name of the user to send the request
     */
    public void sendFollowRequest(Context context, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult() != null && task.getResult().exists()) {
                        Map<String, String> data = new HashMap<>();
                        data.put(getUserName(), getName());
                        task.getResult().getReference().collection("info").document("followingRequests").set(data, SetOptions.merge());
                    }
                    else {
                        Toast.makeText(context, "No user with id: " + userId, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d(TAG,"Failed with:  ", task.getException());
                }
            }
        });
    }

    public User(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Feed getPosts() {
        return posts;
    }

    public void setPosts(Feed posts) {
        this.posts = posts;
    }

    public FollowList getFollowsList() {
        return followList;
    }

    public void setFollowsList(FollowList followsList) {
        followList = followsList;
    }

    public Bitmap getProfilePic(){
        return this.profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
