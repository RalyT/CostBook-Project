package com.comp3617.finalproject.UserLogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.comp3617.finalproject.MainActivity;
import com.comp3617.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Retrieves Firebase Auth instance
        auth = FirebaseAuth.getInstance();

        // UI Elements
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_register);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
    }

    /**
     * Performs User registration or navigates to different Activities.
     * @param v Selected button
     */
    public void onClickNav(View v) {
        switch(v.getId()) {

            // Login Button
            case R.id.btn_sign_in:
                // Go back to Login Page
                finish();
                break;

            // Forgot/Reset Password Button
            case R.id.btn_reset_password:
                startActivity(new Intent(Register.this, ResetPassword.class));
                break;

            // Register Button
            case R.id.btn_register:

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Checks if the Email field is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid Email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Checks if the password field is empty
                else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter a password that is longer than 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Checks if the password is too short
                else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password is too short, please enter minimum 6 characters!", Toast.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // All fields are populated, create the new user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(Register.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds

                                if (!task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Authentication failed. Exception: " + task.getException(),
                                            Toast.LENGTH_LONG).show();
                                }
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                else {
                                    startActivity(new Intent(Register.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
                break;
        }
    }

    /**
     *  Activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
