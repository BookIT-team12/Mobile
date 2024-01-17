 package com.example.bookit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookit.model.Reservation;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReservationApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

 public class ManageMyReservations extends AppCompatActivity {


     private ReservationApi reservationApi;
     private ListView reservationListView;

     private Retrofit retrofit;

     private ReservationAdapter reservationAdapter;

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
    }


    private void fetchUserReservations(){}
    public void showCancelDialog(Reservation reservation){
        Log.d("Dialog", "Preparing to show dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.approve_dialog, null);
        TextView dialogText=findViewById(R.id.dialogTextViewMessage);
        dialogText.setText("Are you sure you want to cancel this reservation?");

        builder.setView(view);

        TextView dialogTextViewMessage = view.findViewById(R.id.dialogTextViewMessage);
        Button dialogBtnApprove = view.findViewById(R.id.dialogBtnApprove);
        dialogBtnApprove.setText("Yes");
        Button dialogBtnDeny = view.findViewById(R.id.dialogBtnDeny);
        dialogBtnDeny.setText("No");

        AlertDialog dialog = builder.create();

        dialogBtnApprove.setOnClickListener(v -> {

            dialog.dismiss();
            CancelUserReservation(reservation);
        });

        dialogBtnDeny.setOnClickListener(v -> {

            dialog.dismiss();
        });
        Log.d("Dialog", "Showing dialog");
        dialog.show();
    }

     public void CancelUserReservation(Reservation reservation){
         Call<Void> call = reservationApi.cancelReservation(reservation.getId());
         handleReservationResponse(call, "Reservation canceled successfully");}


     private void handleReservationResponse(Call<Void> call, String successMessage) {
         call.enqueue(new Callback<Void>() {
             @Override
             public void onResponse(Call<Void> call, Response<Void> response) {
                 if (response.isSuccessful()) {
                     Toast.makeText(ManageMyReservations.this, successMessage, Toast.LENGTH_SHORT).show();
                     fetchUserReservations();
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
}