package com.cmput.feelsbook.post;

import com.cmput.feelsbook.R;

/**
 * Contains the emoticon used by each MoodType.
 */
public enum MoodType {


    HAPPY(R.string.happy,"yellow"),
    SAD(R.string.sad,"blue"),
    ANGRY(R.string.angry, "red"),
    SLEEPY(R.string.sleepy,"purple"),
    ANNOYED(R.string.annoyed, "orange"),
    SEXY(R.string.sexy,"pink");


    private final String color;
    private final int emoticon;

    MoodType(int emoticon, String color) {
        this.color =  color;
        this.emoticon = emoticon;
    }

    public String getColor() {
        return color;
    }

    public int getEmoticon() {
        return emoticon;
    }
}