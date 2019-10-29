package com.cmput.feelsbook;

import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;


import static org.junit.jupiter.api.Assertions.*;

public class FeedTest {

    private static Feed feed;
    private static Mood happy;
    private static Mood sad;
    private static Mood annoyed;

    @BeforeAll
    static void setup() {
        happy = new Mood(MoodType.HAPPY, null);
        sad = new Mood(MoodType.SAD, null);
        annoyed = new Mood(MoodType.ANNOYED, null);
    }

    @BeforeEach
    void start() {
        feed = new Feed();
    }

    @Test
    void addMoodTest() {
        assertEquals(0, feed.getItemCount());
        feed.addPost(happy);
        assertEquals(1, feed.getItemCount());
        assertEquals(happy, feed.getPost(0));
        feed.addPost(sad);
        assertEquals(2, feed.getItemCount());
        assertEquals(sad, feed.getPost(1));
    }

    @Test
    void removeMoodTest() {
        feed.addPost(annoyed);
        assertEquals(1, feed.getItemCount());
        feed.removePost(0);
        assertEquals(0, feed.getItemCount());
        assertThrows(IndexOutOfBoundsException.class, () -> {feed.getPost(0);});
        feed.addPost(happy);
        assertEquals(1, feed.getItemCount());
        feed.removePost(happy);
        assertEquals(0, feed.getItemCount());
    }

    @Test
    void getMoodTest() {
        feed.addPost(happy);
        assertEquals(happy, feed.getPost(0));
        feed.addPost(sad);
        assertEquals(sad, feed.getPost(1));
        assertNotEquals(sad, feed.getPost(0));
    }

    @Test
    void moodTypeTest() {
        assertEquals(MoodType.HAPPY.getColor(), happy.getMoodType().getColor());
        assertEquals(MoodType.ANNOYED.getEmoticon(), annoyed.getMoodType().getEmoticon());
        assertEquals(MoodType.ANNOYED, annoyed.getMoodType());
        assertNotEquals(MoodType.ANGRY, annoyed.getMoodType());
    }

    @Test
    void dateTest() {
        Date date = new Date();
        Mood testMood = new Mood(date, MoodType.SLEEPY, null);
        assertEquals(date, testMood.getDateTime());
        assertNotEquals(date, happy.getDateTime());
    }
}
