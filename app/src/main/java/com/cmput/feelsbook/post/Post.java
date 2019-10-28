package com.cmput.feelsbook.post;

import android.graphics.Bitmap;

import com.cmput.feelsbook.Feed;

import java.util.Date;

public abstract class Post {

    protected Bitmap profilePic;
    protected Date dateTime;

    public abstract void displayPost(Feed.ViewHolder viewHolder);

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}