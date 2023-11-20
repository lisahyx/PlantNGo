package com.example.plantngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText newEmailEditText;
    private EditText newPasswordEditText;
    private EditText newConfirmPasswordEditText;

    private Button registerButton;

    TextView verifyMsg;
    private Button verifyEmailButton;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        newEmailEditText = findViewById(R.id.new_email_editText);
        newPasswordEditText = findViewById(R.id.new_password_editText);
        newConfirmPasswordEditText = findViewById(R.id.confirm_password_editText);
        registerButton = findViewById(R.id.register_button);

        fAuth = FirebaseAuth.getInstance();

        verifyMsg = findViewById(R.id.verify_email_textView);
        verifyEmailButton = findViewById(R.id.verify_email_button);

        verifyMsg.setVisibility(View.INVISIBLE);
        verifyEmailButton.setVisibility(View.INVISIBLE);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered email and password
                String newEmail = newEmailEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = newConfirmPasswordEditText.getText().toString();

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

                Toast.makeText(RegisterActivity.this, "Data Validated", Toast.LENGTH_SHORT).show();

                fAuth.createUserWithEmailAndPassword(newEmail,newPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        verifyMsg.setVisibility(View.VISIBLE);
                        verifyEmailButton.setVisibility(View.VISIBLE);

                        verifyEmailButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}
