package com.example.bookit.retrofit.api;

import com.example.bookit.model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationApi {

    @GET("/notifications")
    Call<List<Notification>> getAllNotifications();

    @GET("/notifications/{guestEmail}")
    Call<List<Notification>> getAllNotificationsUser(@Path("guestEmail") String guestEmail);

    @POST("/notifications")
    Call<Notification>createNotification(@Body Notification dto);


}
