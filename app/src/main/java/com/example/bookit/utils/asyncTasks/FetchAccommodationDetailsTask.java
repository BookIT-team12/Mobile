package com.example.bookit.utils.asyncTasks;

import android.os.AsyncTask;

import com.example.bookit.model.Accommodation;
import com.example.bookit.model.ResponseAccommodationImages;
import com.example.bookit.retrofit.api.AccommodationApi;

import java.io.IOException;

import retrofit2.Response;

public class FetchAccommodationDetailsTask extends AsyncTask<Void, Void, Accommodation> {
    private final AccommodationApi accommodationApi;
    private final int accommodationId;
    private final CallbackListener callbackListener;

    public FetchAccommodationDetailsTask(AccommodationApi accommodationApi, int accommodationId, CallbackListener callbackListener) {
        this.accommodationApi = accommodationApi;
        this.accommodationId = accommodationId;
        this.callbackListener = callbackListener;
    }

    @Override
    protected Accommodation doInBackground(Void... voids) {
        try {
            Response<ResponseAccommodationImages> response = accommodationApi.viewAccommodationDetails(accommodationId).execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body().getFirst();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Accommodation accommodation) {
        if (accommodation != null) {
            callbackListener.onSuccess(accommodation);
        } else {
            callbackListener.onFailure();
        }
    }

    public interface CallbackListener {
        void onSuccess(Accommodation accommodation);
        void onFailure();
    }
}
