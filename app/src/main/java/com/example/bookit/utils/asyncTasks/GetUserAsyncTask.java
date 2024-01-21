package com.example.bookit.utils.asyncTasks;

import android.os.AsyncTask;

import com.example.bookit.model.User;
import com.example.bookit.retrofit.api.UserApi;

import retrofit2.Call;
import retrofit2.Response;

public class GetUserAsyncTask extends AsyncTask<String, Void, User> {

    private UserApi userApi;
    private GetUserCallback callback;

    public GetUserAsyncTask(UserApi userApi, GetUserCallback callback) {
        this.userApi = userApi;
        this.callback = callback;
    }

    @Override
    protected User doInBackground(String... params) {
        try {
            // params[0] should contain the current user's email
            String currentUserEmail = params[0];

            // Make the API request
            Response<User> response = userApi.getUser(currentUserEmail).execute();

            // Check if the request was successful
            if (response.isSuccessful()) {
                return response.body();
            } else {
                // Handle error
                return null;
            }
        } catch (Exception e) {
            // Handle network error
            return null;
        }
    }

    @Override
    protected void onPostExecute(User user) {
        if (user != null) {
            // Handle successful response
            callback.onSuccess(user);
        } else {
            // Handle failure
            callback.onFailure();
        }
    }

    public interface GetUserCallback {
        void onSuccess(User user);

        void onFailure();
    }
}
