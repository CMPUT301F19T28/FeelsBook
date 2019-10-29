package com.cmput.feelsbook;

import android.app.Activity;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class LoginActivityTest {

    private FirebaseFirestore db;
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the activity.
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void badUserTest(){
        // Asserts that the current activity is LoginActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.user_text), "badUsername123123");
        solo.enterText((EditText) solo.getView(R.id.password_text), "passwords123");
        solo.clickOnButton("CONFIRM");

        assertTrue(solo.waitForText("Invalid", 1, 1000));
    }

    @Test
    public void badPasswordTest(){
        // Asserts that the current activity is LoginActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.user_text), "passwordTests");
        solo.enterText((EditText) solo.getView(R.id.password_text), "passwords123");
        solo.clickOnButton("CONFIRM");

        assertTrue(solo.waitForText("Invalid", 1, 1000));
    }

    @Test
    public void goodPasswordTest(){
        // Asserts that the current activity is LoginActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.user_text), "passwordTests");
        solo.enterText((EditText) solo.getView(R.id.password_text), "thisIsATestUser");
        solo.clickOnButton("CONFIRM");

        assertTrue(solo.waitForText("Successful", 1, 3000));
    }

    /**
     * Close activty after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }


}
