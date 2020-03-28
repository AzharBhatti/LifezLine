package com.example.lifezline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class ServicesActivity extends AppCompatActivity {

    LinearLayout ambulance_service, contact_doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        ambulance_service = findViewById(R.id.ambulance_service_layout);
        contact_doctor = findViewById(R.id.contact_doctor_layout);

        ambulance_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ServicesActivity.this, AmbulanceActivity.class);
                Intent intent = new Intent(ServicesActivity.this, AmbulanceMapsActivity.class);

                startActivity(intent);
            }
        });

        contact_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, DoctorActivity.class);

                startActivity(intent);
            }
        });

    }
}
