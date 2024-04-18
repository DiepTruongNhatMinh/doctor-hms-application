package com.example.doctor_hms_application.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.doctor_hms_application.AccountCreation.Login;
import com.example.doctor_hms_application.Home;
import com.example.doctor_hms_application.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    TextView tv_fullname, tv_DoB, tv_mobile, tv_address, tv_specialist, tv_userEmail, tv_gender;
    ArrayList imageBase64List;
    FirebaseAuth auth;
    FirebaseUser user;
    //update button
    Button btn_backHome;
    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        auth = FirebaseAuth.getInstance();
        tv_userEmail = findViewById(R.id.tv_userEmail);
        user = auth.getCurrentUser();

        //data mapping
        tv_address = findViewById(R.id.tv_address);
        tv_DoB = findViewById(R.id.tv_DoB);
        tv_fullname = findViewById(R.id.tv_fullName);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_specialist = findViewById(R.id.tv_specialist);
        tv_gender = findViewById(R.id.tv_gender);

        btn_backHome = findViewById(R.id.btn_backHome);
        btn_backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        //checking user
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else {
            tv_userEmail.setText(user.getEmail());
        }
        // Retrieve doctorId from user's data
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        // Add ValueEventListener to update TextViews
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called when data is changed
                // dataSnapshot contains the data from the database

                // Check if the user's UID exists in the database
                if (dataSnapshot.child(user.getUid()).exists()) {
                    // Get the specific user's data
                    DataSnapshot userData = dataSnapshot.child(user.getUid());

                    // Example: Update userEmail TextView
                    String userEmail = userData.child("email").getValue(String.class);
                    tv_userEmail.setText(userEmail);

                    // Example: Update fullName TextView
                    String fullName = userData.child("fullName").getValue(String.class);
                    tv_fullname.setText(fullName);

                    // Update other TextViews as needed
                    String dateOfBirth = userData.child("date_of_birth").getValue(String.class);
                    tv_DoB.setText(dateOfBirth);

                    String mobile = userData.child("moblie_phone").getValue(String.class);
                    tv_mobile.setText(mobile);

                    String address = userData.child("address").getValue(String.class);
                    tv_address.setText(address);

                    String specialist = userData.child("specialist").getValue(String.class);
                    tv_specialist.setText(specialist);

                    String gender = userData.child("gender").getValue(String.class);
                    tv_gender.setText(gender);

                    //certification
                    // Retrieve your image data from the Realtime Database
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    imageBase64List = userData.child("cert").getValue(t);
                    // Convert base64 strings to Bitmaps
                    ArrayList<Bitmap> imageList = convertBase64ToBitmap(imageBase64List);
                    GridView view = (findViewById(R.id.gridview));
                    view.setAdapter(new GridViewAdapter(Profile.this, imageList));
                    view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            // Lấy Bitmap từ mảng và giảm kích thước
                            Bitmap bitmap = imageList.get(i);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);

                            // Gửi Bitmap đã giảm kích thước qua Intent
                            Intent intent = new Intent(Profile.this, DisplayCertification.class);
                            intent.putExtra("value", scaledBitmap);
                            startActivity(intent);
                        }
                    });

                } else {
                    // Handle the case where the user's data doesn't exist
                    Log.d("ProfileActivity", "User data not found");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // This method is called in case of an error
                // Handle error
            }
        });
    }
    private ArrayList<Bitmap> convertBase64ToBitmap(ArrayList<String> imageBase64List) {
        ArrayList<Bitmap> bitmapList = new ArrayList<>();
        for (String base64String : imageBase64List) {
            if (base64String != null && !base64String.isEmpty()) {
                try {
                    byte[] decodedBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    if (bitmap != null) {
                        bitmapList.add(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmapList;
    }
}
