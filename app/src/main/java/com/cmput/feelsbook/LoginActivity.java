package com.cmput.feelsbook;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

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

        db = FirebaseFirestore.getInstance();  // Create an instance to access Cloud Firestore
        userField = findViewById(R.id.user_text);
        passField = findViewById(R.id.password_text);
        loginButton = findViewById(R.id.confirm_login);
        signupPrompt = findViewById(R.id.sign_up_prompt);

        // creates clickable portion of sign-up prompt
        SpannableString spanString = new SpannableString(signupMessage);
        ClickableSpan clickSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        };
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userField.getText().toString();

                DocumentReference docRef = db.collection("users").document(username);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String saved_pass = document.getString("password");
<<<<<<< HEAD
                                String password = passField.getText().toString();
                                if (password.equals(saved_pass)) {
                                    Toast.makeText(LoginActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();
                                    User user = new User(document.getString("name"), new FollowList());

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("User",user);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
=======
                                try {
                                    byte[] encodedHash = getHash(passField.getText().toString());
                                    String hashedPassword = bytesToHex(encodedHash);

                                    if (hashedPassword.equals(saved_pass)) {
                                        successfulLogin(document);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NoSuchAlgorithmException e){
                                    Log.d(TAG, "Exception thrown for incorrect algorithm " + e);
>>>>>>> 40750d77ef61f35a11472768b0235d8cf08eeaad
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Failed with:  ", task.getException());
                        }
                    }
                });
            }
        });

        spanString.setSpan(clickSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupPrompt.setText(spanString);
        signupPrompt.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private static byte[] getHash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    private static String bytesToHex(byte[] hash){
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++){
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() ==  1) {
                hexString.append('0');
            } else {
                hexString.append(hex);
            }
        }
        return  hexString.toString();
    }

    private void successfulLogin(DocumentSnapshot document){
        Toast.makeText(LoginActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();
        User user = new User(document.getId(), document.getString("name"), new Feed(), new FollowList());
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("User",user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
