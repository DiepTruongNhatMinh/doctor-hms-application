package com.example.doctor_hms_application.AccountCreation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctor_hms_application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ResetPassword extends AppCompatActivity {

    Button bt_resetPassword;
    EditText ed_emailReset;
    TextView tv_loginBack;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String strEmail;

    DatabaseReference resetStatusRef; // Class-level variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        bt_resetPassword = findViewById(R.id.bt_resetPassword);
        tv_loginBack = findViewById(R.id.tv_resetPassword);
        ed_emailReset = findViewById(R.id.ed_emailReset);
        progressBar = findViewById(R.id.pb_resetPassword);

        mAuth = FirebaseAuth.getInstance();
        resetStatusRef = FirebaseDatabase.getInstance().getReference("resetStatus"); // Initialize resetStatusRef here

        bt_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = ed_emailReset.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmail)) {
                    resetPassword();
                } else {
                    ed_emailReset.setError("Email field can't be empty");
                }
            }
        });

        tv_loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent intent = new Intent(ResetPassword.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void resetPassword() {
        progressBar.setVisibility(View.VISIBLE);
        bt_resetPassword.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(strEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Save information in the Realtime Database
                        saveResetStatusToDatabase(strEmail);

                        Toast.makeText(ResetPassword.this, "Reset password link has been sent to your registered email", Toast.LENGTH_SHORT).show();

                        // Assuming uid is the UID of the current user
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();
                            // Log the UID and email for debugging
                            Log.d("ResetPassword", "UID: " + uid);
                            Log.d("ResetPassword", "Email: " + strEmail);
                            // Assuming uid is the UID of the current user
                            updatePasswordInDatabase(uid, "newPassword");
                        }

                        Intent intent = new Intent(ResetPassword.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResetPassword.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        bt_resetPassword.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void saveResetStatusToDatabase(String userEmail) {
        DatabaseReference resetStatusRef = FirebaseDatabase.getInstance().getReference("resetStatus");
        // Use child() to replace "." with "_"
        resetStatusRef.child(userEmail.replace(".", "_")).setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ResetPassword", "Reset status saved successfully");
                        } else {
                            Log.e("ResetPassword", "Error saving reset status: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void updatePasswordInDatabase(String uid, String newPassword) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.child("password").setValue(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ResetPassword", "Password updated successfully");
                        } else {
                            Log.e("ResetPassword", "Error updating password: " + task.getException().getMessage());
                        }
                    }
                });
    }


}