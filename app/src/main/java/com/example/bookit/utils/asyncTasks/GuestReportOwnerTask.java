package com.example.bookit.utils.asyncTasks;

import android.os.AsyncTask;

import com.example.bookit.model.User;
import com.example.bookit.retrofit.api.UserApi;
import com.example.bookit.utils.adapters.GuestReportOwnerAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Response;

public class GuestReportOwnerTask extends AsyncTask<String, Void, String> {
    private final GuestReportOwnerAdapter.ViewHolder holder;
    private final UserApi userApi;
    private final User user;

    public GuestReportOwnerTask(GuestReportOwnerAdapter.ViewHolder holder, UserApi userApi, User user) {
        this.holder = holder;
        this.userApi = userApi;
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
        // This method is executed on the UI thread after doInBackground finishes
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
