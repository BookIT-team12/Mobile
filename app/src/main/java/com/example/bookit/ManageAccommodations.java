package com.example.bookit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookit.model.Accommodation;
import com.example.bookit.model.User;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageAccommodations extends AppCompatActivity {

    private AccommodationApi accommodationApi;
    private ListView accommodationListView;

    private User currentUser;

    private String ownerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_accommodation_update);

        accommodationApi = new RetrofitService(getApplicationContext()).getRetrofit().create(AccommodationApi.class);

        accommodationListView = findViewById(R.id.accommodationListView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_VALUE")) {
            currentUser = (User) intent.getSerializableExtra("USER_VALUE");
            ownerEmail=currentUser.getEmail();
        }


        fetchAccommodationsForUpdate();

//        Button btnUpdate = findViewById(R.id.btnUpdate);
//        btnUpdate.setOnClickListener(v -> {
//            int selectedPosition = accommodationListView.getCheckedItemPosition();
//            if (selectedPosition != ListView.INVALID_POSITION) {
//                Accommodation selectedAccommodation = (Accommodation) accommodationListView.getItemAtPosition(selectedPosition);
//                updateAccommodation(selectedAccommodation.getId());
//            } else {
//                Toast.makeText(ManageAccommodations.this, "Please select an accommodation to update", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void displayAccommodationsForUpdate(List<Accommodation> accommodations) {
        AccommodationApprovalAdapter adapter = new AccommodationApprovalAdapter(this, accommodations);
        accommodationListView.setAdapter(adapter);

        accommodationListView.setOnItemClickListener((parent, view, position, id) -> {
            Accommodation selectedAccommodation = (Accommodation) parent.getItemAtPosition(position);
            showUpdatePrompt(selectedAccommodation);
        });
    }

    private void showUpdatePrompt(Accommodation accommodation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.update_accommodation_prompt, null);
        builder.setView(view);

        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnUpdate.setOnClickListener(v -> {
            dialog.dismiss();
            startUpdateActivity(accommodation);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void startUpdateActivity(Accommodation accommodation) {
        Intent intent = new Intent(ManageAccommodations.this, UpdateAccommodation.class);
        intent.putExtra("SELECTED_ACCOMMODATION", accommodation.getId());
        startActivity(intent);
    }


    private void fetchAccommodationsForUpdate() {
        Call<List<Accommodation>> call = accommodationApi.getOwnerAccommodations(ownerEmail);
        call.enqueue(new Callback<List<Accommodation>>() {
            @Override
            public void onResponse(Call<List<Accommodation>> call, Response<List<Accommodation>> response) {
                if (response.isSuccessful()) {
                    List<Accommodation> accommodations = response.body();
                    displayAccommodationsForUpdate(accommodations);
                } else {
                    Toast.makeText(ManageAccommodations.this, "Failed to fetch accommodations for update", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Accommodation>> call, Throwable t) {
                Toast.makeText(ManageAccommodations.this, "Failed to fetch accommodations for update", Toast.LENGTH_SHORT).show();
            }
        });
    }

}