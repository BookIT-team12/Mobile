package com.example.bookit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookit.model.Reservation;

import java.util.List;
public class ReservationAdapter extends ArrayAdapter<Reservation> {

    public ReservationAdapter(Context context, List<Reservation> reservations) {
        super(context, 0, reservations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            itemView = inflater.inflate(R.layout.reservation_card, parent, false);
        }

        Reservation reservation = getItem(position);

        if (reservation != null) {
            bindReservationDetails(itemView, reservation);
        }

        return itemView;
    }

    private void bindReservationDetails(View itemView, Reservation reservation) {
        TextView username = itemView.findViewById(R.id.resUsername);
        TextView accommodation = itemView.findViewById(R.id.resAccommodation);
        TextView reservationStatus = itemView.findViewById(R.id.resStatus);
        TextView guestNumber = itemView.findViewById(R.id.resGuests);
        TextView price = itemView.findViewById(R.id.resPrice);
        TextView endDate = itemView.findViewById(R.id.resEnd);
        TextView startDate = itemView.findViewById(R.id.resStart);

        String usernameDetails="Guest email: "+reservation.getGuestEmail();
        String accommodationDetails="Accommodation ID: "+reservation.getAccommodationId();
        String statusDetails="Reservation status: "+reservation.getStatus();
        String guestDetails="Number of guests: "+reservation.getNumberOfGuests();
        String priceDetails="Price: "+reservation.getPrice();
        String endDetails="End start: "+reservation.getStartDate();
        String startDetails="Start date: "+reservation.getEndDate();

        username.setText(usernameDetails);
        accommodation.setText(accommodationDetails);
        reservationStatus.setText(statusDetails);
        guestNumber.setText(guestDetails);
        price.setText(priceDetails);
        endDate.setText(endDetails);
        startDate.setText(startDetails);


    }
}