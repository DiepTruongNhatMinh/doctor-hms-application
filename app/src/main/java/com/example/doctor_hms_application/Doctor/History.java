package com.example.doctor_hms_application.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class History extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth auth;

    Button btn_backHis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("CanceledAppointment");

        btn_backHis = findViewById(R.id.btn_backHis);
        btn_backHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> appointmentDetails = new ArrayList<>();

                // Loop through all children of CanceledAppointment node
                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                        // Get the appointment ID
                        String appointmentId = appointmentSnapshot.getKey();
                        String doctorEmail = appointmentSnapshot.child("doctorEmail").getValue(String.class);
                        if(doctorEmail != null && doctorEmail.equals(user.getEmail())){
                        // Get details for each appointment
                        String appointmentCancelDay = appointmentSnapshot.child("appointmentCancelDay").getValue(String.class);
                        String cancellationReason = appointmentSnapshot.child("cancellationReason").getValue(String.class);
                        String doctorName = appointmentSnapshot.child("doctorName").getValue(String.class);
                        String patientName = appointmentSnapshot.child("patientName").getValue(String.class);

                        // Construct the display text
                        String displayText =
                                "Cancel Day: " + appointmentCancelDay +
                                        "\nCancellation Reason: " + cancellationReason +
                                        "\nDoctor Name: " + doctorName +
                                        "\nPatient Name: " + patientName;

                        appointmentDetails.add(displayText);
                    }



                }

                updateListView(R.id.listView2, appointmentDetails);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        DatabaseReference appointmentsCompleteRef = FirebaseDatabase.getInstance().getReference("CompletedAppointment");

        appointmentsCompleteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> appointmentDetails = new ArrayList<>();

                // Loop through all children of CanceledAppointment node
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    // Get the appointment ID
                    String appointmentId = appointmentSnapshot.getKey();
                    String doctorEmail = appointmentSnapshot.child("doctorEmail").getValue(String.class);
                    if(doctorEmail != null && doctorEmail.equals(user.getEmail())){

                        // Get details for each appointment
                        String appointmentCompleted = appointmentSnapshot.child("appointmentTime").getValue(String.class);
                        String conclusion = appointmentSnapshot.child("conclusion").getValue(String.class);
                        String doctorName = appointmentSnapshot.child("dName").getValue(String.class);
                        String patientName = appointmentSnapshot.child("pName").getValue(String.class);
                        String patientEmail = appointmentSnapshot.child("patientEmail").getValue(String.class);


                        // Construct the display text
                        String displayText =
                                "Appointment Date: " + appointmentCompleted +
                                        "\nConclusion: " + conclusion +
                                        "\nDoctor Name: " + doctorName +
                                        "\nPatient Name: " + patientName +
                                        "\nPatient Email: " + patientEmail +
                                        "\nDoctor Email: " + patientName;

                        appointmentDetails.add(displayText);
                    }


                }

                updateListView(R.id.listView1, appointmentDetails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateListView(int listViewId, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        ListView listView = findViewById(listViewId);
        listView.setAdapter(adapter);
    }
}