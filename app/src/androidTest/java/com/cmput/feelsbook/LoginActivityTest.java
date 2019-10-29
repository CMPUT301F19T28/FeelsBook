package com.cmput.feelsbook;

import android.app.Activity;
import android.widget.EditText;
import android.widget.ListView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<LoginPageActivity> rule =
            new ActivityTestRule<>(LoginPageActivity.class, false, false);

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

    @Test
    public void checkLogin(){
        // Asserts that the current activity is the Mainactivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LoginPageActivity.class);

        solo.clickOnButton("CONFIRM"); //Click ADD City buttton

        try{
            solo.wait(2);
        }catch(Exception e){
            //
        }

        solo.assertCurrentActivity("Didn't Work", MainActivity.class);
    }

}
