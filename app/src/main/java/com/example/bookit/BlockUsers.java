package com.example.bookit;

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

        Button btnBlock = findViewById(R.id.btnBlock);
        btnBlock.setOnClickListener(v -> {
            // Handle blocking user here
            int selectedPosition = userListView.getCheckedItemPosition();
            if (selectedPosition != ListView.INVALID_POSITION) {
                User selectedUser = (User) userListView.getItemAtPosition(selectedPosition);
                blockUser(selectedUser.getEmail());
            } else {
                Toast.makeText(BlockUsers.this, "Please select a user to block", Toast.LENGTH_SHORT).show();
            }
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
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, users);
        userListView.setAdapter(adapter);
        userListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void blockUser(String userId) {
        Call<String> call = userApi.blockUser(userId);
        handleUserAPIResponse(call, "User blocked successfully");
    }

    private void handleUserAPIResponse(Call<String> call, String successMessage) {
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String responseMessage = response.body();
                    Toast.makeText(BlockUsers.this, responseMessage, Toast.LENGTH_SHORT).show();
                    fetchUsersForBlocking();
                } else {
                    Toast.makeText(BlockUsers.this, "Failed to block user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(BlockUsers.this, "Failed to block user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
