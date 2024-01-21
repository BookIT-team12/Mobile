package com.example.bookit.utils.asyncTasks;

import android.os.AsyncTask;

import com.example.bookit.model.Review;
import com.example.bookit.retrofit.api.ReviewApi;
import com.example.bookit.utils.adapters.ReviewAccommodationRecycleViewAdapter;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class FetchAccommodationReviewsTask extends AsyncTask<Void, Void, List<Review>> {
    private final ReviewApi api;
    private final String loggedUser;
    private final List<Review> allAuthorReviews;
    private ReviewAccommodationRecycleViewAdapter adapter;

    public FetchAccommodationReviewsTask(ReviewApi api, String loggedUser, List<Review> allAuthorReviews, ReviewAccommodationRecycleViewAdapter adapter) {
        this.api = api;
        this.loggedUser = loggedUser;
        this.allAuthorReviews = allAuthorReviews;
        this.adapter = adapter;
    }

    @Override
    protected List<Review> doInBackground(Void... params) {
        try {
            // Perform the API call on a background thread
            Response<List<Review>> response = api.getReviewsAccommodationByAuthor(loggedUser).execute();

            if (response.isSuccessful()) {
                return response.body();
            } else {
                System.out.println("Greska pri dobijanju reviews accommodaiton by author");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Review> result) {
        // Handle the result on the main thread
        if (result != null) {
            allAuthorReviews.addAll(result);
        }
        adapter.notifyDataSetChanged();
    }
}
