package com.example.bookit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookit.model.User;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.UserApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockUsers extends AppCompatActivity {

    private UserApi userApi;
    private ListView userListView;
    private TextView textViewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_blocking);

        userApi = new RetrofitService(getApplicationContext()).getRetrofit().create(UserApi.class);

        userListView = findViewById(R.id.userListView);
        textViewMessage = findViewById(R.id.textViewMessageBlock);

        fetchUsersForBlocking();

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = (User) userListView.getItemAtPosition(position);
            showBlockUserConfirmationDialog(selectedUser.getEmail());
        });

    }

    private void fetchUsersForBlocking() {
        Call<List<User>> call = userApi.getUsersForBlocking();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    displayUsersForBlocking(users);
                } else {
                    Toast.makeText(BlockUsers.this, "Failed to fetch users for blocking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(BlockUsers.this, "Failed to fetch users for blocking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUsersForBlocking(List<User> users) {
        if (users != null) {
            ArrayAdapter<User> adapter = new BlockUsersAdapter(this, users);
            userListView.setAdapter(adapter);
        } else {
            Toast.makeText(BlockUsers.this, "No users available for blocking", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBlockUserConfirmationDialog(String userEmail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("Block User");

        builder.setMessage("Are you sure you want to block this user?");
        builder.setPositiveButton("Yes", (dialog, which) -> blockUser(userEmail));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void blockUser(String userId) {
        Call<String> call = userApi.blockUser(userId);
        handleUserAPIResponse(call, "User blocked successfully");
    }

    //TODO: RESI ZASTO BACA BLOCK FAILED, STATUS-->200, BAZA UPDATED?
    private void handleUserAPIResponse(Call<String> call, String successMessage) {
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String responseMessage;
                    if (response.body() != null && !response.body().isEmpty()) {
                        responseMessage = response.body();
                    } else {
                        responseMessage = successMessage;
                    }

                    Toast.makeText(BlockUsers.this, responseMessage, Toast.LENGTH_SHORT).show();

                    runOnUiThread(() -> fetchUsersForBlocking());
                } else {
                    Toast.makeText(BlockUsers.this, "User can't be blocked!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(BlockUsers.this, "Failed to block user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
