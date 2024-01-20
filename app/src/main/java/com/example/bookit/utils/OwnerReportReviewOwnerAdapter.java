package com.example.bookit.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookit.R;
import com.example.bookit.model.Review;
import com.example.bookit.model.enums.ReviewStatus;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReviewApi;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerReportReviewOwnerAdapter extends RecyclerView.Adapter<OwnerReportReviewOwnerAdapter.ViewHolder>{
    private List<Review> allOwnerReviewsOnOwner;
    ReviewApi reviewApi;

    public OwnerReportReviewOwnerAdapter(List<Review> dataList, RetrofitService service) {
        this.allOwnerReviewsOnOwner = dataList;
        reviewApi = service.getRetrofit().create(ReviewApi.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_report_review_owner_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = allOwnerReviewsOnOwner.get(position);

        holder.authorTF.setText(String.format("Author: %s", review.getAuthorEmail()));
        holder.gradeTF.setText(String.format("Given grade: %s", review.getRating()));
        holder.commentTF.setText(String.format("Comment: %s", review.getText()));

        // Implement button click listener if needed
        holder.reportReviewBtn.setOnClickListener(v -> {
            Review toSend = review;
            review.setStatus(ReviewStatus.REPORTED);
            reviewApi.updateReviewStatus(review.getId(), review).enqueue(new Callback<Review>() {
                @Override
                public void onResponse(Call<Review> call, Response<Review> response) {
                    allOwnerReviewsOnOwner.remove(review);
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
        return allOwnerReviewsOnOwner.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView authorTF, gradeTF, commentTF;
        Button reportReviewBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTF = itemView.findViewById(R.id.authorNameTextView_owner_report_review_owner_card);
            gradeTF = itemView.findViewById(R.id.gradeTextView_owner_report_review_owner_card);
            commentTF = itemView.findViewById(R.id.commentTextView_owner_report_review_owner_card);
            reportReviewBtn = itemView.findViewById(R.id.reportReview_owner_report_review_owner_card);
        }
    }
}
