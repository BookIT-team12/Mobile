package com.example.bookit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookit.model.Accommodation;

import java.util.List;

public class AccommodationApprovalAdapter extends ArrayAdapter<Accommodation> {
    public AccommodationApprovalAdapter(@NonNull Context context, @NonNull List<Accommodation> accommodations) {
        super(context, 0, accommodations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.accommodation_approval_item, parent, false);
        }

        // Get the current accommodation item
        Accommodation accommodation = getItem(position);

        // Update the UI elements in the list item layout based on the accommodation data
        TextView headerTF = convertView.findViewById(R.id.accommodation_approval_item_header);
        TextView ownerTF = convertView.findViewById(R.id.accommodation_approval_item_owner);
        TextView typeTF = convertView.findViewById(R.id.accommodation_approval_item_type);
        TextView infoTF = convertView.findViewById(R.id.accommodation_approval_item_info);

        if (accommodation != null) {
            String header = accommodation.getName();
            headerTF.setText(header);

            String ownerInfo = "\nOwner: " + accommodation.getOwnerEmail();
            ownerTF.setText(ownerInfo);

            String typeInfo = "Type: " + accommodation.getAccommodationType();
            typeTF.setText(typeInfo);

            String info = "Min Guests: " + accommodation.getMinGuests() + "\n" +
                    "Max Guests: " + accommodation.getMaxGuests() + "\n" +
                    "Description: " + accommodation.getDescription();
            infoTF.setText(info);
        }

        return convertView;
    }
}

