package com.example.bookit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookit.model.Accommodation;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccommodationApprovalActivity extends AppCompatActivity {

    private AccommodationApi accommodationApi;
    private ListView accommodationListView;
    private Accommodation selectedAccommodation;
    private Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accommodation_approval);

        retrofit = new RetrofitService(getApplicationContext()).getRetrofit();
        accommodationApi = retrofit.create(AccommodationApi.class);

        selectedAccommodation = null;

        accommodationListView = findViewById(R.id.accommodationListView);

        fetchAccommodationsForApproval();

    }

    private void showApprovalDialog(Accommodation accommodation) {
        Log.d("Dialog", "Preparing to show dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.approve_dialog, null);
        builder.setView(view);

        TextView dialogTextViewMessage = view.findViewById(R.id.dialogTextViewMessage);
        Button dialogBtnApprove = view.findViewById(R.id.dialogBtnApprove);
        Button dialogBtnDeny = view.findViewById(R.id.dialogBtnDeny);

        AlertDialog dialog = builder.create();

        dialogBtnApprove.setOnClickListener(v -> {
            dialog.dismiss();
            approveAccommodation(accommodation.getId());
        });

        dialogBtnDeny.setOnClickListener(v -> {
            dialog.dismiss();
            denyAccommodation(accommodation.getId());
        });
        Log.d("Dialog", "Showing dialog");
        dialog.show();
    }

    private void fetchAccommodationsForApproval() {
        Call<List<Accommodation>> call = accommodationApi.getPendingAccommodations();
        call.enqueue(new Callback<List<Accommodation>>() {
            @Override
            public void onResponse(Call<List<Accommodation>> call, Response<List<Accommodation>> response) {
                if (response.isSuccessful()) {
                    List<Accommodation> accommodations = response.body();
                    displayAccommodations(accommodations);

                    accommodationListView.setOnItemClickListener((parent, view, position, id) -> {
                        Accommodation selectedAccommodation = accommodations.get(position);
                        showApprovalDialog(selectedAccommodation);
                    });
                } else {
                    Toast.makeText(AccommodationApprovalActivity.this, "Failed to fetch accommodations for approval", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Accommodation>> call, Throwable t) {
                Toast.makeText(AccommodationApprovalActivity.this, "Failed to fetch accommodations for approval", Toast.LENGTH_SHORT).show();
                if (t instanceof IOException) {
                    // Network error
                    Log.e("NetworkError", t.getMessage(), t);
                } else if (call.isCanceled()) {
                    // Request was cancelled
                    Log.e("RequestCancelled", "Request was cancelled");
                } else {
                    // Other error
                    Log.e("ErrorResponse", "Error response: " + t.getMessage());
                }
            }

        });
    }

    private void displayAccommodations(List<Accommodation> accommodations) {
        AccommodationApprovalAdapter adapter = new AccommodationApprovalAdapter(this, accommodations);
        accommodationListView.setAdapter(adapter);

        accommodationListView.setOnItemClickListener((parent, view, position, id) -> {
            this.selectedAccommodation = accommodations.get(position);
        });
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
