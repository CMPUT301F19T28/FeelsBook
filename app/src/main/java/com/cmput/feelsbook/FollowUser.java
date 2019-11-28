package com.cmput.feelsbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class FollowUser implements Serializable {

    private String userName;
    private String name;
    private String profilePic;

    public FollowUser() {

    }

    public FollowUser(String userName, String name, Bitmap profilePic) {
        this.userName = userName;
        this.name = name;
        this.profilePic = profilePicString(profilePic);
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

    public Bitmap profilePicBitmap() {
        if(profilePic != null) {
            byte[] photo = Base64.getDecoder().decode(profilePic);
            return BitmapFactory.decodeByteArray(photo, 0, photo.length);
        }
        return null;
    }

    public static String profilePicString(Bitmap bitmap) {
        if(bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return Base64.getEncoder().encodeToString(stream.toByteArray());
        }
        return null;
    }
}
