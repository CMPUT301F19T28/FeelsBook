package com.cmput.feelsbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Base64;

/**
 * Creates a signup activity where a new user can be created.
 * Button signupButton, cancelButton - used to confirm or cancel the user creation process
 * EditText nameField, passwordField, usernameField - contains the inputted name, password, and
 * username entered by the user respectively.
 * FirebaseFirestore db - created instance of the database used for storing the created user
 */
public class SignUpActivity extends AppCompatActivity implements ProfilePicFragment.OnPictureSelectedListener{

    private static final String TAG = "SignUpActivity";

    private Button signupButton;
    private Button cancelButton;
    private EditText nameField;
    private EditText passwordField;
    private EditText usernameField;
    private ImageView profilePicView;
    private FirebaseFirestore db;
    private Client client;
    private Index index;
    private ProfilePicFragment chooseProfile;
    private Bitmap chosenPic;

    private final String SIGNUP_TAG = "Invalid field";
    public static final String USER = "com.cmput.feelsbook.SignUpActivity.User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);


        client = new Client("CZHQW4KJVA","394205d7e7f173719c08f3e187b2a77b");
        index = client.getIndex("users");
        signupButton    = findViewById(R.id.confirm_signup);
        cancelButton    = findViewById(R.id.cancel_signup);
        nameField       = findViewById(R.id.s_name_text);
        passwordField   = findViewById(R.id.s_password_text);
        usernameField   = findViewById(R.id.s_user_text);
        profilePicView  = findViewById(R.id.set_profile_pic);

        nameField.getText().clear();
        passwordField.getText().clear();
        usernameField.getText().clear();

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
                                createUser(username);
                                try {
                                    index.addObjectAsync(new JSONObject().put("username",username), username, null);
                                }catch (Exception e) {
                                    Log.d(TAG, "Error");
                                }
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
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profilePicView.setOnClickListener(view -> {
            // add profile pic fragment
            chooseProfile = new ProfilePicFragment();
            chooseProfile.show(getSupportFragmentManager(), "PICTURE_CHOOSE");
        });

    }

    @Override
    public void onResume(){
        super.onResume();

        if(chosenPic != null){
            profilePicView.setImageBitmap(chosenPic);
        }
    }

    /**
     *
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

    /**
     * Create a new document with the entered username as doc ID
     * Fill in name and password entries into the document
     * @param username
     *  The username the user has entered
     */
    private void createUser(String username){
        final String password = passwordField.getText().toString();
        final String name = nameField.getText().toString();

        try{
            byte[] encodedHash = getHash(password);
            String hashedPassword = bytesToHex(encodedHash);

            HashMap<String, String> data = new HashMap<>();
            data.put("password", hashedPassword);
            data.put("name", name);
            data.put("total_posts","0");
            try {
                // encodes the profile picture taken from the user document
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                chosenPic.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] picData = baos.toByteArray();
                data.put("profilePic", Base64.getEncoder().encodeToString(picData));

            } catch (Exception e) {
                Log.d("-----UPLOAD PHOTO-----",
                        "**NO profilepic UPLOADED: " + e);
            }

            db.collection("users")
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

        }
        catch (NoSuchAlgorithmException e){
            Log.d(TAG, "Exception thrown for incorrect algorithm " + e);
        }
    }

    private static byte[] getHash(String password) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            } else {
                hexString.append(hex);
            }
        }
        return hexString.toString();
    }

    /**
     * Receives the profile picture chosen by the user during sign-up
     * @param picture
     */
    public void onPictureSelect(Bitmap picture){
        chosenPic = picture;
    }
}
