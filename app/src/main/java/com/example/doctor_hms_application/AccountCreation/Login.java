package com.example.doctor_hms_application.AccountCreation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctor_hms_application.Home;
import com.example.doctor_hms_application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText ed_email, ed_password;

    FirebaseAuth Authentication;

    ProgressBar progressBar;

    Button bt_login;

    /*@Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = Authentication.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        ed_email = findViewById(R.id.tv_email);
        ed_password = findViewById(R.id.tv_password);
        progressBar = findViewById(R.id.progressBar);
        Authentication = FirebaseAuth.getInstance();

        TextView tv_register =(TextView) findViewById(R.id.tv_register);
        TextView tv_resetPassword = (TextView) findViewById(R.id.tv_resetPassword);
        bt_login = findViewById(R.id.bt_login);


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String email = ed_email.getText().toString();
                final String password = ed_password.getText().toString();


                if(TextUtils.isEmpty(email)){
                    ed_email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    ed_password.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    ed_password.setError("Password must be >= 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Lấy đối tượng FirebaseAuth
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

// Xác thực người dùng bằng tài khoản và mật khẩu
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // ... (lấy FirebaseUser)
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String accountId = user.getUid();

                                        // Thay đổi thành cách lấy dữ liệu từ Realtime Database
                                        DatabaseReference userReference = db.child("users").child(accountId);
                                        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String registrationStatus = snapshot.child("registrationStatus").getValue(String.class);
                                                if (isRegistrationApproved(registrationStatus)) {
                                                    // ... (đăng nhập thành công)
                                                    Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                    finish();
                                                } else {
                                                    // ... (người dùng chưa được phê duyệt)

                                                    Toast.makeText(getApplicationContext(), "Registration not yet approved.", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(getApplicationContext(), "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        // ... (xử lý trường hợp user null)
                                        Toast.makeText(getApplicationContext(), "Error retrieving user information.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // ... (xử lý lỗi xác thực)
                                    Toast.makeText(getApplicationContext(), "tui chua co duyet",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        tv_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ResetPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public static boolean isRegistrationApproved(String registrationStatus) {
        return registrationStatus.equals("approved");
    }}