package com.example.bookit.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookit.R;
import com.example.bookit.model.Accommodation;
import com.example.bookit.model.ResponseAccommodationImages;
import com.example.bookit.model.Review;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.ReviewApi;
import com.example.bookit.utils.asyncTasks.FetchAccommodationDetailsTask;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewAccommodationRecycleViewAdapter extends RecyclerView.Adapter<ReviewAccommodationRecycleViewAdapter.ViewHolder>{

    private List<Review> allAuthorAccommodationReviews;
    ReviewApi reviewApi;
    AccommodationApi accommodationApi;

    public ReviewAccommodationRecycleViewAdapter(List<Review> dataList, RetrofitService service) {
        this.allAuthorAccommodationReviews = dataList;
        reviewApi = service.getRetrofit().create(ReviewApi.class);
        accommodationApi = service.getRetrofit().create(AccommodationApi.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accommodation_review_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = allAuthorAccommodationReviews.get(position);
        final Accommodation[] reviewedAccommodation = new Accommodation[1];
        new FetchAccommodationDetailsTask(accommodationApi, review.getAccommodationId(), new FetchAccommodationDetailsTask.CallbackListener() {
            @Override
            public void onSuccess(Accommodation accommodation) {
                reviewedAccommodation[0] = accommodation;
                // Set data to views
                holder.accommodationHeaderTF.setText((reviewedAccommodation[0].getName()));
                holder.authorTF.setText(String.format("Author: %s", review.getAuthorEmail()));
                holder.gradeTF.setText(String.format("Given grade: %s", review.getRating()));
                holder.commentTF.setText(String.format("Comment: %s", review.getText()));
            }

            @Override
            public void onFailure() {
                // Handle failure
            }
        }).execute();


        // Implement button click listener if needed
        holder.deleteReviewBtn.setOnClickListener(v -> {
            reviewApi.deleteAccommodationReview(review.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    allAuthorAccommodationReviews.remove(review);
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("LOSE JBG");
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return allAuthorAccommodationReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView accommodationHeaderTF, authorTF, gradeTF, commentTF;
        Button deleteReviewBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accommodationHeaderTF = itemView.findViewById(R.id.accommodationNameTextView_accommodation_review_card);
            authorTF = itemView.findViewById(R.id.authorTextView_accommodation_review_card);
            gradeTF = itemView.findViewById(R.id.gradeTextView_accommodation_review_card);
            commentTF = itemView.findViewById(R.id.commentTextView_accommodation_review_card);
            deleteReviewBtn = itemView.findViewById(R.id.deleteReview_accommodation_review_card);
        }
    }
}
