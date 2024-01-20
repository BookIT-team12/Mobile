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
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReviewApi;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewOwnerRecycleViewAdapter extends RecyclerView.Adapter<ReviewOwnerRecycleViewAdapter.ViewHolder> {

    private List<Review> allAuthorsOwnerReviews;
    ReviewApi reviewApi;

    public ReviewOwnerRecycleViewAdapter(List<Review> dataList, RetrofitService service) {
        this.allAuthorsOwnerReviews = dataList;
        reviewApi = service.getRetrofit().create(ReviewApi.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_review_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = allAuthorsOwnerReviews.get(position);

        // Set data to views
        holder.ownerHeaderTF.setText(review.getOwnerEmail());
        holder.authorTF.setText(String.format("Author: %s", review.getAuthorEmail()));
        holder.gradeTF.setText(String.format("Given grade: %s", review.getRating()));
        holder.commentTF.setText(String.format("Comment: %s", review.getText()));

        // Implement button click listener if needed
        holder.deleteReviewBtn.setOnClickListener(v -> {
            reviewApi.deleteOwnerReview(review.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    allAuthorsOwnerReviews.remove(review);
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
        return allAuthorsOwnerReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ownerHeaderTF, authorTF, gradeTF, commentTF;
        Button deleteReviewBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ownerHeaderTF = itemView.findViewById(R.id.ownerNameTextView_owner_review_card);
            authorTF = itemView.findViewById(R.id.authorTextView_owner_review_card);
            gradeTF = itemView.findViewById(R.id.gradeTextView_owner_review_card);
            commentTF = itemView.findViewById(R.id.commentTextView_owner_review_card);
            deleteReviewBtn = itemView.findViewById(R.id.deleteReview_owner_review_card);
        }
    }
}
