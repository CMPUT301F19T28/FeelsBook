package com.cmput.feelsbook;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPageActivity extends AppCompatActivity {
    private EditText userField;
    private EditText passField;
    private Button loginButton;
    private TextView signupPrompt;
    private String signupMessage = "Don't have an account? Sign up";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Basic login screen features:
         * - button for login
         * - text fields for username and password
         * - prompt for register account
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        userField = findViewById(R.id.user_text);
        passField = findViewById(R.id.password_text);
        loginButton = findViewById(R.id.confirm_login);
        signupPrompt = findViewById(R.id.sign_up_prompt);

        // creates clickable portion of sign-up prompt
        SpannableString spanString = new SpannableString(signupMessage);
        ClickableSpan clickSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(LoginPageActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        };
        /*loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPageActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
         */

        spanString.setSpan(clickSpan,23,30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupPrompt.setText(spanString);
        signupPrompt.setMovementMethod(LinkMovementMethod.getInstance());

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("users");

        // TO RESOLVE
        // check Firebase for existing user.
        // what behaviors when user is not found?
        // when user found, quit LoginPageActivity and launch MainActivity with the User object



    }
}
