package com.example.doctor_hms_application.Doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.doctor_hms_application.R;

public class DisplayCertification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_certification);
        // Get Bitmap from intent
        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("value");
        ImageView imageView = findViewById(R.id.imageView);
        // Set Bitmap to ImageView
        imageView.setImageBitmap(bitmap);
    }
}