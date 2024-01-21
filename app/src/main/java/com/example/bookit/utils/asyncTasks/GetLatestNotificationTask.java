package com.example.bookit.utils.asyncTasks;

import android.os.AsyncTask;

import com.example.bookit.model.Notification;
import com.example.bookit.model.User;
import com.example.bookit.retrofit.api.NotificationApi;

import java.io.IOException;

import retrofit2.Response;

public class GetLatestNotificationTask extends AsyncTask<String, Void, Notification> {

    private NotificationApi notificationApi;
    private GetLatestNotificationCallback callback;
    private User currentUser;

    public GetLatestNotificationTask(NotificationApi notificationApi, GetLatestNotificationCallback callback, User currentUser) {
        this.notificationApi = notificationApi;
        this.callback = callback;
        this.currentUser = currentUser;
    }

    @Override
    protected Notification doInBackground(String... params) {
        try {
            // params[0] should contain the current user's email
            String currentUserEmail = currentUser.getEmail();

            // Make the API request
            Response<Notification> response = notificationApi.getLatestsNotification(currentUserEmail).execute();

            // Check if the request was successful
            if (response.isSuccessful()) {
                return response.body();
            } else {
                System.out.println("LOSE DOVLACENJE NOTIFIKACIJA");
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Notification notification) {
        if (notification != null) {
            callback.onSuccess(notification);
        } else {
            callback.onFailure();
        }
    }

    public interface GetLatestNotificationCallback {
        void onSuccess(Notification notification);

        void onFailure();
    }
}
