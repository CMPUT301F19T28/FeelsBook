package com.cmput.feelsbook;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {

    private Button signupButton;
    private Button cancelButton;
    private EditText nameField;
    private EditText passwordField;
    private EditText usernameField;
    private FirebaseFirestore db;
    private final String SIGNUP_TAG = "Invalid field";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        signupButton  = findViewById(R.id.confirm_signup);
        cancelButton  = findViewById(R.id.cancel_signup);
        nameField     = findViewById(R.id.s_name_text);
        passwordField = findViewById(R.id.s_password_text);
        usernameField = findViewById(R.id.s_user_text);

        db = FirebaseFirestore.getInstance();  // Create an instance to access Cloud Firestore
        final CollectionReference collectionReference = db.collection("users");


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRequirements();  // Checks field requirements

                final String username = usernameField.getText().toString();
                final String password = passwordField.getText().toString();
                final String name     = nameField.getText().toString();

                HashMap<String, String> data = new HashMap<>();
                data.put("password", password);
                data.put("name", name);

                collectionReference
                        .document(username)
                        .set(data);

                // TO RESOLVE
                // in here: close SignUpActivity and start MainActivity.
                /**
                 * When signup button is made, document is made with title - username and
                 * data with password and name.
                 * Next step: create a user with the data and send to MainActivity
                 */
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Checks the requirements of the field:
     *  All the fields are not empty
     *  The length of the password is at least a length of 8
     */
    private void checkRequirements(){
        if (nameField.getText().length() == 0 ||
                passwordField.getText().length() == 0 ||
                usernameField.getText().length() == 0 ) {
            Log.d(SIGNUP_TAG, "You must fill out all of the information provided. Please" +
                    " try again.");
        }
        else if (passwordField.getText().length() < 8 ){
            // Invalid password error
            Log.d(SIGNUP_TAG, "Your password must be longer than 8 characters. Please try " +
                    "again.");
        }
    }
}
