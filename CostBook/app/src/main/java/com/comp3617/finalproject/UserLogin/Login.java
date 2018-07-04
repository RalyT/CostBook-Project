package com.comp3617.finalproject.UserLogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class Login extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieves Firebase Auth instance
        auth = FirebaseAuth.getInstance();

//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(Login.this, MainActivity.class));
//            finish();
//        }

        // Set the view
        setContentView(R.layout.activity_login);

        // UI Elements
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_register);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
    }

    /**
     * Performs User Authentication or navigates to different Activities.
     * @param v Selected Button
     */
    public void onClickNav(View v) {
        switch (v.getId()) {

            // Login Button
            case R.id.btn_login:
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                // Checks if Email field is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Checks if Password field is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter your password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // If all fields are populated, perform user authentication
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // Error occurred
                                    // Check if the password length is too short
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                } else {
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
                break;

            // Forgot/Reset Password Button
            case R.id.btn_reset_password:
                startActivity(new Intent(Login.this, ResetPassword.class));
                break;

            // Register Button
            case R.id.btn_register:
                startActivity(new Intent(Login.this, Register.class));
                break;
        }
    }
}
