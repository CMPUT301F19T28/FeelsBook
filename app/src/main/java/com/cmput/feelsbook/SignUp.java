package com.cmput.feelsbook;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    private Button signupButton;
    private Button cancelButton;
    private EditText nameField;
    private EditText passwordField;
    private EditText usernameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        signupButton  = (Button) findViewById(R.id.singupButton);
        cancelButton  = (Button) findViewById(R.id.cancelButton);
        nameField     = (EditText) findViewById(R.id.nameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        usernameField = (EditText) findViewById(R.id.usernameField);



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRequirements();  // Checks field requirements


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
     *  The password contains at least one uppercase and one number
     */
    private void checkRequirements(){
        if (nameField.getText().length() == 0){

        }
        else if (passwordField.getText().length() == 0){

        }
        else if (usernameField.getText().length() == 0){

        }

    }

}
