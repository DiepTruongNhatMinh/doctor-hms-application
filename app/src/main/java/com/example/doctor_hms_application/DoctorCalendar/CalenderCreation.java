package com.example.doctor_hms_application.DoctorCalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.doctor_hms_application.Home;
import com.example.doctor_hms_application.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalenderCreation extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private Button bt_logout; // Declare the Button

    // Create an ArrayList to store schedules
    private List<SchedulesDataClass> scheduleList = new ArrayList<>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_creation);
        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("schedules");

        CalendarView calendarView = findViewById(R.id.calendarView);
        // Change background color
        calendarView.setBackgroundColor(getResources().getColor(R.color.blue));

        // Change focused month day color
        int focusedMonthDateColor = getResources().getColor(R.color.grey);
        calendarView.setDateTextAppearance(R.style.CustomDateText);
        calendarView.setFocusedMonthDateColor(focusedMonthDateColor);

        // Change selected date color
        int selectedDateColor = getResources().getColor(R.color.colorPrimary);
        calendarView.setSelectedDateVerticalBar(selectedDateColor);
        TimePicker timePickerStart = findViewById(R.id.timePickerStart);
        TimePicker timePickerEnd = findViewById(R.id.timePickerEnd);
        Button saveButton = findViewById(R.id.saveButton);
        Button viewTimetableButton = findViewById(R.id.viewTimetableButton);

        bt_logout = findViewById(R.id.bt_logout);// Initialize the Button

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalenderCreation.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        // Set the time picker to 24-hour format
        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Handle date selection
            String selectedDate = formatDate(year, month, dayOfMonth);
            saveButton.setOnClickListener(v -> {
                // Handle time selection
                int startHour = timePickerStart.getHour();
                int startMinute = timePickerStart.getMinute();
                String selectedStartTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);

                int endHour = timePickerEnd.getHour();
                int endMinute = timePickerEnd.getMinute();
                String selectedEndTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);
                // Combine the start and end times as needed
                String selectedTimeRange = "From" + " " + selectedStartTime + " " +"To" + " " + selectedEndTime;

                // Store the schedule in Firebase and add to the array
                storeSchedule(selectedDate, selectedTimeRange, "", "");
            });
        });


        viewTimetableButton.setOnClickListener(v -> {
            // Open TimetableActivity to display all schedules
            Intent intent = new Intent(CalenderCreation.this, Schedules.class);

            // Pass user information to the display activity
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                intent.putExtra("email", userEmail);

                // Retrieve and pass the full name
                retrieveFullName(currentUser.getUid(), fullName -> {
                    intent.putExtra("fullName", fullName);

                    // Pass the schedule list to the display activity
                    intent.putParcelableArrayListExtra("scheduleList", new ArrayList<>(scheduleList));

                    startActivity(intent);
                });
            }
        });



        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalenderCreation.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String formatDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void storeSchedule(String selectedDate, String selectedTime, String cancellationReason, String ScheduleStatus) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser != null ? currentUser.getEmail() : "";
        String fullDateTime = selectedDate + " " + selectedTime;

        // Check if the schedule already exists based on fullDateTime
        boolean scheduleExists = false;
        int existingScheduleIndex = -1;

        for (int i = 0; i < scheduleList.size(); i++) {
            SchedulesDataClass existingSchedule = scheduleList.get(i);
            if (existingSchedule.getFullDateTime().equals(fullDateTime)) {
                scheduleExists = true;
                existingScheduleIndex = i;
                break;
            }
        }

        if (!scheduleExists) {
            // Schedule doesn't exist, proceed to add
            String key = databaseReference.push().getKey();


            SchedulesDataClass newSchedule = new SchedulesDataClass(userEmail, ScheduleStatus, fullDateTime, cancellationReason);
            newSchedule.setId(key); // Set the ID here

            scheduleList.add(newSchedule);

            DatabaseReference userScheduleRef = FirebaseDatabase.getInstance().getReference("user_schedules")
                    .child(currentUser.getUid());

            userScheduleRef.child(key).setValue(newSchedule)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Data saved successfully");
                        showToast("Schedule added");
                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "Error saving data: " + e.getMessage()));
        }
    }
    private void retrieveFullName(String userUid, FullNameCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userUid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    if (callback != null) {
                        callback.onFullNameRetrieved(fullName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    interface FullNameCallback {
        void onFullNameRetrieved(String fullName);
    }

}