package com.example.bookit.utils.asyncTasks;

import android.os.AsyncTask;
import android.widget.TextView;

import androidx.loader.content.AsyncTaskLoader;

import com.example.bookit.ReviewAccommodationActivity;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.ReviewApi;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;

import retrofit2.Response;

public class FetchAverageAccommodatioGradeTask extends AsyncTask<Void, Void, Double> {
    private final ReviewApi api;
    private final int accommodationId;
    private final TextView averageGradeTF;

    public FetchAverageAccommodatioGradeTask(ReviewApi api, int accommodationId, TextView averageGradeTF) {
        this.api = api;
        this.accommodationId = accommodationId;
        this.averageGradeTF = averageGradeTF;
    }

    @Override
    protected Double doInBackground(Void... voids) {
        try {
            // Perform the API call on a background thread
            Response<Double> response = api.getAccommodationAverageGrade(accommodationId).execute();

            if (response.isSuccessful()) {
                return response.body();
            } else {
                // Handle the error
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Double averageGrade) {
        super.onPostExecute(averageGrade);

        if (averageGrade != null) {
            // Update UI components with the fetched data
            averageGradeTF.setText(String.format("Average grade: %s", averageGrade));
        } else {
            System.out.println("Something went wrong in fetching average grade for owner");
        }
    }
}
