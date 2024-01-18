package com.example.bookit.retrofit.api;

import com.example.bookit.model.Reservation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ReservationApi {

    //TODO: IMPLEMENTIRAJ DATE METODE
    @PUT("/reservations/{status}")
    Call<Void> changeReservationStatus(@Path("status") int status, @Body Reservation dto);

    @GET("/reservations/guest/{email}")
    Call<List<Reservation>> getGuestReservations(@Path("email") String email);

    @GET("/reservations/owner/{email}")
    Call<List<Reservation>> getOwnerReservations(@Path("email") String email);

}
