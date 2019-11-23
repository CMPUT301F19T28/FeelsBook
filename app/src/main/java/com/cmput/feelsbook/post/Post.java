package com.cmput.feelsbook.post;

import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.Feed;

import java.io.Serializable;
import java.util.Date;

/**
 * Base for displaying a post on the user's feed.
 * Bitmap profilePic - profile picture of the user who created the Post object
 * Date dateTime - date and time used to display the Post
 */
public abstract class Post implements Serializable{

    protected Bitmap profilePic;
    protected Date dateTime;

    public abstract void displayPost(RecyclerView.ViewHolder viewHolder);

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

    @Override
    public String toString(){
        return getClass().getName()+"@"+Integer.toHexString(dateTime.hashCode());
    }
}