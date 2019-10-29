package com.cmput.feelsbook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private Button signupButton;
    private Button cancelButton;
    private EditText nameField;
    private EditText passwordField;
    private EditText usernameField;
    private FirebaseFirestore db;

    private final String SIGNUP_TAG = "Invalid field";
    public static final String USER = "com.cmput.feelsbook.signUpActivity.User";

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
                Boolean result = checkRequirements();  // Checks field requirements

                if (result == false){
                    return;
                }

                final String username = usernameField.getText().toString();

                DocumentReference docRef = db.collection("users").document(username);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (!doc.exists()) {
                                final String password = passwordField.getText().toString();
                                final String name = nameField.getText().toString();


                                HashMap<String, String> data = new HashMap<>();
                                data.put("password", password);
                                data.put("name", name);

                                collectionReference
                                        .document(username)
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "User creation successful");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "User creation failed", e);
                                            }
                                        });

                                finish();
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, "Username is not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Log.d(TAG, "Failed with:   ", task.getException());
                        }
                    }
                });


                /*
                Structure of the database:

                collectionReference.document(username).collection("data").document("Followers")
                collectionReference.document(username).collection("data").document("Following")
                collectionReference.document(username).collection("data").document("Follow_Requests")
                collectionReference.document(username).collection("data").document("Mood_History")
                collectionReference.document(username).collection("data").document("Mood_History").collection("History").document()
                 */

                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                User user = new User("john", new Feed(), new FollowList());
                intent.putExtra(USER, user);
                startActivity(intent);
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
    private Boolean checkRequirements(){
        if (nameField.getText().length() == 0 ||
                passwordField.getText().length() == 0 ||
                usernameField.getText().length() == 0 ) {
            Log.d(TAG, "A required field is not filled");
            Toast.makeText(SignUpActivity.this, "Required field empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordField.getText().length() < 8 ){
            Log.d(TAG, "Invalid password length");
            Toast.makeText(SignUpActivity.this, "Invalid password length", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }
}
