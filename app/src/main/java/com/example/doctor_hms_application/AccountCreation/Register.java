package com.example.doctor_hms_application.AccountCreation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctor_hms_application.Doctor.DoctorClass;
import com.example.doctor_hms_application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Register extends AppCompatActivity {

    EditText ed_emailRes, ed_passwordRes, ed_fullname, ed_DoB, ed_mobile, ed_address, ed_workExperience, ed_price;
    String gender;
    ArrayList<Uri> images_cert;
    String[] base64Strings;
    TextView tv_loginRes;
    FirebaseAuth auth;
    FirebaseUser user;
    //update button
    Button btn_certification;
    FirebaseAuth mAuth;
    Button bt_resgister;
    ProgressBar progressBar;
    private static final String TAG = "Register";
    private boolean isNewData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ed_emailRes = findViewById(R.id.ed_emailRes);
        ed_passwordRes = findViewById(R.id.ed_passwordRes);
        bt_resgister = findViewById(R.id.bt_register);
        auth = FirebaseAuth.getInstance();
        //data mapping
        ed_address = findViewById(R.id.ed_address);
        ed_DoB = findViewById(R.id.ed_DoB);
        ed_fullname = findViewById(R.id.ed_fullName);
        ed_mobile = findViewById(R.id.ed_mobile);
        btn_certification = (findViewById(R.id.bt_certification));
        ed_workExperience = findViewById(R.id.ed_workExperience);
        ed_price = findViewById(R.id.ed_price);
        tv_loginRes = findViewById(R.id.tv_loginRes);
        tv_loginRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        // Tạo danh sách các chuyên gia
        String[] specialists = {"Heart Specialist", "Dietician", "Dermatologist", "Gynecologist", "Neurologist", "Proctologist", "Obstetrician", "Oncologist", "Physiotherapist"};
        // Tạo adapter cho spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialists);
        // Đặt adapter cho spinner
        Spinner spinner_specialist = findViewById(R.id.spinner_specialist);
        spinner_specialist.setAdapter(adapter);
        //xu ly cert
        btn_certification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thêm quyền
                if (ContextCompat.checkSelfPermission(Register.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }else {
                    // Khởi tạo mảng images với kích thước phù hợp
                    images_cert = new ArrayList<>(0);

                    // Tạo intent cho phép chọn nhiều ảnh
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);

                    // Khởi động hoạt động
                    startActivityForResult(intent, 1);
                }

            }
        });
        bt_resgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailRes = ed_emailRes.getText().toString();
                String passwordRes = ed_passwordRes.getText().toString();

                if (TextUtils.isEmpty(emailRes)) {
                    showToast("Please enter your email");
                    ed_emailRes.setError("Email is required");
                    ed_emailRes.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailRes).matches()) {
                    showToast("Please re-enter your email");
                    ed_emailRes.setError("Valid email is required");
                    ed_emailRes.requestFocus();
                } else if (TextUtils.isEmpty(passwordRes)) {
                    showToast("Please enter your password");
                    ed_passwordRes.setError("Password is required");
                    ed_passwordRes.requestFocus();
                } else if (passwordRes.length() < 6) {
                    showToast("Password should be at least 6 digits");
                    ed_passwordRes.setError("Password too weak");
                    ed_passwordRes.requestFocus();
                } else {
                    registerUser(emailRes, passwordRes);
                }
            }

            private void registerUser(String emailRes, String passwordRes) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(emailRes, passwordRes).addOnCompleteListener(Register.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    //fullname
                                    String fullname = ed_fullname.getText().toString();
                                    //address
                                    String address = ed_address.getText().toString();
                                    //DoB
                                    String DoB = ed_DoB.getText().toString();
                                    //mobile
                                    String mobile = ed_mobile.getText().toString();
                                    //specialist
                                    String specialist = spinner_specialist.getSelectedItem().toString();
                                    //Price
                                    String price = ed_price.getText().toString();
                                    //Experience
                                    String workExperience = ed_workExperience.getText().toString();
                                    //de xu ly gender
                                    RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);
                                    int selectedId = radioGroupGender.getCheckedRadioButtonId();
                                    if (selectedId != -1) {
                                        RadioButton selectedGender = findViewById(selectedId);
                                        gender = selectedGender.getText().toString();
                                    } else {
                                        // Xử lý trường hợp không có radio button nào được chọn
                                        // Ví dụ: hiển thị thông báo lỗi hoặc đặt giá trị mặc định cho biến gender
                                    }
                                    // Chuyển mảng base64Strings thành ArrayList
                                    ArrayList<String> certificatesList = new ArrayList<>(Arrays.asList(base64Strings));
                                    // Tạo bản ghi mới
                                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
                                    if (isNewData) {
                                        // Tạo bản ghi mới
                                        DoctorClass doctorProfile = new DoctorClass(certificatesList, address, DoB, mobile, specialist, gender, fullname, "pending",emailRes, passwordRes, "pending", price, workExperience);
                                        referenceProfile.child(firebaseUser.getUid()).setValue(doctorProfile);
                                        Toast.makeText(getApplicationContext(), "Your Profile Have Been Sent To The Administrator! Please Wait For The Approvement", Toast.LENGTH_LONG).show();
                                        isNewData = false;
                                    } else {
                                        // Cập nhật bản ghi hiện có
                                        DoctorClass doctorProfile = new DoctorClass(certificatesList, address, DoB, mobile, specialist, gender, fullname, "pending", emailRes, passwordRes, "pending", price, workExperience);
                                        referenceProfile.setValue(doctorProfile);
                                        Toast.makeText(getApplicationContext(), "Your Profile Have Been Updated", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }

            private void handleRegistrationFailure(Exception exception) {
                try {
                    throw exception;
                } catch (FirebaseAuthWeakPasswordException e) {
                    showToast("Your password is too weak");
                    ed_passwordRes.setError("Your password is too weak");
                    ed_passwordRes.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    showToast("Your email is invalid or already in use");
                    ed_passwordRes.setError("Your email is invalid or already in use");
                    ed_passwordRes.requestFocus();
                } catch (FirebaseAuthUserCollisionException e) {
                    showToast("User is already registered with this email");
                    ed_passwordRes.setError("User is already registered with this email");
                    ed_passwordRes.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    showToast(e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
            }

            private void showToast(String message) {
                Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
            }

            private void notifyAdminAboutRegistration(String uid) {
                // Implement your notification mechanism here
                // You can send an email, store requests in a separate database node, etc.
                // For simplicity, let's log a message
                Log.d(TAG, "New registration request: " + uid);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Nếu kết quả là thành công
        if (resultCode == RESULT_OK && data.getClipData() != null) {
            // Lấy danh sách ảnh đã chọn
            ClipData clipData = data.getClipData();

            if (clipData != null) {
                // Số lượng ảnh được chọn
                int count = clipData.getItemCount();

                // Duyệt qua các ảnh và lưu vào mảng images
                for (int i = 0; i < count; i++) {
                    images_cert.add(clipData.getItemAt(i).getUri());
                }
            } else {
                // Nếu người dùng chỉ chọn một tấm ảnh
                images_cert.add(data.getData());
            }
            // Mảng base64
            base64Strings = new String[images_cert.size()];
            for (int i = 0; i < images_cert.size(); i++) {
                // Lấy đối tượng Bitmap từ uri của hình ảnh
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(images_cert.get(i)));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                // Chuyển đối tượng Bitmap thành base64 string
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                // Lưu chuỗi base64 vào vị trí thứ i trong mảng base64Strings
                base64Strings[i] = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
        }
    }
}
