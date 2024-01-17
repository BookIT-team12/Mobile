package com.example.bookit.retrofit.api;

import retrofit2.Call;

public interface ReservationApi {

    //TODO: IMPLEMENTIRAJ DATE METODE
    Call<Void> denyReservation(int reservationID);

    Call<Void> approveReservation(int reservationID);

    Call<Void> cancelReservation(Integer id);
}
