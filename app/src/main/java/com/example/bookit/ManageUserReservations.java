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

import com.example.bookit.model.Accommodation;
import com.example.bookit.model.Reservation;
import com.example.bookit.model.Review;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.ReservationApi;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

//TODO: NAMESTI DA SE PROSLEDJUJE ID ULOGOVANOG OWNERA KAKO BI SE DOBAVILE NJEGOVE REZERVACIJE, KOJE MOZE DA ODBIJA/APPROVE-UJE
//TODO: Implementiraj klasu do kraja, nakon sto Uros zavrsi
public class ManageUserReservations extends AppCompatActivity {

    private ReservationApi reservationApi;
    private ListView reservationListView;

    private Retrofit retrofit;

    private ReservationAdapter reservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservations);

        retrofit = new RetrofitService(getApplicationContext()).getRetrofit();
        reservationApi = retrofit.create(ReservationApi.class);

        reservationAdapter = new ReservationAdapter(this, new ArrayList<>());

        reservationListView = findViewById(R.id.manageUserReservations);
        reservationListView.setAdapter(reservationAdapter);

        reservationListView.setOnItemClickListener((parent, view, position, id) -> {
            Reservation selectedReservation = reservationAdapter.getItem(position);
            showApprovalDialog(selectedReservation);
        });

        fetchExistingOwnersReservations();

    }

    private void showApprovalDialog(Reservation reservation) {
        Log.d("Dialog", "Preparing to show dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.approve_dialog, null);
        TextView dialogText=findViewById(R.id.dialogTextViewMessage);
        dialogText.setText("Do you want to approve or deny this reservation?");
        builder.setView(view);

        TextView dialogTextViewMessage = view.findViewById(R.id.dialogTextViewMessage);
        Button dialogBtnApprove = view.findViewById(R.id.dialogBtnApprove);
        Button dialogBtnDeny = view.findViewById(R.id.dialogBtnDeny);

        AlertDialog dialog = builder.create();

        dialogBtnApprove.setOnClickListener(v -> {
            dialog.dismiss();
            approveReservation(reservation.getId());
        });

        dialogBtnDeny.setOnClickListener(v -> {
            dialog.dismiss();
            denyReservation(reservation.getId());
        });
        Log.d("Dialog", "Showing dialog");
        dialog.show();
    }

    private void approveReservation(int reservationID) {
        Call<Void> call = reservationApi.approveReservation(reservationID);
        handleReservationResponse(call, "Reservation approved successfully");
    }

    private void denyReservation(int reservationID) {
        Call<Void> call = reservationApi.denyReservation(reservationID);
        handleReservationResponse(call, "Reservation denied successfully");
    }


    private void handleReservationResponse(Call<Void> call, String successMessage) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageUserReservations.this, successMessage, Toast.LENGTH_SHORT).show();
                    fetchExistingOwnersReservations();
                } else {
                    Toast.makeText(ManageUserReservations.this, "Failed to process a reservation", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageUserReservations.this, "Failed to process a reservation", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchExistingOwnersReservations(){}




}