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

import com.example.bookit.model.Accommodation;
import com.example.bookit.model.Notification;
import com.example.bookit.model.Reservation;
import com.example.bookit.model.Review;
import com.example.bookit.model.User;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.NotificationApi;
import com.example.bookit.retrofit.api.ReservationApi;
import com.example.bookit.utils.asyncTasks.PostNotificationTask;

import org.w3c.dom.Text;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ManageUserReservations extends AppCompatActivity {

    private ReservationApi reservationApi;
    private NotificationApi notificationApi;
    private ListView reservationListView;

    private Retrofit retrofit;

    private ReservationAdapter reservationAdapter;

    private User owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservations);

        retrofit = new RetrofitService(getApplicationContext()).getRetrofit();
        reservationApi = retrofit.create(ReservationApi.class);

        reservationAdapter = new ReservationAdapter(this, new ArrayList<>());

        reservationListView = findViewById(R.id.manageUserReservations);
        reservationListView.setAdapter(reservationAdapter);

        notificationApi=retrofit.create(NotificationApi.class);

        reservationListView.setOnItemClickListener((parent, view, position, id) -> {
            Reservation selectedReservation = reservationAdapter.getItem(position);
            showApprovalDialog(selectedReservation);
        });


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_VALUE")) {
            owner = (User) intent.getSerializableExtra("USER_VALUE");
        }

        fetchExistingOwnersReservations(owner.getEmail());

    }

    private void showApprovalDialog(Reservation reservation) {
        Log.d("Dialog", "Preparing to show dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.approve_dialog, null);

        // Inflate layout before finding views
        builder.setView(view);

        TextView dialogTextViewMessage = view.findViewById(R.id.dialogTextViewMessage);
        dialogTextViewMessage.setText("Do you want to approve or deny this reservation?");

        Button dialogBtnApprove = view.findViewById(R.id.dialogBtnApprove);
        Button dialogBtnDeny = view.findViewById(R.id.dialogBtnDeny);

        AlertDialog dialog = builder.create();

        dialogBtnApprove.setOnClickListener(v -> {
            dialog.dismiss();
            approveReservation(reservation);
        });

        dialogBtnDeny.setOnClickListener(v -> {
            dialog.dismiss();
            denyReservation(reservation);
        });

        Log.d("Dialog", "Showing dialog");
        dialog.show();
    }

    private void approveReservation(Reservation reservation) {
        Call<Void> call = reservationApi.changeReservationStatus(2,reservation);
        String notificationMessage="Reservation APPROVED - "+ reservation.getGuestEmail()+ " enjoy your stay!";
        handleReservationResponse(call, "Reservation approved successfully", notificationMessage, reservation.getGuestEmail());

        Notification notification=new Notification(null, reservation.getGuestEmail(),notificationMessage, LocalDateTime.now());
        View view=findViewById(android.R.id.content);
        PostNotificationTask asyncPostTask = new PostNotificationTask(notificationApi, view);
        asyncPostTask.execute(notification);


    }

    private void denyReservation(Reservation reservation) {
        Call<Void> call = reservationApi.changeReservationStatus(1,reservation);
        String notificationMessage="Reservation DENIED - "+ reservation.getGuestEmail()+ " we'll see each other next time!";
        handleReservationResponse(call, "Reservation denied successfully", notificationMessage, reservation.getGuestEmail());

        Notification notification=new Notification(null, reservation.getGuestEmail(),notificationMessage,  LocalDateTime.now());
        View view=findViewById(android.R.id.content);
        PostNotificationTask asyncPostTask = new PostNotificationTask(notificationApi, view);
        asyncPostTask.execute(notification);
    }

    private void handleReservationResponse(Call<Void> call, String successMessage, String notificationMessage, String guestID) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageUserReservations.this, successMessage, Toast.LENGTH_SHORT).show();
                    fetchExistingOwnersReservations(owner.getEmail());
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

    private void handleUserReservations(Call<List<Reservation>> call, String successMessage) {
        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful()) {
                    updateAdapter(response);
                    Toast.makeText(ManageUserReservations.this, successMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageUserReservations.this, "Failed to fetch reservations!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Toast.makeText(ManageUserReservations.this, "Couldn't fetch reservations!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateAdapter(Response<List<Reservation>> response){
        reservationAdapter.clear();
        reservationAdapter.addAll(response.body());
        reservationAdapter.notifyDataSetChanged();
    }


    private void fetchExistingOwnersReservations(String ownerEmail){
        Call<List<Reservation>> call=reservationApi.getOwnerReservations(ownerEmail);
        handleUserReservations(call, "Successfully fetched user reservations!");
    }


    public void saveNotification(Notification notification){
        Call<Notification> call=notificationApi.createNotification(notification);
        handleNotificationResponse(call, "Notification sent successfully!");
    }
    private void handleNotificationResponse(Call<Notification> call, String successMessage) {
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageUserReservations.this, successMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("NotificationRequest", "Request payload: " + call.request().body().toString());
                    Toast.makeText(ManageUserReservations.this, "Failed to create Notification!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.e("NotificationRequest", "Request payload: " + call.request().body().toString());
                Toast.makeText(ManageUserReservations.this, "Notification fail! :(", Toast.LENGTH_SHORT).show();
            }
        });
    }



}