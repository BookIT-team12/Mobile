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
import com.example.bookit.utils.adapters.OwnerReportReviewOwnerAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerReportReviewOwner extends AppCompatActivity {

    String loggedUser;
    ReviewApi reviewApi;
    OwnerReportReviewOwnerAdapter adapter;
    RecyclerView recyclerView;
    List<Review> allOwnerReviewsOnOwner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_report_review_owner);
        UserTokenService userTokenService = new UserTokenService(getApplicationContext());
        try {
            loggedUser = userTokenService.getCurrentUser(AppPreferences.getToken(getApplicationContext()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        allOwnerReviewsOnOwner = new ArrayList<>();
        RetrofitService retrofitService = new RetrofitService(getApplicationContext());
        reviewApi = retrofitService.getRetrofit().create(ReviewApi.class);
        reviewApi.getAllReviewsForOwner(loggedUser).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()){
                    allOwnerReviewsOnOwner = response.body();
                    recyclerView = findViewById(R.id.recyclerView_activity_owner_report_review_owner);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapter = new OwnerReportReviewOwnerAdapter(allOwnerReviewsOnOwner, retrofitService);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                System.out.println("LOSE JBG");
            }
        });

    }

}