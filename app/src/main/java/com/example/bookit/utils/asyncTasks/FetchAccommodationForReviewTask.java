package com.example.bookit.utils.asyncTasks;

import android.os.AsyncTask;
import android.telecom.Call;
import android.widget.TextView;

import com.example.bookit.model.Accommodation;
import com.example.bookit.model.ResponseAccommodationImages;
import com.example.bookit.retrofit.api.AccommodationApi;

import java.io.IOException;

import okhttp3.Response;

public class FetchAccommodationForReviewTask  extends AsyncTask<Void, Void, Accommodation> {
    private final AccommodationApi accommodationApi;
    private final int reviewingAccommodationId;
    private final TextView nameTF;
    private final TextView maxGuestsTF;
    private final TextView minGuestsTF;
    private final TextView ownerEmailTF;

    public FetchAccommodationForReviewTask(AccommodationApi accommodationApi, int reviewingAccommodationId,
                                    TextView nameTF, TextView maxGuestsTF, TextView minGuestsTF, TextView ownerEmailTF) {
        this.accommodationApi = accommodationApi;
        this.reviewingAccommodationId = reviewingAccommodationId;
        this.nameTF = nameTF;
        this.maxGuestsTF = maxGuestsTF;
        this.minGuestsTF = minGuestsTF;
        this.ownerEmailTF = ownerEmailTF;
    }

    @Override
    protected Accommodation doInBackground(Void... voids) {
        try {
            ResponseAccommodationImages response = accommodationApi.viewAccommodationDetails(reviewingAccommodationId).execute().body();
            assert response != null;
            return response.getFirst();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Accommodation result) {
        if (result != null) {
            // Update UI with the obtained details
            nameTF.setText(String.format("Name: %s", result.getName()));
            maxGuestsTF.setText(String.format("Max Guests: %d", result.getMaxGuests()));
            minGuestsTF.setText(String.format("Min Guests: %d", result.getMinGuests()));
            ownerEmailTF.setText(String.format("Owner email: %s", result.getOwnerEmail()));
        } else {
            System.out.println("Failed to fetch accommodation details.");
        }
    }
}