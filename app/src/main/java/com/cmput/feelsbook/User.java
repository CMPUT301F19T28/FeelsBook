package com.cmput.feelsbook;

import android.graphics.Bitmap;

public class User {
    private String name;
    private Feed posts;
    private FollowList followsList;
    private Bitmap profilePicture;

    public User(String name, Feed posts, FollowList followsList){
        this.name = name;
        this.posts = posts;
        this.followsList = followsList;
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
        return followsList;
    }

    public void setFollowsList(FollowList followsList) {
        this.followsList = followsList;
    }


    public Bitmap getProfilePic(){
        return this.profilePicture;
    }
}