package com.cmput.feelsbook.post;

import android.location.Location;
import android.media.Image;

import java.util.Date;

public class Mood extends Post {

    private Date dateTime;
    private MoodType moodType;
    private String reason;
    private SocialSituation situation;
    private Image photo;
    private Location location;

    public Mood(MoodType moodType) {
        dateTime = new Date();
        this.moodType = moodType;
    }

    public Mood(Date dateTime, MoodType moodType) {
        this.dateTime = dateTime;
        this.moodType = moodType;
    }

    public Mood withReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Mood withSituation(SocialSituation situation) {
        this.situation = situation;
        return this;
    }

    public Mood withPhoto(Image photo) {
        this.photo = photo;
        return this;
    }

    public Mood withLocation(Location location) {
        this.location = location;
        return this;
    }
    //TODO: Implement display post to format Mood
    @Override
    protected void displayPost() {

    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
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

    public Image getPhoto() {
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
