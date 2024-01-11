package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookit.model.Accommodation;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OwnerAccommodationUpdate extends AppCompatActivity {

    private AccommodationApi accommodationApi;
    private ListView accommodationListView;

    private String ownerEmail; //TODO: fetch ownerEmail from the user that is logged in!!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_accommodation_update);

        accommodationApi = new RetrofitService().getRetrofit().create(AccommodationApi.class);

        accommodationListView = findViewById(R.id.accommodationListView);

        fetchAccommodationsForUpdate();

        Button btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(v -> {
            int selectedPosition = accommodationListView.getCheckedItemPosition();
            if (selectedPosition != ListView.INVALID_POSITION) {
                Accommodation selectedAccommodation = (Accommodation) accommodationListView.getItemAtPosition(selectedPosition);
                updateAccommodation(selectedAccommodation.getId());
            } else {
                Toast.makeText(OwnerAccommodationUpdate.this, "Please select an accommodation to update", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAccommodationsForUpdate() {
        Call<List<Accommodation>> call = accommodationApi.getOwnerAccommodations(ownerEmail); // Replace ownerEmail with the actual owner's email
        call.enqueue(new Callback<List<Accommodation>>() {
            @Override
            public void onResponse(Call<List<Accommodation>> call, Response<List<Accommodation>> response) {
                if (response.isSuccessful()) {
                    List<Accommodation> accommodations = response.body();
                    displayAccommodationsForUpdate(accommodations);
                } else {
                    Toast.makeText(OwnerAccommodationUpdate.this, "Failed to fetch accommodations for update", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Accommodation>> call, Throwable t) {
                Toast.makeText(OwnerAccommodationUpdate.this, "Failed to fetch accommodations for update", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAccommodationsForUpdate(List<Accommodation> accommodations) {
        ArrayAdapter<Accommodation> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, accommodations);
        accommodationListView.setAdapter(adapter);
        accommodationListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    //TODO: CREATE THAT ACCOMMODATION UPDATE PAGE IS OPENED FOR THE CHOSEN ACCOMMODATION!
    private void updateAccommodation(int accommodationId) {
    }
}