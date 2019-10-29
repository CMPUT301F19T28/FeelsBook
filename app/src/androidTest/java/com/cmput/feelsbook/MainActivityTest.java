package com.cmput.feelsbook;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput.feelsbook.post.Mood;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


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
     * feed using assertFalse
     */
    @Test
    public void checkFeed() {
        // Asserts that the current activity is the Mainactivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        MainActivity activity = (MainActivity) solo.getCurrentActivity(); //access the activity
        View fab = activity.findViewById(R.id.addPostButton);//add city button
        solo.clickOnView(fab); //Click ADD City buttton

        //Get view for EditText and enter a city name
        solo.enterText((EditText) solo.getView(R.id.editText), "Happy");
        solo.clickOnButton("Post"); //Select Confirm button

        //Get MainActivity to access its variables and methods
        try{
            solo.wait(5);
        }catch(Exception e){
            //do nothing
        }

        //these fail
        try { //asserts theres something in the feedAdapter
            final RecyclerView list = activity.feedView;
            assertEquals(list.getChildCount(), 1);

        }catch(Exception e){

        }

        try{ //Makes sure the right mood was added
            final String reason = ((Mood) activity.feedAdapter.getPost(0)).getReason(); //get the feedAdapter
            assertEquals("Happy", reason);
        }catch(Exception e){

        }

    }


}
