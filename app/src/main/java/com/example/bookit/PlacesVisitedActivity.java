package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Pair;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.model.Accommodation;
import com.example.bookit.model.ResponseAccommodationImages;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.ReservationApi;
import com.example.bookit.security.UserTokenService;
import com.example.bookit.utils.AccommodationRecycleViewAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlacesVisitedActivity extends AppCompatActivity implements PlacesVisitedLoading.MyCallback {

    List<Accommodation> allPlacesVisited;
    UserTokenService userTokenService;
    List<ResponseAccommodationImages> placesVisited;
    Map<Integer, Boolean> containingMap;
    RecyclerView recycleViewAdapter;
    AccommodationRecycleViewAdapter accommodationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_visited);
        recycleViewAdapter = findViewById(R.id.recyclerViewAccommodations);
        RetrofitService retrofitService = new RetrofitService(getApplicationContext());
        ReservationApi api = retrofitService.getRetrofit().create(ReservationApi.class);
        AccommodationApi accommodationApi = retrofitService.getRetrofit().create(AccommodationApi.class);
        userTokenService = new UserTokenService(getApplicationContext());
        try {
            String loggedGuest = userTokenService.getCurrentUser(AppPreferences.getToken(getApplicationContext()));
            PlacesVisitedLoading asyncTask = new PlacesVisitedLoading(loggedGuest, this, api, accommodationApi);
            asyncTask.execute();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onResponseReceived(List<ResponseAccommodationImages> response, Map<Integer, Boolean> containingMap) {
        this.placesVisited = response;
        this.containingMap = containingMap;
        accommodationAdapter = new AccommodationRecycleViewAdapter(this.placesVisited, containingMap);
        recycleViewAdapter.setLayoutManager(new LinearLayoutManager(this));
        recycleViewAdapter.setAdapter(accommodationAdapter);
    }
}