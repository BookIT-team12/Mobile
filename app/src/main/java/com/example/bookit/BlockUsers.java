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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockUsers extends AppCompatActivity {

    private UserApi userApi;
    private ListView userListView;
    private TextView textViewMessage;

    private User blockedUser;

    private ArrayAdapter<User> arrayAdapter;
    private List<User> usersForBlocking;
    private BlockUsersAdapter blockUsersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_blocking);

        userApi = new RetrofitService(getApplicationContext()).getRetrofit().create(UserApi.class);

        userListView = findViewById(R.id.userListView);
        textViewMessage = findViewById(R.id.textViewMessageBlock);


        // Initialize the adapter
        blockUsersAdapter = new BlockUsersAdapter(this, new ArrayList<>());
        userListView.setAdapter(blockUsersAdapter);

        fetchUsersForBlocking();

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = (User) userListView.getItemAtPosition(position);
            blockedUser=selectedUser;
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

                    List<User> nonBlockedUsers = filterBlockedUsers(users);

                    if (nonBlockedUsers.isEmpty()) {
                        textViewMessage.setText("You have 0 users for blocking.");
                    } else {
                        textViewMessage.setText("Users for blocking");
                    }

                    if (blockUsersAdapter == null) {
                        blockUsersAdapter = new BlockUsersAdapter(BlockUsers.this, nonBlockedUsers);
                        userListView.setAdapter(blockUsersAdapter);
                    } else {
                        blockUsersAdapter.updateData(nonBlockedUsers);
                    }
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


    private List<User> filterBlockedUsers(List<User> users) {
        List<User> nonBlockedUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.isBlocked()) {
                nonBlockedUsers.add(user);
            }
        }
        return nonBlockedUsers;
    }

    private void displayUsersForBlocking(List<User> users) {
        if (users != null) {
            blockUsersAdapter.updateData(users);
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
        Call<Void> call = userApi.blockUser(userId);
        handleUserAPIResponse(call, "User blocked successfully");
    }


    private void handleUserAPIResponse(Call<Void> call, String successMessage) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    int statusCode = response.code();
                    if (statusCode == 200) {
                        String responseMessage = "User blocked successfully";
                        Toast.makeText(BlockUsers.this, responseMessage, Toast.LENGTH_SHORT).show();
                        blockedUser.setBlocked(true);
                        fetchUsersForBlocking();

                    } else {
                        Toast.makeText(BlockUsers.this, "Unexpected response code: " + statusCode, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BlockUsers.this, "User can't be blocked!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(BlockUsers.this, "Failed to block user", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
