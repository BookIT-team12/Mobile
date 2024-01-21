package com.example.bookit.utils.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookit.R;
import com.example.bookit.ReviewAccommodationActivity;
import com.example.bookit.ReviewOwnerActivity;
import com.example.bookit.model.ResponseAccommodationImages;
import com.example.bookit.utils.StringImageConverter;

import java.util.List;
import java.util.Map;

// Your custom adapter class
public class AccommodationRecycleViewAdapter extends RecyclerView.Adapter<AccommodationRecycleViewAdapter.ViewHolder> {
    private List<ResponseAccommodationImages> accommodationList;
    private Map<Integer, Boolean> containing;

    public AccommodationRecycleViewAdapter(List<ResponseAccommodationImages> accommodationList, Map<Integer, Boolean> containing) {
        this.accommodationList = accommodationList;
        this.containing = containing;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_visited_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResponseAccommodationImages accommodation = accommodationList.get(position);
        Boolean canReviewAccommodation = containing.get(accommodation.getFirst().getId());
        // Bind data to the views
        holder.textViewName.setText(accommodation.getFirst().getName());
        holder.textViewDescription.setText(String.format("Description: %s", accommodation.getFirst().getDescription()));
        holder.textViewOwner.setText(String.format("Owner: %s", accommodation.getFirst().getOwnerEmail()));
        holder.textViewMinGuests.setText(String.format("Min number of guests: %s", accommodation.getFirst().getMinGuests()));
        holder.textViewMaxGuests.setText(String.format("Maximal number of guests: %s", accommodation.getFirst().getMaxGuests()));
        holder.reviewAccommodationBtn.setEnabled(canReviewAccommodation);

        List<byte[]> allImages = StringImageConverter.turnStringsToBytes(accommodation.getSecond());
        Bitmap bitmap = BitmapFactory.decodeByteArray(allImages.get(0), 0, allImages.get(0).length);

        holder.image.setImageBitmap(bitmap);

        holder.reviewOwnerBtn.setOnClickListener(view -> {
            // Start ReviewOwnerActivity
            Intent intent = new Intent(view.getContext(), ReviewOwnerActivity.class);
            // If you need to pass any data to the ReviewOwnerActivity, you can use putExtra here
             intent.putExtra("owner", accommodation.getFirst().getOwnerEmail());

            view.getContext().startActivity(intent);
        });

        holder.reviewAccommodationBtn.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ReviewAccommodationActivity.class);
            intent.putExtra("accommodationId", accommodation.getFirst().getId());
            intent.putExtra("ownerEmail", accommodation.getFirst().getOwnerEmail());
            intent.putExtra("accommodationName", accommodation.getFirst().getName());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return accommodationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewDescription;
        ImageView image;
        TextView textViewOwner;
        TextView textViewMinGuests;
        TextView textViewMaxGuests;
        Button reviewAccommodationBtn;
        Button reviewOwnerBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.accommodationHeader_placesVisitedCard);
            textViewDescription = itemView.findViewById(R.id.descriptionTextView);
            image = itemView.findViewById(R.id.imageView);
            textViewOwner = itemView.findViewById(R.id.ownerTextView);
            textViewMinGuests = itemView.findViewById(R.id.minGuestsTextView);
            textViewMaxGuests = itemView.findViewById(R.id.maxGuestsTextView);
            reviewAccommodationBtn = itemView.findViewById(R.id.reviewAccommodationBtn);
            reviewOwnerBtn = itemView.findViewById(R.id.reviewOwnerBtn);
            // Initialize other TextViews
        }
    }
}
