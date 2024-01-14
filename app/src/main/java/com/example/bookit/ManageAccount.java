package com.example.bookit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.example.bookit.model.User;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.UserApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageAccount extends AppCompatActivity {

    private EditText editUsername;
    private EditText editName;
    private EditText editSurname;
    private EditText editPhoneNumber;

    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout home;
    private LinearLayout accountDetails;
    private LinearLayout favorites;
    private LinearLayout logout;
    private Button editBtn;
    private Button changePasswordButton;

    private UserApi userApi;

    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        userApi = new RetrofitService(getApplicationContext()).getRetrofit().create(UserApi.class);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_VALUE")) {
            currentUser = (User) intent.getSerializableExtra("USER_VALUE");
        }

        navDrawerInitialization();
        fetchUserDetails();
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleSaveChanges();
            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangePasswordDialog();
            }
        });
    }

    private void fetchUserDetails() {
        editUsername.setText(currentUser.getEmail());
        editName.setText(currentUser.getName());
        editSurname.setText(currentUser.getLastName());
        editPhoneNumber.setText(currentUser.getPhone());
    }

    private void handleSaveChanges() {

        String username = editUsername.getText().toString();
        String name = editName.getText().toString();
        String surname = editSurname.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();

        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(ManageAccount.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;  // Stop further processing
        }
        if (!isValidString(surname)) {
            Toast.makeText(ManageAccount.this, "Invalid surname", Toast.LENGTH_SHORT).show();
            return;  // Stop further processing
        }
        if (!isValidString(username)) {
            Toast.makeText(ManageAccount.this, "Invalid username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidString(name)) {
            Toast.makeText(ManageAccount.this, "Invalid name", Toast.LENGTH_SHORT).show();
            return;  // Stop further processing
        }

        User updatedUser = new User();
        updatedUser.setEmail(username);
        updatedUser.setName(name);
        updatedUser.setLastName(surname);
        updatedUser.setPhone(phoneNumber);
        updatedUser.setPassword(currentUser.getPassword());
        // Call the updateUser API method
        updateUserDetails(updatedUser);

        Toast.makeText(ManageAccount.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void updateUserDetails(User updatedUser) {
        String loggedInUserEmail = updatedUser.getEmail();
        Call<String> call = userApi.updateUser(loggedInUserEmail, updatedUser);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String responseMessage = response.body();
                    Toast.makeText(ManageAccount.this, responseMessage, Toast.LENGTH_SHORT).show();
                    fetchUserDetails();

                } else {
                    Toast.makeText(ManageAccount.this, "Failed to update user details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ManageAccount.this, "Failed to update user details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isValidPhoneNumber(String phoneNumber) {

        return phoneNumber.matches("\\d{10,}");
    }

    private boolean isValidString(String username) {

        return !username.trim().isEmpty();
    }

    private void showChangePasswordDialog() {
        // Create a dialog with a custom layout
        final Dialog changePasswordDialog = new Dialog(this);
        changePasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changePasswordDialog.setContentView(R.layout.dialog_change_password);

        final EditText currentPasswordEditText = changePasswordDialog.findViewById(R.id.currentPasswordEditText);
        final EditText newPasswordEditText = changePasswordDialog.findViewById(R.id.newPasswordEditText);
        final EditText confirmPasswordEditText = changePasswordDialog.findViewById(R.id.confirmPasswordEditText);
        Button submitButton = changePasswordDialog.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = currentPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (newPassword.equals(confirmPassword)) {
                    currentUser.setPassword(newPassword);
                    Toast.makeText(ManageAccount.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    changePasswordDialog.dismiss();

                } else {
                    Toast.makeText(ManageAccount.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        changePasswordDialog.show();
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    private void redirectActivity(Activity activity, Class<?> secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    private void navDrawerInitialization(){
        //Nav drawer menu initialization
        favorites = findViewById(R.id.favorites);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        accountDetails = findViewById(R.id.account_details);
        home = findViewById(R.id.home);
        logout = findViewById(R.id.logout);

        //Btn initialization
        editBtn = findViewById(R.id.edit_btn);
        changePasswordButton = findViewById(R.id.change_password_btn);

        //EditText fields
        // Initialize EditText fields
        editUsername = findViewById(R.id.editUsername);
        editName = findViewById(R.id.editName);
        editSurname = findViewById(R.id.editSurname);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);

        editBtn = findViewById(R.id.edit_btn);
        changePasswordButton = findViewById(R.id.change_password_btn);

        //onClickListeners
        favorites.setOnClickListener(v -> redirectActivity(ManageAccount.this, LoginScreen.class));

        menu.setOnClickListener(v -> openDrawer(drawerLayout));

        home.setOnClickListener(v -> redirectActivity(ManageAccount.this, HomeScreen.class));

        logout.setOnClickListener(v -> Toast.makeText(ManageAccount.this, "You have successfully logged out!", Toast.LENGTH_SHORT).show());

        accountDetails.setOnClickListener(v -> recreate());
    }
}