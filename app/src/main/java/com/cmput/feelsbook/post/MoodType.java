package com.cmput.feelsbook.post;

import com.cmput.feelsbook.R;

/**
 * Contains the emoticon used by each MoodType.
 **/
public enum MoodType {


    HAPPY(R.string.happy,"#FFFF80"),
    SAD(R.string.sad,"#80B3FF"),
    ANGRY(R.string.angry,"#FF6666"),
    SLEEPY(R.string.sleepy,"#DF80FF"),
    ANNOYED(R.string.annoyed,"#FFB380"),
    SEXY(R.string.sexy,"#FFCCF2");


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

    public static MoodType getMoodType(String moodtype){

        if(moodtype.toLowerCase().equals("happy"))
            return MoodType.HAPPY;

        else if(moodtype.toLowerCase().equals("sad"))
            return MoodType.SAD;

        else if(moodtype.toLowerCase().equals("angry"))
            return MoodType.ANGRY;

        else if(moodtype.toLowerCase().equals("sleepy"))
            return MoodType.SLEEPY;

        else if(moodtype.toLowerCase().equals("annoyed"))
            return MoodType.ANNOYED;

        else
            return MoodType.SEXY;

    }
}