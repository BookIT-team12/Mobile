package com.example.bookit.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookit.OwnerReportReviewAccommodation;
import com.example.bookit.R;
import com.example.bookit.model.Accommodation;
import com.example.bookit.model.Review;
import com.example.bookit.model.enums.ReviewStatus;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.ReviewApi;
import com.example.bookit.utils.asyncTasks.FetchAccommodationNameTask;
import com.example.bookit.utils.asyncTasks.FetchAverageAccommodatioGradeTask;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerReportReviewAccommodationAdapter extends RecyclerView.Adapter<OwnerReportReviewAccommodationAdapter.ViewHolder> {
    private List<Review> allAccommodationReviewsOnOwnerAccommodations;
    ReviewApi reviewApi;
    AccommodationApi accommodationApi;

    public OwnerReportReviewAccommodationAdapter(List<Review> dataList, RetrofitService service) {
        this.allAccommodationReviewsOnOwnerAccommodations = dataList;
        reviewApi = service.getRetrofit().create(ReviewApi.class);
        accommodationApi = service.getRetrofit().create(AccommodationApi.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_report_review_accommodation_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerReportReviewAccommodationAdapter.ViewHolder holder, int position) {
        Review review = allAccommodationReviewsOnOwnerAccommodations.get(position);

        holder.authorTF.setText(String.format("Author: %s", review.getAuthorEmail()));
        holder.gradeTF.setText(String.format("Given grade: %s", review.getRating()));
        holder.commentTF.setText(String.format("Comment: %s", review.getText()));

        new FetchAccommodationNameTask(accommodationApi, review.getAccommodationId(), new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // Handle the response here
                if (response.isSuccessful()) {
                    String accommodationName = response.body();
                    holder.accommodationNameTF.setText(String.format("Accommodation name: %s", accommodationName));
                } else {
                    System.out.println("Request for accommodation name ended badly");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("Request for accommodation name failed");
            }
        }).execute();



        // Implement button click listener if needed
        holder.reportReviewBtn.setOnClickListener(v -> {
            Review toSend = review;
            review.setStatus(ReviewStatus.REPORTED);
            reviewApi.updateReviewStatus(review, review.getId()).enqueue(new Callback<Review>() {
                @Override
                public void onResponse(Call<Review> call, Response<Review> response) {
                    allAccommodationReviewsOnOwnerAccommodations.remove(review);
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Review> call, Throwable t) {
                    System.out.println("LOSE JBG");
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return allAccommodationReviewsOnOwnerAccommodations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView authorTF, gradeTF, commentTF, accommodationNameTF;
        Button reportReviewBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTF = itemView.findViewById(R.id.authorNameTextView_owner_report_review_accommodation_card);
            gradeTF = itemView.findViewById(R.id.gradeTextView_owner_report_review_accommodation_card);
            accommodationNameTF = itemView.findViewById(R.id.accommodationNameTextView_owner_report_review_accommodation_card);
            commentTF = itemView.findViewById(R.id.commentTextView_owner_report_review_accommodation_card);
            reportReviewBtn = itemView.findViewById(R.id.reportReview_owner_report_review_accommodation_card);
        }
    }
}
