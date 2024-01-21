package com.example.bookit.utils.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookit.R;
import com.example.bookit.model.Review;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReviewApi;

import java.util.List;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_review_approval, parent, false);
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

        Button deleteReviewBtn = new Button(holder.itemView.getContext());
        deleteReviewBtn.setText("Report review");

        int colorResId = R.color.logo; // Replace with your color resource ID
        int color = ContextCompat.getColor(holder.itemView.getContext(), colorResId);

        // Create a rounded shape drawable
        GradientDrawable shapeDrawable = new GradientDrawable();
        shapeDrawable.setColor(color);
        shapeDrawable.setCornerRadius(40); // Adjust the corner radius as needed

        // Set the shape drawable as the background of the button
        deleteReviewBtn.setBackground(shapeDrawable);

        // Add the button to the item layout
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 10, 16, 0); // Adjust the margins as needed

        // Set the layout parameters for the button
        deleteReviewBtn.setLayoutParams(layoutParams);

        holder.linearLayout.addView(deleteReviewBtn, layoutParams);

        deleteReviewBtn.setOnClickListener(v -> {
            reviewApi.deleteOwnerReview(review.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    allAuthorsOwnerReviews.remove(review);
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
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
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ownerHeaderTF = itemView.findViewById(R.id.ownerReview);
            authorTF = itemView.findViewById(R.id.authorReview);
            gradeTF = itemView.findViewById(R.id.gradeReview);
            commentTF = itemView.findViewById(R.id.commentReview);
            linearLayout = itemView.findViewById(R.id.blueSquareContainer);
        }
    }
}
