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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used to represent the individual using the application. Contains personal information and
 * allows for creation of new posts.
 * String userName, name - contains the username and name of the user, respectively
 * Feed posts - contains the user's personal posts
 * FollowList followList - contains the user's following list
 * Bitmap profilePicture - contains the user's profile pictur
 */
public class User implements Serializable {
    private static final String TAG = "User";

    private String userName;
    private String name;
    private Feed posts;
    private List<User> following;
    private Bitmap profilePicture;

    public User(String userName, String name, Feed posts, List<User> following){
        this.userName = userName;
        this.name = name;
        this.posts = posts;
        this.following = following;
    }

    public void acceptFollowRequest() {

    }

    /**
     * Send a follow request to the specified user
     * @param context
     *      Provides the context to display information to the user
     * @param userId
     *      The name of the user to send the request
     */
    public void sendFollowRequest(Context context, String userId) {
        if(userId.equals(getUserName())){
            Toast.makeText(context, "You can't follow yourself.", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(getUserName())
                .collection("info")
                .document("following")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists() || !task.getResult().contains(userId)) {
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(userId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful() && task.getResult().exists()) {
                                                Map<String, Object> data = new HashMap<>();
                                                data.put(getUserName(), getName());
                                                task.getResult().getReference()
                                                        .collection("info")
                                                        .document("followingRequests")
                                                        .set(data, SetOptions.merge());
                                                Toast.makeText(context, "Follow request was sent to " + userId, Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(context, "No user exists with the id " + userId, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(context, "You are already following " + userId, Toast.LENGTH_SHORT).show();
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

    public void setPosts(Feed posts) {this.posts = posts;}

    public List<User> getFollowingList() {
        return following;
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
