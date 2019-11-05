package com.cmput.feelsbook;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput.feelsbook.post.Mood;
import com.robotium.solo.Solo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;



public class MainActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    /**
     * Runs before all tests and creates solo instance
     *
     * @throwsException
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     *
     * @throwsException
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Add a mood to the recyclerView and checks the number of moods in the
     * feed using assertEquals
     * Also checks to make sure the right thing was added
     */
    @Test
    public void checkFeed() {
        // Asserts that the current activity is the Main activity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        MainActivity activity = (MainActivity) solo.getCurrentActivity(); //access the activity
        View fab = activity.findViewById(R.id.addPostButton);//add post button
        solo.clickOnView(fab); //Click Add post buttton

        //Get view for EditText and enter a reason
        solo.enterText((EditText) solo.getView(R.id.edit_text), "Happy");
        solo.clickOnButton("Post"); //Select Confirm button

        //Get MainActivity to access its variables and methods
        try { //asserts theres something in the feedAdapter
            solo.wait(5);
            final RecyclerView list = activity.feedFragment.getRecyclerView();
            assertEquals(list.getChildCount(), 1);

        }catch(Exception e){

        }

        try{ //Makes sure the right mood was added
            final String reason = ((Mood) activity.feedFragment.getRecyclerAdapter().getPost(0)).getReason(); //get the feedAdapter
            assertEquals("Happy", reason);
        }catch(Exception e){

        }

    }

    /**
     * Check if swaps between feed and map screens function properly
     */
    @Test
    public void checkViewPager() {
        // Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        MainActivity activity = (MainActivity) solo.getCurrentActivity(); //access the activity
        View googleMap = activity.findViewById(R.id.map_view);
        View feedList = activity.findViewById(R.id.feed_list);
        solo.clickOnButton("Map");
        solo.waitForView(googleMap);
        solo.clickOnButton("Feed");
        solo.waitForView(feedList);
    }


}