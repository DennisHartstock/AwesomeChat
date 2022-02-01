package com.example.awesomechat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private FirebaseAuth auth;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpLogInButton;
    private TextView toggleTextView;
    private boolean isLogInModeActive;
    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://awesome-chat-72589-default-rtdb.europe-west1.firebasedatabase.app/");
        usersDatabaseReference = database.getReference().child("users");

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpLogInButton = findViewById(R.id.signUpLogInButton);
        toggleTextView = findViewById(R.id.toggleTextView);

        signUpLogInButton.setOnClickListener(view -> signUpLogInUser(emailEditText.getText().toString().trim(),
                passwordEditText.getText().toString().trim()));

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, UserListActivity.class));
        }
    }

    private void signUpLogInUser(String email, String password) {

        if (isLogInModeActive) {
            if (nameEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            } else if (passwordEditText.getText().toString().trim().length() < 6) {
                Toast.makeText(this, "Password must be at least 6 chars", Toast.LENGTH_LONG).show();
            } else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                intent.putExtra("userName", nameEditText.getText().toString().trim());
                                startActivity(intent);
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();
                                //updateUI(null);
                            }
                        });
            }
        } else {
            if (nameEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            } else if (passwordEditText.getText().toString().trim().length() < 6) {
                Toast.makeText(this, "Password must be at least 6 chars", Toast.LENGTH_LONG).show();
            } else if (!passwordEditText.getText().toString().trim().equals(confirmPasswordEditText.getText().toString().trim())) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show();
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(SignInActivity.this, "User created", Toast.LENGTH_LONG).show();
                                FirebaseUser user = auth.getCurrentUser();
                                createUser(user);
                                Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                intent.putExtra("userName", nameEditText.getText().toString().trim());
                                startActivity(intent);
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();
                                //updateUI(null);
                            }
                        });
            }
        }

    }

    private void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setName(nameEditText.getText().toString().trim());
        user.setEmail(firebaseUser.getEmail());
        user.setId(firebaseUser.getUid());

        usersDatabaseReference.push().setValue(user);
    }

    public void toggleLoginMode(View view) {

        if (isLogInModeActive) {
            signUpLogInButton.setText("sign up");
            toggleTextView.setText("OR LOG IN");
            confirmPasswordEditText.setVisibility(View.VISIBLE);
            isLogInModeActive = false;
        } else {
            signUpLogInButton.setText("log in");
            toggleTextView.setText("OR SIGN UP");
            confirmPasswordEditText.setVisibility(View.GONE);
            isLogInModeActive = true;
        }
    }
}