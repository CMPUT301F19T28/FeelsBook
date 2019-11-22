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
    private FollowList followList;
    private Bitmap profilePicture;

    public User(String userName, String name, Feed posts, FollowList followsList){
        this.userName = userName;
        this.name = name;
        this.posts = posts;
        followList = followsList;
    }


    /**
     * Send the accepted follow request to the users Followers Collection and the senders Following collection
     * @param userId
     *  The username of the user who sent the follow request
     */
    public void acceptFollowRequest(String userId) {
        DocumentReference fromRequest = FirebaseFirestore.getInstance().collection("users").document(getUserName()).collection("FollowRequests").document(userId);
        DocumentReference toFollowers = FirebaseFirestore.getInstance().collection("users").document(getUserName()).collection("Followers").document(userId);
        moveFirestoreDoc(fromRequest, toFollowers);
        DocumentReference toFollowing = FirebaseFirestore.getInstance().collection("users").document(userId).collection("Following").document(getUserName());
        moveFirestoreDoc(fromRequest, toFollowing);

        fromRequest.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Doc deleted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Doc failed to delete successfully", e);
                    }
                });
    }

    public void moveFirestoreDoc (DocumentReference fromCollection, DocumentReference toCollection){
            fromCollection.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc != null) {
                            toCollection.set(doc.getData())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Doc successfully written");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Failure writing Doc", e);
                                        }
                                    });
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
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
                .collection("Following")
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
                                                Map<String, Object> data = new HashMap<>();
                                                try{
                                                    Bitmap bitmap = getProfilePic();
                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                    byte[] picData = baos.toByteArray();
                                                    data.put("photo", Base64.getEncoder().encodeToString(picData));

                                                } catch (Exception e){
                                                    Log.d("-----UPLOAD PHOTO-----",
                                                            "****NO PHOTO UPLOADED: " + e);
                                                    data.put("photo", null);
                                                }
                                                data.put("fullname", getName());
                                                data.put("username", getUserName());
                                                task.getResult().getReference()
                                                        .collection("FollowRequests")
                                                        .document(getUserName())
                                                        .set(data)
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
