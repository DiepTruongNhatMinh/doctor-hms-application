package com.example.doctor_hms_application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.doctor_hms_application.AccountCreation.Login;
import com.example.doctor_hms_application.Doctor.History;
import com.example.doctor_hms_application.Doctor.ListOfPatient;
import com.example.doctor_hms_application.Doctor.Profile;
import com.example.doctor_hms_application.DoctorCalendar.CalenderCreation;
import com.example.doctor_hms_application.DoctorCalendar.Schedules;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    Button bt_logout;
    private DrawerLayout drawerLayout;
    private FirebaseAuth auth;
    ImageView imageViewProfile, imageViewSchedule, imageViewListOfPatient, imageViewHistory;
    TextView tv_profile, tv_schedule, tv_list_of_patient, tv_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("Home", "onCreate");

        auth = FirebaseAuth.getInstance();
        bt_logout = findViewById(R.id.bt_logout);

        drawerLayout = findViewById(R.id.drawerLayout);
        auth = FirebaseAuth.getInstance();

        imageViewProfile = findViewById(R.id.imageViewProfile);
        tv_profile = findViewById(R.id.tv_profile);

        imageViewSchedule = findViewById(R.id.imageViewSchedule);
        tv_schedule = findViewById(R.id.tv_schedule);

        imageViewListOfPatient = findViewById(R.id.imageViewListOfPatient);
        tv_list_of_patient = findViewById(R.id.textViewListOfPatient);

        imageViewHistory = findViewById(R.id.imageViewHistory);
        tv_history = findViewById(R.id.textViewHistory);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }

        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });
        imageViewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, CalenderCreation.class);
                startActivity(intent);
                finish();
            }
        });

        tv_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, CalenderCreation.class);
                startActivity(intent);
                finish();
            }
        });
        imageViewListOfPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, ListOfPatient.class);
                startActivity(intent);
                finish();
            }
        });

        tv_list_of_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, ListOfPatient.class);
                startActivity(intent);
                finish();
            }
        });

        imageViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, History.class);
                startActivity(intent);
                finish();
            }
        });

        tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, History.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
