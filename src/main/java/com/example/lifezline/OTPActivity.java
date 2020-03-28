package com.example.lifezline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OTPActivity extends AppCompatActivity {

    Button submit_button;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        progressDialog = new ProgressDialog(OTPActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");
        submit_button = findViewById(R.id.submit_button);

        final Handler handler = new Handler();

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
//                                                Intent intent = new Intent(OTPActivity.this, ServicesActivity.class);
                                                Intent intent = new Intent(OTPActivity.this, AmbulanceMapsActivity.class);

                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }, 3000);
                            }
                        });
                    }
                }, 10);

            }
        });
    }
}
