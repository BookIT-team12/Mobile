package com.example.bookit.retrofit.api;

import com.example.bookit.model.Reservation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReservationApi {
    @GET("/reservations/guest/{email}")
    Call<List<Reservation>> getGuestReservations(@Path("email") String email);

}
