package com.example.doctor_hms_application.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctor_hms_application.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AppointmentResult extends AppCompatActivity {
    TextView tv_patient_name, tv_patient_dob, tv_patient_gender, tv_patient_mobilephone, tv_appointment_time, tv_doctor_name, tv_Reason, tv_doctor_address;
    EditText ed_patient_insurance, ed_clinical_result, ed_diagnosis, ed_conclusion, ed_recommendation, ed_notes;
    Button btn_cancel, btn_confirm;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_result);
        //Data mapping
        tv_appointment_time = findViewById(R.id.tv_appointment_time);
        tv_doctor_name = findViewById(R.id.tv_doctor_name);
        tv_Reason = findViewById(R.id.tv_Reason);
        tv_patient_name = findViewById(R.id.tv_patient_name);
        tv_patient_mobilephone = findViewById(R.id.tv_patient_mobilephone);
        tv_doctor_address = findViewById(R.id.tv_doctor_address);

        ed_clinical_result = findViewById(R.id.ed_clinical_result);
        ed_patient_insurance = findViewById(R.id.ed_patient_insurance);
        ed_diagnosis = findViewById(R.id.ed_diagnosis);
        ed_conclusion = findViewById(R.id.ed_conclusion);
        ed_recommendation = findViewById(R.id.ed_recommendation);
        ed_notes = findViewById(R.id.ed_notes);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_confirm = findViewById(R.id.btn_confirm);


        // Get a reference to the "Appointment" node in Firebase database
        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("Appointments");
        // Retrieve appointment details from the database
        appointmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()){
                    String appointmentId = appointmentSnapshot.child("appointmentId").getValue(String.class);
                    String appointmentId_intent = getIntent().getStringExtra("appointmentId");
                    //Toast.makeText(getApplicationContext(), appointmentId + "    " + appointmentId_intent, Toast.LENGTH_LONG).show();

                    if (appointmentId.equals(appointmentId_intent)) {
                        // Retrieve appointment data
                        String patientName = appointmentSnapshot.child("patientName").getValue(String.class);
                        String doctorAddress = appointmentSnapshot.child("address").getValue(String.class);
                        String appointmentTime = appointmentSnapshot.child("appointmentTime").getValue(String.class);
                        String doctorName = appointmentSnapshot.child("doctorName").getValue(String.class);
                        String reason = appointmentSnapshot.child("reason").getValue(String.class);
                        String mobilephone = appointmentSnapshot.child("patientPhoneNumber").getValue(String.class);
                        // Set text in TextViews
                        tv_patient_name.setText(patientName);
                        tv_doctor_address.setText(doctorAddress);
                        tv_appointment_time.setText(appointmentTime);
                        tv_patient_mobilephone.setText(mobilephone);
                        tv_doctor_name.setText(doctorName);
                        tv_Reason.setText(reason);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //btn cancel
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(AppointmentResult.this, ListOfPatient.class);
                startActivity(home);
            }
        });

        //btn confirm
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create new table of completed appoitment
                DatabaseReference databaseResultReference = FirebaseDatabase.getInstance().getReference("CompletedAppointment");
                DatabaseReference reference = databaseResultReference.push();
                String appointmentId_intent = getIntent().getStringExtra("appointmentId");
                String doctorEmail = getIntent().getStringExtra("doctorEmail");
                String patientEmail = getIntent().getStringExtra("patientEmail");

                Map<String, String> data = new HashMap<>();
                String pName = tv_patient_name.getText().toString();
                String pPhoneNo = tv_patient_mobilephone.getText().toString();
                String apmTime = tv_appointment_time.getText().toString();
                String dName = tv_doctor_name.getText().toString();
                String dAddress = tv_doctor_address.getText().toString();
                String reason = tv_Reason.getText().toString();
                String insuranceType = ed_patient_insurance.getText().toString();
                String clinicalER = ed_clinical_result.getText().toString();
                String diagnosis = ed_diagnosis.getText().toString();
                String conclution = ed_conclusion.getText().toString();
                String recommendation = ed_recommendation.getText().toString();
                String notes = ed_notes.getText().toString();
                data.put("pName", pName);
                data.put("pPhoneNo", pPhoneNo);
                data.put("appointmentTime", apmTime);
                data.put("insuranceType", insuranceType);
                data.put("dName", dName);
                data.put("dAddress", dAddress);
                data.put("reason", reason);
                data.put("clinicalER", clinicalER);
                data.put("diagnosis", diagnosis);
                data.put("conclusion", conclution);
                data.put("recommendation", recommendation);
                data.put("notes", notes);
                data.put("appointmentId", appointmentId_intent);
                data.put("doctorEmail", doctorEmail);
                data.put("patientEmail", patientEmail);
                reference.setValue(data);

                //Change Appointment Status from "Has not been completed" to "Completed"
                DatabaseReference appointmentReference = FirebaseDatabase.getInstance().getReference("Appointments");
                appointmentReference.child(appointmentId_intent).child("appointmentStatus").setValue("Completed");
                Toast.makeText(getApplicationContext(), "Congratulations!! You have just completed a medical examination form", Toast.LENGTH_LONG).show();
                //AfterComplete
                Intent history_intent = new Intent(AppointmentResult.this, History.class);
                startActivity(history_intent);
            }
        });
    }
}