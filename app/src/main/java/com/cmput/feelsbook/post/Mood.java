package com.cmput.feelsbook.post;

import android.graphics.Bitmap;
import android.location.Location;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmput.feelsbook.Feed;
import com.cmput.feelsbook.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Mood extends Post {

    private final static DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.CANADA);

    private MoodType moodType;
    private String reason;
    private SocialSituation situation;
    private Bitmap photo;
    private Location location;


    /**
     * Create a Mood object with the current Date
     * @param moodType
     * Holds the associated emoji and colour for the Mood
     * @param profilePic
     * The user who made the posts
     */
    public Mood(MoodType moodType, Bitmap profilePic) {
        this.dateTime = new Date();
        this.moodType = moodType;
        this.profilePic = profilePic;
    }

    public Mood(Date dateTime, MoodType moodType, Bitmap profilePic) {
        this.dateTime = dateTime;
        this.moodType = moodType;
        this.profilePic = profilePic;
    }

    public Mood withReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Mood withSituation(SocialSituation situation) {
        this.situation = situation;
        return this;
    }

    public Mood withPhoto(Bitmap photo) {
        this.photo = photo;
        return this;
    }

    public Mood withLocation(Location location) {
        this.location = location;
        return this;
    }

    /**
     * Provides context for the fields in a mood feed item.
     * @Param viewHolder Contains the view for the mood feed item which has the other views
     */
    @Override
    public void displayPost(Feed.ViewHolder viewHolder) {
        TextView dateTimeText = viewHolder.itemView.findViewById(R.id.dateText);
        TextView moodText = viewHolder.itemView.findViewById(R.id.moodText);
        ImageView profile_pic_feed = viewHolder.itemView.findViewById(R.id.profileImage);

        dateTimeText.setText(dateFormatter.format(dateTime));
        moodText.setText(moodType.getEmoticon());
        profile_pic_feed.setImageBitmap(this.profilePic);

//        TODO: Implemented but out of scope for sprint 1
//
//        if(reason != null) {
//            TextView reasonText = viewHolder.itemView.findViewById(R.id.reason_feed);
//            reasonText.setText(reason);
//        }
//        if(situation != null) {
//            TextView situationText = viewHolder.itemView.findViewById(R.id.situation_feed);
//            situationText.setText(situation.toString());
//        }
//        if(photo != null) {
//            ImageView photoFeed = viewHolder.itemView.findViewById(R.id.photo_feed);
//            photoFeed.setImageBitmap(photo);
//        }
    }

    public MoodType getMoodType() {
        return moodType;
    }

    public void setMoodType(MoodType moodType) {
        this.moodType = moodType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public SocialSituation getSituation() {
        return situation;
    }

    public void setSituation(SocialSituation situation) {
        this.situation = situation;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
