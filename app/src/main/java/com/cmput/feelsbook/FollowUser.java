package com.cmput.feelsbook;

import android.graphics.Bitmap;
import java.io.Serializable;

public class FollowUser implements Serializable {

    private String userName;
    private String name;
    private Bitmap profilePic;

    public FollowUser() {

    }

    public FollowUser(String userName, String name, Bitmap profilePic) {
        this.userName = userName;
        this.name = name;
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }
}
