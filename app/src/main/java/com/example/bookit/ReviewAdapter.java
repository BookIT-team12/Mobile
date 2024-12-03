package com.example.bookit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookit.model.Review;

import java.util.List;

// ReviewAdapter.java
public class ReviewAdapter extends ArrayAdapter<Review> {

    private static final int TYPE_ACCOMMODATION = 0;
    private static final int TYPE_OWNER = 1;

    public ReviewAdapter(Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }
    public boolean containsReview(Review review) {
        for (int i = 0; i < getCount(); i++) {
            Review existingReview = getItem(i);
            if (existingReview != null && existingReview.getId() == review.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        Review review = getItem(position);
        if (review != null) {
            Log.d("Reviews ", "Review ID:"+review.getId());
            if (review.getOwnerEmail() == null) {
                Log.d("ReviewAdapter", "Type: Accommodation, Position: " + position);

                return TYPE_ACCOMMODATION;
            } else if (review.getOwnerEmail() != null) {
                Log.d("ReviewAdapter", "Type: Owner, Position: " + position);

                return TYPE_OWNER;
            }
        }
        Log.d("ReviewAdapter", "Unknown type, Position: " + position);

        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Number of view types
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);

        Log.d("ReviewAdapter", "getView called for position: " + position + ", viewType: " + viewType);

        Review review = getItem(position);
        View itemView = convertView;

        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            itemView = inflateReviewLayout(viewType, inflater, parent);
        }

        bindData(itemView, review);

        return itemView;
    }

    private View inflateReviewLayout(int viewType, LayoutInflater inflater, ViewGroup parent) {
        if (viewType == TYPE_ACCOMMODATION) {
            return inflater.inflate(R.layout.accommodation_review_approval, parent, false);
        } else if (viewType == TYPE_OWNER) {
            return inflater.inflate(R.layout.owner_review_approval, parent, false);
        }
        return inflater.inflate(R.layout.review_card_container, parent, false);
    }

    private void bindData(View itemView, Review review) {
        if (review != null) {

            if (review.getAccommodationId() != -100) {
                String accommodation = "Accommodation ID: " + review.getAccommodationId();
                String comment = "Comment: " + review.getText();
                String author = "Author: " + review.getAuthorEmail();
                String rating = "Grade: " + review.getRating();
                ((TextView) itemView.findViewById(R.id.apartmentNameTextView)).setText(accommodation);
                ((TextView) itemView.findViewById(R.id.commentTextView)).setText(comment);
                ((TextView) itemView.findViewById(R.id.gradeTextView)).setText(author);
                ((TextView) itemView.findViewById(R.id.authorTextView)).setText(rating);
            } else if (review.getOwnerEmail() != null) {
                // Owner review
                String owner = "Owner: " + review.getOwnerEmail();
                String comment = "Comment: " + review.getText();
                String author = "Author: " + review.getAuthorEmail();
                String rating = "Grade: " + review.getRating();
                ((TextView) itemView.findViewById(R.id.ownerReview)).setText(owner);
                ((TextView) itemView.findViewById(R.id.commentReview)).setText(comment);
                ((TextView) itemView.findViewById(R.id.gradeReview)).setText(rating);
                ((TextView) itemView.findViewById(R.id.authorReview)).setText(author);
            }
        }
    }
}




