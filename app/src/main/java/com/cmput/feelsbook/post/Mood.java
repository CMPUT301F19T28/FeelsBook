package com.cmput.feelsbook.post;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cmput.feelsbook.R;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

/**
 * Object that contains information about a mood.
 * MoodType moodType - specifies which mood to be displayed as an emoticon
 * String reason - additional information about the mood added by the user
 * SocialSituation situation - specifies the social situation regarding the mood
 * Bitmap photo - additional context image added to the mood
 * Location location - contextual information about the place the mood was experienced
 * DateFormat dateFormatter - specifies the date format used when displaying the mood
 */

public class Mood extends Post implements Serializable {

    private final static DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.CANADA);

    private MoodType moodType;
    private String reason;
    private SocialSituation situation;
    private String photo;
    private double latitude;
    private double longitude;
    private String user;


    public Mood() {
        this.dateTime = new Date();
    }

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
        this.profilePic = profilePicString(profilePic);
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
        this.profilePic = profilePicString(profilePic);
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
        this.photo = photoString(photo);
        return this;
    }

    /**
     * Builder style to add a location to the Mood
     * @param latitude
     * @param longitude
     * A location where the user is posting their Mood
     * @return
     * The mood with a Location added
     */
    public Mood withLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        return this;
    }


    public Mood withUser(String username){
        this.user = username;
        return this;
    }

    /**
     * Provides context for the fields in a mood feed item.
     * @Param viewHolder Contains the view for the mood feed item which has the other views
     */
    @Override
    public void displayPost(RecyclerView.ViewHolder viewHolder) {
        TextView dateTimeText = viewHolder.itemView.findViewById(R.id.dateText);
        TextView moodText = viewHolder.itemView.findViewById(R.id.moodText);
        ImageView profile_pic_feed = viewHolder.itemView.findViewById(R.id.profileImage);
        TextView username = viewHolder.itemView.findViewById(R.id.user_name);

        viewHolder.itemView.setBackgroundColor(Color.parseColor(moodType.getColor()));
        dateTimeText.setText(dateFormatter.format(dateTime));
        moodText.setText(moodType.getEmoticon());
        profile_pic_feed.setImageBitmap(profilePicBitmap());
        username.setText(user);

        if(reason != null) {
            TextView reasonText = viewHolder.itemView.findViewById(R.id.reasonText);
            reasonText.setText(reason);
        }
/*        if(situation != null) {
            TextView situationText = viewHolder.itemView.findViewById(R.id.situation_feed);
            situationText.setText(situation.toString());
        }
        if(photo != null) {
            ImageView photoFeed = viewHolder.itemView.findViewById(R.id.photo_feed);
            photoFeed.setImageBitmap(photoBitmap());
        }

 */
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

    //returns true if a situation has been set false otherwise
    public boolean hasSituation(){
        return situation != null;
    }

    public boolean hasPhoto(){
        return photo != null;
    }

    public boolean hasLocation() {return (latitude != 0 && longitude != 0);}

    public void setSituation(SocialSituation situation) {
        this.situation = situation;
    }

    public Bitmap photoBitmap() {
        if(photo != null) {
            byte[] photo = Base64.getDecoder().decode(this.photo);
            return BitmapFactory.decodeByteArray(photo, 0, photo.length);
        }
        return null;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static String photoString(Bitmap bitmap) {
        if(bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return Base64.getEncoder().encodeToString(stream.toByteArray());
        }
        return null;
    }
}
