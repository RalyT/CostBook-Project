package finalproject.comp3617.com.costbook.UserLogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import finalproject.comp3617.com.costbook.R;

/**
 *  Reset Password Activity Page
 */
public class ResetPassword extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // UI Elements
        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Retrieves Firebase Auth instance
        auth = FirebaseAuth.getInstance();
    }

    /**
     *  Performs Password Reset or navigates to different Activities.
     * @param v Selected button
     */
    public void onClickNav(View v) {
        switch (v.getId()) {

            // Reset Password Button
            case R.id.btn_reset_password:

                String email = inputEmail.getText().toString().trim();

                // Check if the Email field is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered Email ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Email field is populated, perform password reset process
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPassword.this, "We have sent the Email instructions on how to reset your password!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ResetPassword.this, "ERROR: Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    });
                break;

            // Back button
            case R.id.btn_back:
                // Return to previous page
                finish();
                break;
        }
    }
}