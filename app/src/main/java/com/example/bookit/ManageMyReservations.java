 package com.example.bookit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookit.model.Reservation;
import com.example.bookit.model.User;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReservationApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

//TODO: NAMESTI PROSLEDJIVANJE ID-A ULOGOVANOG USERA!
 public class ManageMyReservations extends AppCompatActivity {


     private ReservationApi reservationApi;
     private ListView reservationListView;

     private Retrofit retrofit;

     private ReservationAdapter reservationAdapter;

     private User guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_my_reservations);


        retrofit = new RetrofitService(getApplicationContext()).getRetrofit();
        reservationApi = retrofit.create(ReservationApi.class);

        reservationAdapter = new ReservationAdapter(this, new ArrayList<>());

        reservationListView = findViewById(R.id.myReservationsList);
        reservationListView.setAdapter(reservationAdapter);

        reservationListView.setOnItemClickListener((parent, view, position, id) -> {
            Reservation selectedReservation = reservationAdapter.getItem(position);
            showCancelDialog(selectedReservation);
        });


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_VALUE")) {
            guest = (User) intent.getSerializableExtra("USER_VALUE");
        }

        fetchUserReservations(guest.getEmail());
    }


    private void fetchUserReservations(String userID){
        Call<List<Reservation>> call=reservationApi.getGuestReservations(userID);
        handleGuestReservationsResponse(call, "Successfully fetched reservations");
    }

     //------------- CANCEL A RESERVATION
     public void showCancelDialog(Reservation reservation) {
         Log.d("Dialog", "Preparing to show dialog");

         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         View view = getLayoutInflater().inflate(R.layout.approve_dialog, null);

         // Find TextView in the inflated view
         TextView dialogText = view.findViewById(R.id.dialogTextViewMessage);
         dialogText.setText("Are you sure you want to cancel this reservation?");

         builder.setView(view);

         Button dialogBtnApprove = view.findViewById(R.id.dialogBtnApprove);
         dialogBtnApprove.setText("Yes");
         Button dialogBtnDeny = view.findViewById(R.id.dialogBtnDeny);
         dialogBtnDeny.setText("No");

         AlertDialog dialog = builder.create();

         dialogBtnApprove.setOnClickListener(v -> {
             dialog.dismiss();
             cancelUserReservation(reservation);
         });

         dialogBtnDeny.setOnClickListener(v -> {
             dialog.dismiss();
         });

         Log.d("Dialog", "Showing dialog");
         dialog.show();
     }

     public void cancelUserReservation(Reservation reservation){
         Call<Void> call = reservationApi.changeReservationStatus(3,reservation);
         handleReservationResponse(call, "Reservation canceled successfully");}


     //------------------------- HANDLE CALLS
     private void handleReservationResponse(Call<Void> call, String successMessage) {
         call.enqueue(new Callback<Void>() {
             @Override
             public void onResponse(Call<Void> call, Response<Void> response) {
                 if (response.isSuccessful()) {
                     Toast.makeText(ManageMyReservations.this, successMessage, Toast.LENGTH_SHORT).show();
                     fetchUserReservations(guest.getEmail());
                 } else {
                     Toast.makeText(ManageMyReservations.this, "Failed to process a reservation! Cancel not successful!", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onFailure(Call<Void> call, Throwable t) {
                 Toast.makeText(ManageMyReservations.this, "Cancel failed!", Toast.LENGTH_SHORT).show();
             }
         });
     }
     private void handleGuestReservationsResponse(Call<List<Reservation>> call, String successMessage) {
         call.enqueue(new Callback<List<Reservation>>() {
             @Override
             public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                 if (response.isSuccessful()) {
                     Toast.makeText(ManageMyReservations.this, successMessage, Toast.LENGTH_SHORT).show();
                     updateAdapter(response);
                 } else {
                     Toast.makeText(ManageMyReservations.this, "Failed to fetch reservations!", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onFailure(Call<List<Reservation>> call, Throwable t) {
                 Toast.makeText(ManageMyReservations.this, "Couldn't fetch reservations!", Toast.LENGTH_SHORT).show();
             }
         });
     }

    public void updateAdapter(Response<List<Reservation>> response){
        reservationAdapter.clear();
        reservationAdapter.addAll(response.body());
        reservationAdapter.notifyDataSetChanged();
    }
}