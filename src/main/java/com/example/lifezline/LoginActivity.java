package com.example.lifezline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lifezline.model.LoginRequest;
import com.example.lifezline.model.LoginResponse;
import com.example.lifezline.utils.Constants;
import com.example.lifezline.utils.CoreService;
import com.example.lifezline.utils.Services;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button login_button;
    ProgressDialog progressDialog;
    EditText mobileNumber_et, password_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_button = findViewById(R.id.login_button);
        mobileNumber_et = findViewById(R.id.number_et);
        password_et = findViewById(R.id.password_et);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, ServicesActivity.class);

                Intent intent = new Intent(LoginActivity.this, AmbulanceMapsActivity.class);

                startActivity(intent);

//                if(mobileNumber_et.getText().toString().isEmpty() || mobileNumber_et.getText().toString().length() != 11)
//                {
//                    mobileNumber_et.setError("Please enter valid mobile number!");
//                }
//                else if(password_et.getText().toString().isEmpty())
//                {
//                    password_et.setError("Please enter a password");
//                }
//                else {
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            loginTask();
//                        }
//                    });
//
//                }
            }
        });
    }

    private void loginTask()
    {
        if(!LoginActivity.this.isFinishing())
        {
            progressDialog.show();
        }

        Services coreApi = CoreService.getInstance(Constants.URL);

        Call<LoginResponse<Object>> callback = coreApi.login(new LoginRequest(mobileNumber_et.getText().toString(), password_et.getText().toString()));
        callback.enqueue(new Callback<LoginResponse<Object>>() {
            @Override
            public void onResponse(Call<LoginResponse<Object>> call, Response<LoginResponse<Object>> response) {


                LoginResponse<Object> body = response.body();
//                LoginResponse<Object> body = response.body();
                if(body != null && body.getCode() != null) {
                    if (body.getCode().equalsIgnoreCase("00")) {

                        progressDialog.dismiss();

                        Intent intent = new Intent(LoginActivity.this, AmbulanceMapsActivity.class);

                        startActivity(intent);

                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle("Alert!");
                        alertDialog.setMessage(body.getDescription());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();

                        progressDialog.dismiss();
                    }
                }
                else {
                    progressDialog.dismiss();
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Alert!");
                    alertDialog.setMessage("Something went wrong, Please try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }

//                AccountsCustomListAdapter customAdapter = new AccountsCustomListAdapter(CnicValidate.this, R.layout.account_listview_items, accountList);
//
//                accountListView.setAdapter(customAdapter);



            }

            @Override
            public void onFailure(Call<LoginResponse<Object>> call, Throwable t) {

                progressDialog.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Alert!");
                alertDialog.setMessage("Something went wrong, Please try again!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });



    }
}
