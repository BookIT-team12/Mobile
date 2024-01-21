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
import com.example.bookit.model.Accommodation;
import com.example.bookit.model.Review;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.ReviewApi;
import com.example.bookit.utils.asyncTasks.FetchAccommodationDetailsTask;

import java.util.List;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accommodation_review_approval, parent, false);
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

        Button deleteReviewBtn = new Button(holder.itemView.getContext());
        deleteReviewBtn.setText("Delete review");

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
            reviewApi.deleteAccommodationReview(review.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    allAuthorAccommodationReviews.remove(review);
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
        return allAuthorAccommodationReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView accommodationHeaderTF, authorTF, gradeTF, commentTF;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accommodationHeaderTF = itemView.findViewById(R.id.apartmentNameTextView);
            authorTF = itemView.findViewById(R.id.authorTextView);
            gradeTF = itemView.findViewById(R.id.gradeTextView);
            commentTF = itemView.findViewById(R.id.commentTextView);
            linearLayout = itemView.findViewById(R.id.blueSquareContainer);
        }
    }
}
