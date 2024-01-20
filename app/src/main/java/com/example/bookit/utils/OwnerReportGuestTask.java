package com.example.bookit.utils;

import android.os.AsyncTask;

import com.example.bookit.model.User;
import com.example.bookit.retrofit.api.UserApi;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Response;

public class OwnerReportGuestTask extends AsyncTask<String, Void, String> {
    private UserApi userApi;
    private final OwnerReportGuestAdapter.ViewHolder holder;
    private final User user;

    public OwnerReportGuestTask(UserApi userApi, OwnerReportGuestAdapter.ViewHolder holder, User user) {
        this.userApi = userApi;
        this.holder = holder;
        this.user = user;
    }

    @Override
    protected String doInBackground(String... params) {
        // Perform the background operation, e.g., making the network call
        try {
            Response<Void> response = userApi.reportUser(user.getEmail()).execute();
            if (response.isSuccessful()) {
                return "went okay";
            } else {
                // Handle unsuccessful response
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            // Handle the successful response
            Snackbar.make(holder.itemView, "You have successfully reported user", Snackbar.LENGTH_LONG).show();
            holder.submitReportButton.setEnabled(false);
            holder.submitReportButton.setText("Already reported");
        } else {
            // Handle the case where the report couldn't be posted
            System.out.println("You couldn't post the report, even though you entered all necessary data");
        }
    }
}

