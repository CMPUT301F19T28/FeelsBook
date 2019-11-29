package com.cmput.feelsbook;

import java.io.Serializable;

public class FollowUser implements Serializable {

    private String userName;
    private String name;
    private String profilePic;

    public FollowUser(String userName, String name) {
        this.userName = userName;
        this.name = name;
        this.profilePic = null;
    }

    public FollowUser(String userName, String name, String profilePic) {
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

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
