package com.example.bookit;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.bookit.model.Review;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookit.model.enums.ReviewStatus;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReviewApi;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private List<Review> displayReviews;
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
            showConfirmationDialog(selectedReview);
        });

        fetchAccommodationReviewsForApproval();
        fetchOwnerReviewsForApproval();

    }


    //-------------------------- FETCH AND DISPLAY DATA
    private void fetchAccommodationReviewsForApproval() {
        Call<List<Review>> call = reviewApi.getAllReviewAccommodationForApprovalAndroid();
        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    List<Review> accommodationReviews = response.body();
                    displayNonEmptyReviews(accommodationReviews);
                } else {
                    handleFailure("Failed to fetch accommodation reviews for approval", response);
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                t.printStackTrace();
                handleFailure("Failed to fetch accommodation reviews for approval", null);
            }
        });
    }

    private void fetchOwnerReviewsForApproval() {
        Call<List<Review>> call = reviewApi.getAllReviewOwnerForApprovalAndroid();
        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    List<Review> reviews = response.body();
                    displayNonEmptyReviews(reviews);
                } else {
                    handleFailure("Failed to fetch reviews", response);
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                t.printStackTrace();
                handleFailure("Failed to fetch reviews", null);
            }
        });
    }

    private void handleFailure(String message, Response<List<Review>> response) {
        if (response != null) {
            Log.e(TAG, "Error response code: " + response.code());
            try {
                Log.e(TAG, "Error response body: " + response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(ApproveReviews.this, message, Toast.LENGTH_SHORT).show();
    }

    private void displayNonEmptyReviews(List<Review> reviews) {
        for (Review optionalReview : reviews) {
            if (!reviewAdapter.containsReview(optionalReview)) {
                reviewAdapter.add(optionalReview);
            }
        }
        reviewAdapter.notifyDataSetChanged();
    }


    // ---------------------- APPROVE AND DELETE A SELECTED REVIEW
    private void showConfirmationDialog(Review selectedReview) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the color resource
        int colorLogo = ContextCompat.getColor(this, R.color.logo);

        // Create a SpannableString to apply color
        String messageText = "Do you want to approve or delete this review?";
        SpannableString spannableString = new SpannableString(messageText);
        spannableString.setSpan(new ForegroundColorSpan(colorLogo), 0, messageText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle("Review Action")
                .setMessage(spannableString)
                .setPositiveButton("Approve", (dialog, which) -> {
                    approveSelectedReview(selectedReview);
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    deleteSelectedReview(selectedReview);
                })
                .setNeutralButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    //TODO: NAMESTI NA BACKU APPROVAL KAKO TREBA
    private void approveSelectedReview(Review selectedReview) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = selectedReview.getCreatedAt().format(formatter);
        selectedReview.setCreatedAt(LocalDateTime.parse(formattedDateTime, formatter));
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
        selectedReview.setReviewStatus(ReviewStatus.APPROVED);
        Log.d("Review", "JSON Payload: " + new Gson().toJson(selectedReview));

        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    Review updatedReview = response.body();
                    reviewAdapter.remove(selectedReview);
                    Toast.makeText(ApproveReviews.this, "Review approval successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ApproveReviews.this, "Approval not successful", Toast.LENGTH_SHORT).show();
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
