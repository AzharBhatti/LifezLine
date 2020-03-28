package com.example.lifezline.utils;

import com.example.lifezline.model.LoginRequest;
import com.example.lifezline.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Services {

    @Headers("Content-Type: application/json")
    @POST("Customerlogin")
    public Call<LoginResponse<Object>> login(@Body LoginRequest body);
}
