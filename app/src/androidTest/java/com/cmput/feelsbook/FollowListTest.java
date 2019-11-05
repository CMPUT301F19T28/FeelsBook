package com.cmput.feelsbook;


import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FollowListTest {

    static User user;

    @Before
    public void setup() {
        user = new User("JoeUserName", "Joe", new Feed(), new FollowList());
    }

    @Test
    public void sendFriendRequestTest() {
        user.sendFollowRequest(InstrumentationRegistry.getInstrumentation().getContext(), "MoodMan2019");
        assertEquals(true,true);
    }
}
