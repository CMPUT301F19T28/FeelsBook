package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SignUp extends AppCompatActivity {

    private Button signupButton;
    private Button cancelButton;
    private EditText nameField;
    private EditText passwordField;
    private EditText usernameField;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        signupButton  = (Button) findViewById(R.id.singupButton);
        cancelButton  = (Button) findViewById(R.id.cancelButton);
        nameField     = (EditText) findViewById(R.id.nameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        usernameField = (EditText) findViewById(R.id.usernameField);

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
            return;
        }

        if (passwordField.getText().length() < 8 ){
            // Invalid password error
            return;
        }

    }




}
