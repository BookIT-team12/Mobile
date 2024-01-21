package com.example.bookit.utils.asyncTasks;

import android.os.AsyncTask;

import com.example.bookit.retrofit.api.AccommodationApi;

import java.io.IOException;
import java.util.Map;

import retrofit2.Response;

public class FetchOwnerEmailTask extends AsyncTask<Void, Void, String> {
    private final AccommodationApi api;
    private final int accommodationId;
    private final OwnerEmailCallback callback;

    public interface OwnerEmailCallback {
        void onOwnerEmailReceived(String ownerEmail);
        void onFailure(Throwable t);
    }

    public FetchOwnerEmailTask(AccommodationApi api, int accommodationId, OwnerEmailCallback callback) {
        this.api = api;
        this.accommodationId = accommodationId;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            // Perform the API call on a background thread
            Response<Map<String, String>> response = api.getUserIdBasedOnAccommodationId(accommodationId).execute();

            if (response.isSuccessful()) {
                Map<String, String> responseBody = response.body();
                return responseBody.get("ownerID");
            } else {
                // Handle the error
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String ownerEmail) {
        super.onPostExecute(ownerEmail);

        if (ownerEmail != null) {
            callback.onOwnerEmailReceived(ownerEmail);
        } else {
            callback.onFailure(new Exception("Failed to fetch owner's email"));
        }
    }
}
