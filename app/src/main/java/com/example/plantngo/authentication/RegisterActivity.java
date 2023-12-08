package com.example.plantngo.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plantngo.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * The RegisterActivity class handles user registration, email verification,
 * and navigation to the login activity.
 */
public class RegisterActivity extends AppCompatActivity {

    // UI elements
    private EditText newEmailEditText;
    private EditText newPasswordEditText;
    private EditText newConfirmPasswordEditText;

    private TextView verifyMsg;
    private Button verifyEmailButton;

    // Firebase Authentication instance
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views and Firebase Authentication instance
        newEmailEditText = findViewById(R.id.new_email_editText);
        newPasswordEditText = findViewById(R.id.new_password_editText);
        newConfirmPasswordEditText = findViewById(R.id.confirm_password_editText);
        Button registerButton = findViewById(R.id.register_button);

        fAuth = FirebaseAuth.getInstance();

        verifyMsg = findViewById(R.id.verify_email_textView);
        verifyEmailButton = findViewById(R.id.verify_email_button);

        verifyMsg.setVisibility(View.INVISIBLE);
        verifyEmailButton.setVisibility(View.INVISIBLE);

        // Set click listener for the register button
        registerButton.setOnClickListener(v -> {
            // Get the entered email and password
            String newEmail = newEmailEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = newConfirmPasswordEditText.getText().toString();

            // Validate input fields
            if (newEmail.isEmpty()) {
                newEmailEditText.setError("Email is required");
                return;
            }
            if (newPassword.isEmpty()) {
                newPasswordEditText.setError("Password is required");
                return;
            }
            if (confirmPassword.isEmpty()) {
                newConfirmPasswordEditText.setError("Confirm Password is required");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                newConfirmPasswordEditText.setError("Password does not match!");
                return;
            }

            // Show a toast for validated data
            Toast.makeText(RegisterActivity.this, "Data Validated", Toast.LENGTH_SHORT).show();

            // Create a new user with email and password
            fAuth.createUserWithEmailAndPassword(newEmail, newPassword)
                    .addOnSuccessListener(authResult -> {
                        // Display verification message and button
                        verifyMsg.setVisibility(View.VISIBLE);
                        verifyEmailButton.setVisibility(View.VISIBLE);

                        // Set click listener for the verify email button
                        verifyEmailButton.setOnClickListener(v1 -> {
                            // Send email verification
                            sendEmailVerification();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to create a new user
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    /**
     * Sends a verification email to the user's registered email address.
     * If successful, displays a success message and navigates to the login activity.
     */
    private void sendEmailVerification() {
        Objects.requireNonNull(fAuth.getCurrentUser()).sendEmailVerification()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(RegisterActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                    fAuth.signOut();
                    navigateToLoginActivity();
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to send the verification email
                    Toast.makeText(RegisterActivity.this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Navigates to the login activity.
     */
    private void navigateToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}

