package com.example.bookit;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import com.example.bookit.model.Review;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReviewApi;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApproveReviews extends AppCompatActivity {

    private ReviewApi reviewApi;
    private ListView reviewListView;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_reviews_and_ratings);

        reviewApi = new RetrofitService(getApplicationContext()).getRetrofit().create(ReviewApi.class);

        reviewAdapter = new ReviewAdapter(this, new ArrayList<>());

        reviewListView = findViewById(R.id.reviewListView);
        reviewListView.setAdapter(reviewAdapter);

        reviewListView.setOnItemClickListener((parent, view, position, id) -> {
            Review selectedReview = reviewAdapter.getItem(position);
        });

        fetchAccommodationReviewsForApproval();
        fetchOwnerReviewsForApproval();

    }

    //-------------------------- FETCH AND DISPLAY DATA
    private void fetchAccommodationReviewsForApproval() {
        Call<List<Optional<Review>>> call = reviewApi.getAllReviewAccommodationForApproval();
        call.enqueue(new Callback<List<Optional<Review>>>() {
            @Override
            public void onResponse(Call<List<Optional<Review>>> call, Response<List<Optional<Review>>> response) {
                if (response.isSuccessful()) {
                    List<Optional<Review>> accommodationReviews = response.body();

                    // Log the raw JSON response
                    String rawJson = new Gson().toJson(accommodationReviews);
                    Log.d(TAG, "Raw JSON response: " + rawJson);

                    displayNonEmptyReviews(accommodationReviews);
                } else {
                    Toast.makeText(ApproveReviews.this, "Failed to fetch accommodation reviews for approval", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Optional<Review>>> call, Throwable t) {
                Toast.makeText(ApproveReviews.this, "Failed to fetch accommodation reviews for approval", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchOwnerReviewsForApproval() {
        Call<List<Optional<Review>>> call = reviewApi.getAllReviewOwnerForApproval();
        call.enqueue(new Callback<List<Optional<Review>>>() {
            @Override
            public void onResponse(Call<List<Optional<Review>>> call, Response<List<Optional<Review>>> response) {
                if (response.isSuccessful()) {
                    List<Optional<Review>> reviews = response.body();
                    Log.d(TAG, "Response body: " + reviews);
                    displayNonEmptyReviews(reviews);
                } else {
                    Log.d(TAG, "Failed to fetch reviews. Response code: " + response.code());
                    Toast.makeText(ApproveReviews.this, "Failed to fetch reviews", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Optional<Review>>> call, Throwable t) {
                Toast.makeText(ApproveReviews.this, "Failed to fetch owner reviews for approval", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void displayNonEmptyReviews(List<Optional<Review>> reviews) {
        Log.d(TAG, "Displaying reviews: " + reviews.size());
        reviewAdapter.clear();
        for (Optional<Review> optionalReview : reviews) {
            optionalReview.ifPresent(review -> {
                Log.d(TAG, "Added review: " + review.getId() + ", " + review.getText());
                reviewAdapter.add(review);
            });
        }
        reviewAdapter.notifyDataSetChanged();
    }



    // ---------------------- APPROVE AND DELETE A SELECTED REVIEW
    private void approveSelectedReview(Review selectedReview) {
        Call<Review> call = reviewApi.updateReviewStatus(selectedReview.getId(), selectedReview);
        handleApprovalResponse(call, selectedReview);
    }

    private void deleteSelectedReview(Review selectedReview) {
        Call<Void> call;
        if(selectedReview.getAccommodationId()!=-100){
        call = reviewApi.deleteAccommodationReview(selectedReview.getId());}
        else{ call = reviewApi.deleteOwnerReview(selectedReview.getId());}


        handleDeleteResponse(call, selectedReview);
    }


    //-------------------- HANDLE REPSPONSES


    private void handleApprovalResponse(Call<Review> call, Review selectedReview) {
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    Review updatedReview = response.body();
                    reviewAdapter.remove(selectedReview);
                    Toast.makeText(ApproveReviews.this, "Review approval successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ApproveReviews.this, "Failed to approve review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(ApproveReviews.this, "Failed to approve review", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleDeleteResponse(Call<Void> call, Review selectedReview) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    reviewAdapter.remove(selectedReview);
                    Toast.makeText(ApproveReviews.this, "Review operation successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ApproveReviews.this, "Failed to process review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ApproveReviews.this, "Failed to process review", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
