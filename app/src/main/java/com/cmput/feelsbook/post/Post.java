package com.cmput.feelsbook.post;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.Feed;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Date;

/**
 * Base for displaying a post on the user's feed.
 * Bitmap profilePic - profile picture of the user who created the Post object
 * Date dateTime - date and time used to display the Post
 */
public abstract class Post implements Serializable{

    protected String profilePic;
    protected Date dateTime;

    public Post() {

    }

    public abstract void displayPost(RecyclerView.ViewHolder viewHolder);

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Bitmap profilePicBitmap() {
        if(profilePic != null) {
            byte[] photo = Base64.getDecoder().decode(profilePic);
            return BitmapFactory.decodeByteArray(photo, 0, photo.length);
        }
        return null;
    }

    @Override
    public String toString(){
        return getClass().getName()+"@"+Integer.toHexString(dateTime.hashCode());
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