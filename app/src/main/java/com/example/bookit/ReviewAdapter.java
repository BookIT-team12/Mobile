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
// ReviewAdapter.java
public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d("ReviewAdapter", "getView called for position: " + position);


        Review review = getItem(position);

        View itemView = inflateReviewLayout(review, parent);

        bindData(itemView, review);

        return itemView;
    }

    private View inflateReviewLayout(Review review, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (review.getAccommodationId()!=-100) {
            return inflater.inflate(R.layout.accommodation_review_approval, parent, false);
        } else if (review.getAuthorEmail()!=null) {
            return inflater.inflate(R.layout.owner_review_approval, parent, false);
        }
        return inflater.inflate(R.layout.review_card_container, parent, false);
    }

    private void bindData(View itemView, Review review) {
        if (review.getAuthorEmail()==null) {
            ((TextView) itemView.findViewById(R.id.apartmentNameTextView)).setText(review.getAccommodationId());
            ((TextView) itemView.findViewById(R.id.commentTextView)).setText(review.getText());
            ((TextView) itemView.findViewById(R.id.gradeTextView)).setText(String.valueOf(review.getRating()));
            ((TextView) itemView.findViewById(R.id.authorTextView)).setText(review.getAuthorEmail());

        } else{
            ((TextView) itemView.findViewById(R.id.ownerReview)).setText(review.getOwnerEmail());
            ((TextView) itemView.findViewById(R.id.commentReview)).setText(review.getText());
            ((TextView) itemView.findViewById(R.id.gradeReview)).setText(String.valueOf(review.getRating()));
            ((TextView) itemView.findViewById(R.id.authorReview)).setText(review.getAuthorEmail());

        }
    }
}


