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
import com.example.bookit.model.enums.ReviewStatus;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReviewApi;

import org.mapsforge.map.rendertheme.renderinstruction.Line;

import java.util.List;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_review_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = allOwnerReviewsOnOwner.get(position);

        holder.authorTF.setText(String.format("Author: %s", review.getAuthorEmail()));
        holder.gradeTF.setText(String.format("Given grade: %s", review.getRating()));
        holder.commentTF.setText(String.format("Comment: %s", review.getText()));

        Button reportReviewBtn = new Button(holder.itemView.getContext());
        reportReviewBtn.setText("Report review");

        int colorResId = R.color.logo; // Replace with your color resource ID
        int color = ContextCompat.getColor(holder.itemView.getContext(), colorResId);

        // Create a rounded shape drawable
        GradientDrawable shapeDrawable = new GradientDrawable();
        shapeDrawable.setColor(color);
        shapeDrawable.setCornerRadius(40); // Adjust the corner radius as needed

        // Set the shape drawable as the background of the button
        reportReviewBtn.setBackground(shapeDrawable);

        // Add the button to the item layout
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 10, 16, 0); // Adjust the margins as needed

        // Set the layout parameters for the button
        reportReviewBtn.setLayoutParams(layoutParams);

        holder.linearLayout.addView(reportReviewBtn, layoutParams);

        // Implement button click listener if needed
        reportReviewBtn.setOnClickListener(v -> {
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
        TextView authorTF, gradeTF, commentTF, ownerTF;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTF = itemView.findViewById(R.id.authorReview);
            gradeTF = itemView.findViewById(R.id.gradeReview);
            commentTF = itemView.findViewById(R.id.commentReview);
            ownerTF = itemView.findViewById(R.id.ownerReview);
            linearLayout = itemView.findViewById(R.id.blueSquareContainer);
        }
    }
}
