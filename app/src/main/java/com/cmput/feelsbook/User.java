package com.cmput.feelsbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Base64;
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
    private List<FollowUser> following;
    private Bitmap profilePicture;

    public User(String userName, String name, Feed posts){
        this.userName = userName;
        this.name = name;
        this.posts = posts;
        //this.following = following;
    }


    /**
     * Send the accepted follow request to the users Followers Collection and the senders Following collection
     * @param userId
     *  The username of the user who sent the follow request
     */
    public void acceptFollowRequest(String userId) {
        DocumentReference fromRequest = FirebaseFirestore.getInstance().collection("users").document(getUserName()).collection("followRequests").document(userId);
        fromRequest.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                CollectionReference toFollowing = FirebaseFirestore.getInstance().collection("users").document(userId).collection("following");
                CollectionReference toFollowers = FirebaseFirestore.getInstance().collection("users").document(getUserName()).collection("followers");
                FirebaseFirestore.getInstance().collection("users").document(getUserName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            toFollowing.document(getUserName()).set(new FollowUser(documentSnapshot.getId(),(String) documentSnapshot.getData().get("name"), null));
                        }
                    }
                });
                toFollowers.document(userId).set(documentSnapshot.getData());
                fromRequest.delete();
            }
        });
    }

    public void declineFollowRequest(String userId) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(getUserName())
                .collection("followRequests")
                .document(userId)
                .delete();
    }

    /**
     * Send a follow request to the specified user
     * @param context
     *      Provides the context to display information to the user
     * @param userId
     *      The name of the user to send the request
     */
    public void sendFollowRequest(Context context, String userId){
        if (userId.equals(getUserName())){
            Toast.makeText(context, "You can't follow yourself.", Toast.LENGTH_SHORT).show();
            return;
        }
        CollectionReference cr = FirebaseFirestore.getInstance().collection("users");
        cr
                .document(getUserName())
                .collection("following")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            // Sender is not following user yet
                            // Sends follow request
                            cr
                                    .document(userId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful() && task.getResult().exists()) {
                                                task.getResult().getReference()
                                                        .collection("followRequests")
                                                        .document(getUserName())
                                                        .set(new FollowUser(getUserName(), getName(), getProfilePic()))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "Data addition successful");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d(TAG, "Data addition failed" + e.toString());
                                                            }
                                                        });

                                            } else {
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

    public void removeFollower(String  userId) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(getUserName())
                .collection("following")
                .document(userId)
                .delete();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("followers")
                .document(getUserName())
                .delete();
    }

    public void removeFollowing(String  userId) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(getUserName())
                .collection("followers")
                .document(userId)
                .delete();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("following")
                .document(getUserName())
                .delete();
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

    public List<FollowUser> getFollowingList() {
        return following;
    }

    public void setFollowingList(List<FollowUser> followsList) {
        following = followsList;
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
