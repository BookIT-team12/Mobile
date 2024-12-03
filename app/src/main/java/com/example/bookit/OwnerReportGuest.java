package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.model.User;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.UserApi;
import com.example.bookit.security.UserTokenService;
import com.example.bookit.utils.adapters.OwnerReportGuestAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerReportGuest extends AppCompatActivity {

    RecyclerView recyclerView;
    OwnerReportGuestAdapter adapter;
    UserApi userApi;
    List<User> reportableUsers;
    String loggedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_report_guest);
        reportableUsers = new ArrayList<>();
        UserTokenService userTokenService = new UserTokenService(getApplicationContext());
        try {
            loggedUser = userTokenService.getCurrentUser(AppPreferences.getToken(getApplicationContext()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        RetrofitService retrofitService = new RetrofitService(getApplicationContext());
        userApi = retrofitService.getRetrofit().create(UserApi.class);

        userApi.getReportableUsers(loggedUser).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    reportableUsers = response.body();
                    recyclerView = findViewById(R.id.recyclerView_activity_owner_report_guest);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapter = new OwnerReportGuestAdapter(reportableUsers, userApi);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println("Failed to fetch reportable users!!");
            }
        });


    }
}