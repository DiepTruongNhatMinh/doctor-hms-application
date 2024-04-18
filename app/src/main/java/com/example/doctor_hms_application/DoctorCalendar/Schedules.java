package com.example.doctor_hms_application.DoctorCalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

public class Schedules extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private List<SchedulesDataClass> scheduleList;
    private ArrayAdapter<SchedulesDataClass> adapter;

    private TextView userEmailTextView;
    private TextView userFullNameTextView;
    private ListView timetableListView;
    private Button logoutButton, backButton;
    private Button cancelScheduleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("user_schedules");

        userEmailTextView = findViewById(R.id.userEmailTextView);
        userFullNameTextView = findViewById(R.id.userFullNameTextView);
        logoutButton = findViewById(R.id.logoutButton);
        timetableListView = findViewById(R.id.timetableListView);
        cancelScheduleButton = findViewById(R.id.cancelScheduleButton);

        backButton = findViewById(R.id.backButton);
        scheduleList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, scheduleList);
        timetableListView.setAdapter(adapter);
        timetableListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Retrieve and display user information
        displayUserInfo();

        // Retrieve schedules from Firebase and update the list
        retrieveSchedules();

        // Logout button click listener
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // Redirect to the login screen
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Schedules.this, CalenderCreation.class);
                startActivity(intent);
                finish();
            }
        });

        // Cancel Schedule button click listener
        cancelScheduleButton.setOnClickListener(v -> {
            // Handle the cancellation logic here, you can use the selected schedules from the list
            ArrayList<SchedulesDataClass> selectedSchedules = getSelectedSchedules();

            if (!selectedSchedules.isEmpty()) {
                showCancelDialog(selectedSchedules);
            } else {
                // No schedules selected, display a message or handle accordingly
                showToast("Please select at least one date to cancel.");
            }
        });

    }

    // Helper method to get the selected schedules from the list
    private ArrayList<SchedulesDataClass> getSelectedSchedules() {
        ArrayList<SchedulesDataClass> selectedSchedules = new ArrayList<>();
        for (int i = 0; i < timetableListView.getCount(); i++) {
            if (timetableListView.isItemChecked(i)) {
                selectedSchedules.add(scheduleList.get(i));
            }
        }
        return selectedSchedules;
    }

    private void retrieveSchedules() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();

            DatabaseReference userScheduleRef = FirebaseDatabase.getInstance().getReference("user_schedules")
                    .child(userUid);

            userScheduleRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    scheduleList.clear();  // Clear the list before adding new schedules
                    for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                        SchedulesDataClass schedule = scheduleSnapshot.getValue(SchedulesDataClass.class);
                        if (schedule != null) {
                            // Set cancellationReason from the snapshot data
                            if (scheduleSnapshot.child("cancellationReason").exists()) {
                                schedule.setCancellationReason(scheduleSnapshot.child("cancellationReason").getValue(String.class));
                            }

                            // Check if the schedule has a cancellation reason
                            if (schedule.getCancellationReason() == null || schedule.getCancellationReason().isEmpty()) {
                                scheduleList.add(schedule);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }


    private void displayUserInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            String userEmail = intent.getStringExtra("email");
            String userFullName = intent.getStringExtra("fullName");

            // Update TextViews with user information
            userEmailTextView.setText("Email: " + userEmail);
            userFullNameTextView.setText("Full Name: " + userFullName);
        }
    }

    private void showCancelDialog(ArrayList<SchedulesDataClass> selectedSchedules) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Schedule");
        builder.setMessage("Enter reason for cancellation:");

        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String cancellationReason = input.getText().toString().trim();
            if (!cancellationReason.isEmpty()) {
                // Handle the cancellation logic with the reason
                handleCancellation(selectedSchedules, cancellationReason);
            } else {
                showToast("Please enter a reason for cancellation.");
            }
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void handleCancellation(ArrayList<SchedulesDataClass> selectedSchedules, String cancellationReason) {
        Log.d("HandleCancellation", "Received Cancellation Reason: " + cancellationReason);

        // Update the schedules with the cancellation reason and set scheduleStatus to "cancelled"
        for (SchedulesDataClass schedule : selectedSchedules) {
            // Make sure cancellationReason is not empty before updating
            if (!cancellationReason.isEmpty()) {
                schedule.setCancellationReason(cancellationReason);
                schedule.setScheduleStatus("cancelled");

                // Check if the schedule ID is not null before updating
                if (schedule.getId() != null) {
                    // Save the updated schedule to Firebase
                    saveUpdatedSchedule(schedule);
                } else {
                    showToast("Schedule ID is null. Cannot update.");
                }
            } else {
                showToast("Please enter a reason for cancellation.");
                return; // Don't proceed if cancellationReason is empty
            }
        }
    }

    private void saveUpdatedSchedule(SchedulesDataClass updatedSchedule) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference userScheduleRef = FirebaseDatabase.getInstance().getReference("user_schedules")
                    .child(userUid);

            // Check if the schedule ID is not null before updating
            if (updatedSchedule.getId() != null) {
                // Update the schedule in the database
                userScheduleRef.child(updatedSchedule.getId()).setValue(updatedSchedule)
                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Schedule updated successfully"))
                        .addOnFailureListener(e -> Log.e("Firebase", "Error updating schedule: " + e.getMessage()));
            } else {
                showToast("Schedule ID is null. Cannot update.");
            }
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveUpdatedSchedules(ArrayList<SchedulesDataClass> updatedSchedules) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference userScheduleRef = FirebaseDatabase.getInstance().getReference("user_schedules")
                    .child(userUid);

            for (SchedulesDataClass updatedSchedule : updatedSchedules) {
                // Check if the schedule ID is not null before updating
                if (updatedSchedule.getId() != null) {
                    // Update the schedule in the database
                    userScheduleRef.child(updatedSchedule.getId()).setValue(updatedSchedule);
                }
            }
        }
    }
}
