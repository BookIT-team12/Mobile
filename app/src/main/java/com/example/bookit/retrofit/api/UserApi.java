package com.example.bookit.retrofit.api;

import com.example.bookit.model.User;
import com.example.bookit.model.UserCredentials;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserApi {
    @POST("/register")
    Call<UserCredentials> register(@Body User toRegister);

    @GET
    Call<List<User>> getAllUsers();
}
