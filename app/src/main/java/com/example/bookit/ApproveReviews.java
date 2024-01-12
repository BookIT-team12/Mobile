package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;
import com.example.bookit.model.Review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookit.model.ReviewAccommodation;
import com.example.bookit.model.ReviewOwner;
import com.example.bookit.model.enums.ReviewStatus;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReviewApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApproveReviews extends AppCompatActivity {

    private ReviewApi reviewApi;
    private ListView reviewListView;
    private Button deleteButton;
    private Button approveButton;

    private ReviewAdapter reviewAdapter;

    //TODO: SOLVE APPROVE AND DELETE BUTTON BINDING AS WELL AS THE DATA BINDING IN THE EVERY SINGLE CONTAINER!!!!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_reviews_and_ratings);

        reviewApi = new RetrofitService().getRetrofit().create(ReviewApi.class);

        reviewAdapter = new ReviewAdapter(this, new ArrayList<>());

        reviewListView = findViewById(R.id.reviewListView);
        reviewListView.setAdapter(reviewAdapter);


//        approveButton = findViewById(R.id.approveButton);
//        deleteButton = findViewById(R.id.deleteButton);

        fetchAccommodationReviewsForApproval();

//        approveButton.setOnClickListener(v -> approveSelectedReview());
//        deleteButton.setOnClickListener(v -> deleteSelectedReview());
    }

    private void fetchAccommodationReviewsForApproval() {
        Call<List<Optional<Review>>> call = reviewApi.getAllReviewAccommodationForApproval();
        call.enqueue(new Callback<List<Optional<Review>>>() {
            @Override
            public void onResponse(Call<List<Optional<Review>>> call, Response<List<Optional<Review>>> response) {
                if (response.isSuccessful()) {
                    List<Optional<Review>> accommodationReviews = response.body();
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
                    List<Optional<Review>> ownerReviews = response.body();
                    displayNonEmptyReviews(ownerReviews);
                } else {
                    Toast.makeText(ApproveReviews.this, "Failed to fetch owner reviews for approval", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Optional<Review>>> call, Throwable t) {
                Toast.makeText(ApproveReviews.this, "Failed to fetch owner reviews for approval", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayNonEmptyReviews(List<Optional<Review>> reviews) {
        reviewAdapter.clear();
        for (Optional<Review> optionalReview : reviews) {
            optionalReview.ifPresent(reviewAdapter::add);
        }
    }

    private void approveSelectedReview() {
        Review selectedReview = (Review) reviewListView.getSelectedItem();
        if (selectedReview != null) {
            Call<Review> call = reviewApi.updateReviewStatus(selectedReview.getId(), selectedReview);
            call.enqueue(new Callback<Review>() {
                @Override
                public void onResponse(Call<Review> call, Response<Review> response) {
                    if (response.isSuccessful()) {
                        displayNonEmptyReviews(Collections.singletonList(Optional.of(response.body())));
                        Toast.makeText(ApproveReviews.this, "Review approved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ApproveReviews.this, "Failed to approve review", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Review> call, Throwable t) {
                    Toast.makeText(ApproveReviews.this, "Failed to approve review", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please select a review to approve", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSelectedReview() {
        Review selectedReview = (Review) reviewListView.getSelectedItem();
        if (selectedReview != null) {
            if (selectedReview instanceof ReviewAccommodation) {
                deleteAccommodationReview((ReviewAccommodation) selectedReview);
            } else if (selectedReview instanceof ReviewOwner) {
                deleteOwnerReview((ReviewOwner) selectedReview);
            } else {
                Toast.makeText(this, "Unknown review type", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select a review to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAccommodationReview(Review selectedReview) {
        Call<Void> call = reviewApi.deleteAccommodationReview(selectedReview.getId());
        handleDeleteResponse(call, selectedReview);
    }

    private void deleteOwnerReview(Review selectedReview) {
        Call<Void> call = reviewApi.deleteOwnerReview(selectedReview.getId());
        handleDeleteResponse(call, selectedReview);
    }

    private void handleDeleteResponse(Call<Void> call, Review selectedReview) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    displayNonEmptyReviews(Collections.singletonList(Optional.empty()));
                    Toast.makeText(ApproveReviews.this, "Review deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ApproveReviews.this, "Failed to delete review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ApproveReviews.this, "Failed to delete review", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
