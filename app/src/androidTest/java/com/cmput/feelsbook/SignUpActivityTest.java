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

public class SignUpActivityTest {

    private FirebaseFirestore db;
    private Solo solo;

    @Rule
    public ActivityTestRule<SignUpActivity> rule =
            new ActivityTestRule<>(SignUpActivity.class, true, true);

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
    public void addNewUser(){
        // Asserts that the current activity is SignUpActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document("MoodMan2019");

        solo.enterText((EditText) solo.getView(R.id.s_name_text), "Daylen Pacheco");
        solo.enterText((EditText) solo.getView(R.id.s_user_text), "MoodMan2019");
        solo.enterText((EditText) solo.getView(R.id.s_password_text), "passwords123");
        solo.clickOnButton("CONFIRM");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assertTrue(doc.exists());
                }
            }
        });
    }

    @Test
    public void missingFieldTest(){
        // Asserts that the current activity is SignUpActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        solo.enterText((EditText) solo.getView(R.id.s_name_text), "Daylen Pacheco");
        solo.enterText((EditText) solo.getView(R.id.s_password_text), "passwords123");
        solo.clickOnButton("CONFIRM");

        assertTrue(solo.waitForText("Required", 1, 1000));
    }

    @Test
    public void invalidPassTest(){
        // Asserts that the current activity is SignUpActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        solo.enterText((EditText) solo.getView(R.id.s_name_text), "Daylen Pacheco");
        solo.enterText((EditText) solo.getView(R.id.s_user_text), "MoodMan2019");
        solo.enterText((EditText) solo.getView(R.id.s_password_text), "passwor");
        solo.clickOnButton("CONFIRM");

        assertTrue(solo.waitForText("Invalid", 1, 1000));
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
