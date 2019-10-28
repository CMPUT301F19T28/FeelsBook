package com.cmput.feelsbook.post;

<<<<<<< HEAD
import com.cmput.feelsbook.Feed;

public abstract class Post {

    public abstract void displayPost(Feed.ViewHolder viewHolder);
}
=======
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
>>>>>>> 600051d28804c10d8677ea7f1984eb319ea7cf40
