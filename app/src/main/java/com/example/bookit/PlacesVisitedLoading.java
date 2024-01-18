package com.example.bookit;
import android.os.AsyncTask;
import android.util.Pair;

import com.example.bookit.model.Accommodation;
import com.example.bookit.model.Reservation;
import com.example.bookit.model.ResponseAccommodationImages;
import com.example.bookit.model.enums.ReservationStatus;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.ReservationApi;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import retrofit2.Response;

public class PlacesVisitedLoading extends AsyncTask<Void, Void, Pair<List<ResponseAccommodationImages>, Map<Integer, Boolean>>> {
    private final String email;
    private final MyCallback callback;
    ReservationApi reservationApi;
    AccommodationApi accommodationApi;
    Map<Integer, Boolean> containing;

    public PlacesVisitedLoading(String email, MyCallback callback, ReservationApi api, AccommodationApi accommodationApi) {
        this.email = email;
        this.callback = callback;
        this.reservationApi = api;
        this.accommodationApi = accommodationApi;
    }

    @Override
    protected Pair<List<ResponseAccommodationImages>, Map<Integer, Boolean>> doInBackground(Void... params) {
        List<ResponseAccommodationImages> retVal = new ArrayList<>();
        containing = new HashMap<>();    //list with pairs that will say: [accommodationID,  boolean if he can make review for accommodation]
        // Perform your network request here
        try {
            Response<List<Reservation>> response = reservationApi.getGuestReservations(email).execute();
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime today = currentDateTime.with(LocalTime.MIDNIGHT);
            LocalDateTime weekBefore = today.minusWeeks(1);
            if (response.isSuccessful()) {
                assert response.body() != null;
                for (Reservation res: response.body()){
                    if (res.getEndDate().isBefore(today) && res.getStatus().equals(ReservationStatus.APPROVED)) {   //gotova je rezervacija odradjena je
                        boolean contains = this.containing.containsKey(res.getAccommodationId());
                        if (contains && Boolean.FALSE.equals(this.containing.get(res.getAccommodationId()))){
                            Boolean canAccommodationBeReviewed = res.getEndDate().isAfter(weekBefore);
                            this.containing.put(res.getAccommodationId(), canAccommodationBeReviewed);
                            continue;
                        }
                        if (!contains){
                            Response<ResponseAccommodationImages> toAdd = accommodationApi.viewAccommodationDetails(res.getAccommodationId()).execute();
                            retVal.add(toAdd.body());
                            Boolean canAccommodationBeReviewed = res.getEndDate().isAfter(weekBefore);
                            this.containing.put(res.getAccommodationId(), canAccommodationBeReviewed);
                        }
                    }
                }
                return new Pair<>(retVal, containing);
            } else {
                // Handle unsuccessful response
                // You can check the status code and error body
                int statusCode = response.code();
//                String errorBody = response.errorBody().string();
                // Handle the error as needed
                System.out.println(statusCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Pair<List<ResponseAccommodationImages>, Map<Integer, Boolean>> result) {
        super.onPostExecute(result);
        // Update UI with the result
        if (callback != null) {
            callback.onResponseReceived(result.first, result.second);
        }
    }

    // Define a callback interface
    public interface MyCallback {
        void onResponseReceived(List<ResponseAccommodationImages> result, Map<Integer, Boolean> containingMap);
    }

}
