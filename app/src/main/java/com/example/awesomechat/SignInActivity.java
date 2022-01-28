package com.example.awesomechat;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpLogInButton = findViewById(R.id.signUpLogInButton);
        toggleTextView = findViewById(R.id.toggleTextView);

        signUpLogInButton.setOnClickListener(view -> signUpLogInUser(emailEditText.getText().toString().trim(),
                passwordEditText.getText().toString().trim()));
    }

    private void signUpLogInUser(String email, String password) {

        if (isLogInModeActive) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            //updateUI(user);
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }
                    });
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignInActivity.this, "User created", Toast.LENGTH_LONG).show();
                            FirebaseUser user = auth.getCurrentUser();
                            //updateUI(user);
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
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