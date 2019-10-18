package com.cmput.feelsbook.post;

public enum MoodType {


    //TODO: Add all Moods
    Happy('a',"red"),
    Sad('a',"blue");


    private final String color;
    private final char emoticon;

    MoodType(char emoticon, String color) {
        this.color =  color;
        this.emoticon = emoticon;
    }

    public String getColor() {
        return color;
    }

    public char getEmoticon() {
        return emoticon;
    }
}
