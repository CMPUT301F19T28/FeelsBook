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

    /**
     * Create a Mood object with a different Date
     * @param dateTime
     * The Date that the post should be associated
     * @param moodType
     * Holds the associated emoji and colour for the Mood
     * @param profilePic
     * The user who made the posts
     */
    public Mood(Date dateTime, MoodType moodType, Bitmap profilePic) {
        this.dateTime = dateTime;
        this.moodType = moodType;
        this.profilePic = profilePic;
    }

    /**
     * Builder style to add a reason to the Mood
     * @param reason
     * A short description of why the user is in the given Mood
     * @return
     * The Mood with the reason added
     */
    public Mood withReason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * Builder style to add a situation to the Mood
     * @param situation
     * A category describing how many people they are with
     * @return
     * The Mood with the situation added
     */
    public Mood withSituation(SocialSituation situation) {
        this.situation = situation;
        return this;
    }

    /**
     * Builder style to add a situation to the Mood
     * @param photo
     * A photo to show something about their Mood
     * @return
     * The mood with a Photo added
     */
    public Mood withPhoto(Bitmap photo) {
        this.photo = photo;
        return this;
    }

    /**
     * Builder style to add a location to the Mood
     * @param location
     * A location where the user is posting their Mood
     * @return
     * The mood with a Location added
     */
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
