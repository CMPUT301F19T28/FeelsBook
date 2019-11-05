package com.cmput.feelsbook;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private Feed posts;
    private FollowList followList;
    private Bitmap profilePicture;

    public User(String name, FollowList followsList){
        this.name = name;
        //this.posts = posts;
        followList = followsList;
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
}
