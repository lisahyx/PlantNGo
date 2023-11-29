package com.example.plantngo.authentication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantngo.R;
import com.example.plantngo.main.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * The activity for user login.
 */
public class LoginActivity extends AppCompatActivity {

    // Constants
    public static final int GOOGLE_SIGN_IN_CODE = 10005;

    // Views
    private EditText emailEditText;
    private EditText passwordEditText;
    private AlertDialog.Builder resetAlert;
    private LayoutInflater inflater;

    // Firebase
    private FirebaseAuth fAuth;
    public static String userID;

    // Google Sign In
    private GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        Button loginButton = findViewById(R.id.login_button);
        TextView signUpTextView = findViewById(R.id.sign_up_textView);
        TextView forgotPasswordTextView = findViewById(R.id.forgotPassword_textView);

        // Initialize Firebase Authentication
        fAuth = FirebaseAuth.getInstance();

        // Check if user is signed in
        if (fAuth.getCurrentUser() != null) {
            userID = fAuth.getCurrentUser().getUid();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        // Reset password alert
        resetAlert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        // Google Sign In
        SignInButton googleSignIn = findViewById(R.id.google_sign_in);

        // Configure Google Sign In
        configureGoogleSignIn();

        // Check if user is already signed in with Google
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null || fAuth.getCurrentUser() != null) {
            Toast.makeText(this, "User is already logged in", Toast.LENGTH_SHORT).show();
        }

        // Google Sign In button click listener
        googleSignIn.setOnClickListener(v -> {
            Intent gSign = signInClient.getSignInIntent();
            startActivityForResult(gSign, GOOGLE_SIGN_IN_CODE);
        });

        // Login button click listener
        loginButton.setOnClickListener(v -> loginUser());

        // Forgot password TextView click listener
        forgotPasswordTextView.setOnClickListener(v -> showResetPasswordDialog());

        // Sign up TextView click listener
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Configures Google Sign In options and client.
     */
    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("323059889443-kf5q8v43fr0pns0vl1q0kna5fo0sjfkj.apps.googleusercontent.com")
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Attempts to log in the user using Firebase Email/Password authentication.
     */
    private void loginUser() {
        // Get the entered email and password
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty()) {
            emailEditText.setError("Email is missing");
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is missing");
            return;
        }

        // Attempt Firebase Email/Password authentication
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    userID = fAuth.getCurrentUser().getUid();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Displays a dialog for resetting the user's password.
     */
    private void showResetPasswordDialog() {
        // Show reset password dialog
        View view = inflater.inflate(R.layout.reset_popup, null);

        resetAlert.setTitle("Reset Password?").setMessage("Enter your email to get a password reset link")
                .setPositiveButton("Reset", (dialogInterface, i) -> {
                    // Validate email address
                    EditText email = view.findViewById(R.id.reset_email);
                    if (email.getText().toString().isEmpty()) {
                        email.setError("Required field");
                        return;
                    }

                    // Send reset link
                    fAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnSuccessListener(unused -> Toast.makeText(LoginActivity.this, "Reset Email Sent", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                }).setNegativeButton("Cancel", null)
                .setView(view).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount signInAccount = signInTask.getResult(ApiException.class);

                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                // Sign in with Google credential
                fAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                    Toast.makeText(getApplicationContext(), "Your Google Account is Connected to Our Application", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show());
            } catch (ApiException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
