package com.example.bookit;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookit.model.Review;
import com.example.bookit.model.ReviewAccommodation;
import com.example.bookit.model.ReviewOwner;

import java.util.List;

// ReviewAdapter.java
// ReviewAdapter.java
public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the review at the current position
        Review review = getItem(position);

        // Use the appropriate layout for the review type (accommodation or owner)
        View itemView = inflateReviewLayout(review, parent);

        // Handle the data binding for the review type
        bindData(itemView, review);

        return itemView;
    }

    private View inflateReviewLayout(Review review, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (review instanceof ReviewAccommodation) {
            return inflater.inflate(R.layout.accommodation_review_approval, parent, false);
        } else if (review instanceof ReviewOwner) {
            return inflater.inflate(R.layout.owner_review_approval, parent, false);
        }
        return inflater.inflate(R.layout.review_card_container, parent, false);
    }

    private void bindData(View itemView, Review review) {
        // Implement data binding logic based on the review type
        if (review instanceof ReviewAccommodation) {
            ReviewAccommodation accommodationReview = (ReviewAccommodation) review;
            // Bind data for accommodation review
            // Example: ((TextView) itemView.findViewById(R.id.apartmentNameTextView)).setText(accommodationReview.getApartmentName());
        } else if (review instanceof ReviewOwner) {
            ReviewOwner ownerReview = (ReviewOwner) review;
            // Bind data for owner review
            // Example: ((TextView) itemView.findViewById(R.id.ownerNameTextView)).setText(ownerReview.getOwnerName());
        }

        // Handle approve and delete button clicks if needed
        // Example:
        // itemView.findViewById(R.id.approveButton).setOnClickListener(v -> approveReview(review));
        // itemView.findViewById(R.id.deleteButton).setOnClickListener(v -> deleteReview(review));
    }
}


