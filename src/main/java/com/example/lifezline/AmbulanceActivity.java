package com.example.lifezline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class AmbulanceActivity extends AppCompatActivity {

    LinearLayout driver_details_layout;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance);

        driver_details_layout = findViewById(R.id.driver_details_layout);
        progressDialog = new ProgressDialog(AmbulanceActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");

        Handler handler = new Handler();

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
                                        driver_details_layout.setVisibility(View.VISIBLE);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new AlertDialog.Builder(AmbulanceActivity.this)
                                                                .setTitle("Alert!")
                                                                .setMessage("Ambulance has been arrived at your location!")
                                                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        onBackPressed();
                                                                    }
                                                                })
                                                                .setCancelable(false).show();
                                                    }
                                                });
                                            }
                                        }, 3000);
                                    }
                                });
                            }
                        }, 3000);
                    }
                });
            }
        }, 3000);

    }
}
