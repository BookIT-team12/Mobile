package com.example.bookit.retrofit.api;

import com.example.bookit.model.User;
import com.example.bookit.model.UserCredentials;
import com.example.bookit.security.UserTokenState;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {
    @POST("/users/register")
    Call<UserCredentials> register(@Body User toRegister);

    @POST("/users/login")
    Call<UserTokenState> login(@Body UserCredentials credentials);

    @DELETE("/users/{email}")
    Call<Map<String, String>> deleteUser(@Path("email") String email);

    @GET("/users/{email}")
    Call<User> getUser(@Path("email") String email);

    @POST("/users/{email}")
    Call<String> updateUser(@Path("email") String email, @Body User user);

    @GET("/users")
    Call<List<User>> getAllUsers();

    @GET("/users/reported")
    Call<List<User>> getReportedUsers();

    @GET("/users/reportable")
    Call<List<User>> getReportableUsers(@Query("userID") String userID);

    @POST("/users/blocked")
    Call<Void> blockUser(@Query("userID") String userID);

    @GET("/users/block")
    Call<List<User>> getUsersForBlocking();

    @POST("/users/report")
    Call<Void> reportUser(@Query("reportedID") String reportedID);
}
