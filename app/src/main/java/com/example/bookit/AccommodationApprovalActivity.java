package com.example.bookit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookit.model.Accommodation;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccommodationApprovalActivity extends AppCompatActivity {

    private AccommodationApi accommodationApi;
    private ListView accommodationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accommodation_approval);

        Retrofit retrofit = new RetrofitService().getRetrofit();
        accommodationApi = retrofit.create(AccommodationApi.class);

        accommodationListView = findViewById(R.id.accommodationListView);

        fetchAccommodationsForApproval();
    }

    private void fetchAccommodationsForApproval() {
        Call<List<Accommodation>> call = accommodationApi.getPendingAccommodations();
        call.enqueue(new Callback<List<Accommodation>>() {
            @Override
            public void onResponse(Call<List<Accommodation>> call, Response<List<Accommodation>> response) {
                if (response.isSuccessful()) {
                    List<Accommodation> accommodations = response.body();
                    displayAccommodations(accommodations);
                } else {
                    Toast.makeText(AccommodationApprovalActivity.this, "Failed to fetch accommodations for approval", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Accommodation>> call, Throwable t) {
                Toast.makeText(AccommodationApprovalActivity.this, "Failed to fetch accommodations for approval", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAccommodations(List<Accommodation> accommodations) {
        ArrayAdapter<Accommodation> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, accommodations);
        accommodationListView.setAdapter(adapter);

        accommodationListView.setOnItemClickListener((parent, view, position, id) -> {
            Accommodation selectedAccommodation = accommodations.get(position);
            showApprovalDialog(selectedAccommodation);
        });
    }

    private void showApprovalDialog(Accommodation accommodation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.activity_accommodation_approval, null);
        builder.setView(view);

        TextView textViewMessage = view.findViewById(R.id.textViewMessage);
        Button btnApprove = view.findViewById(R.id.btnApprove);
        Button btnDeny = view.findViewById(R.id.btnDeny);


        AlertDialog dialog = builder.create();

        btnApprove.setOnClickListener(v -> {
            dialog.dismiss();
            approveAccommodation(accommodation.getId());
        });

        btnDeny.setOnClickListener(v -> {
            dialog.dismiss();
            denyAccommodation(accommodation.getId());
        });

        dialog.show();
    }


    private void approveAccommodation(int accommodationID) {
        Call<Void> call = accommodationApi.approveAccommodation(accommodationID);
        handleAccommodationResponse(call, "Accommodation approved successfully");
    }

    private void denyAccommodation(int accommodationID) {
        Call<Void> call = accommodationApi.denyAccommodation(accommodationID);
        handleAccommodationResponse(call, "Accommodation denied successfully");
    }

    private void handleAccommodationResponse(Call<Void> call, String successMessage) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AccommodationApprovalActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                    fetchAccommodationsForApproval();
                } else {
                    Toast.makeText(AccommodationApprovalActivity.this, "Failed to process accommodation", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AccommodationApprovalActivity.this, "Failed to process accommodation", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
