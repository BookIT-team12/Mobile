package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.model.Review;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReviewApi;
import com.example.bookit.security.UserTokenService;
import com.example.bookit.utils.adapters.OwnerReportReviewAccommodationAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerReportReviewAccommodation extends AppCompatActivity {
    String loggedUser;
    ReviewApi reviewApi;
    OwnerReportReviewAccommodationAdapter adapter;
    RecyclerView recyclerView;
    List<Review> allReviewsAccommodationOnOwnerApartments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_report_review_accommodation);

        UserTokenService userTokenService = new UserTokenService(getApplicationContext());
        try {
            loggedUser = userTokenService.getCurrentUser(AppPreferences.getToken(getApplicationContext()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        allReviewsAccommodationOnOwnerApartments = new ArrayList<>();
        RetrofitService retrofitService = new RetrofitService(getApplicationContext());
        reviewApi = retrofitService.getRetrofit().create(ReviewApi.class);

        reviewApi.getAllReviewsOnOwnerAccommodations(loggedUser).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()){
                    allReviewsAccommodationOnOwnerApartments = response.body();
                    recyclerView = findViewById(R.id.recyclerView_activity_owner_report_review_accommodation);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapter = new OwnerReportReviewAccommodationAdapter(allReviewsAccommodationOnOwnerApartments, retrofitService);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                System.out.println("Error in fetching reviews on accommodations owned by owner");
            }
        });
    }
}