package com.example.bookit.utils.asyncTasks;

import android.os.AsyncTask;

import com.example.bookit.retrofit.api.AccommodationApi;

import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Response;

public class FetchAccommodationNameTask extends AsyncTask<Void, Void, String> {
    private int accommodationId;
    private AccommodationApi accommodationApi;
    private Callback<String> callback;

    public FetchAccommodationNameTask(AccommodationApi accommodationApi, int accommodationId, Callback<String> callback) {
        this.accommodationApi = accommodationApi;
        this.accommodationId = accommodationId;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            // Perform the API call on a background thread
            Response<String> response = accommodationApi.getNameById(accommodationId).execute();

            if (response.isSuccessful()) {
                return response.body();
            } else {
                // Handle the error
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // This method is executed on the main thread after doInBackground is finished
        if (result != null) {
            callback.onResponse(null, Response.success(result));
        } else {
            callback.onFailure(null, new Throwable("Failed to get accommodation name"));
        }
    }
}
