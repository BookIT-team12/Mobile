package com.example.bookit.utils.asyncTasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.example.bookit.HomeScreen;
import com.example.bookit.model.Notification;
import com.example.bookit.retrofit.api.NotificationApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostNotificationTask extends AsyncTask<Notification, Void, Void> {

    private NotificationApi notificationApi;
    private View view;

    public PostNotificationTask(NotificationApi notificationApi, View mainView) {
        this.notificationApi = notificationApi;
        this.view = mainView;
    }

    @Override
    protected Void doInBackground(Notification... notifications) {
        if (notifications.length > 0) {
            Notification ntf = notifications[0];
            Call<Notification> call = notificationApi.createNotification(ntf);

            call.enqueue(new Callback<Notification>() {
                @Override
                public void onResponse(Call<Notification> call, Response<Notification> response) {
                    System.out.println("PROSLO JE");
                }

                @Override
                public void onFailure(Call<Notification> call, Throwable t) {
                    System.out.println("NIJE PROSLO");
                }
            });
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // Start HomeScreenActivity
        Intent intent = new Intent(view.getContext(), HomeScreen.class);
        intent.putExtra("ROLE", "guest");
        view.getContext().startActivity(intent);
    }
}
