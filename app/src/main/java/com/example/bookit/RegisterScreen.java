package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.bookit.model.User;
import com.example.bookit.model.UserCredentials;
import com.example.bookit.model.enums.Role;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.UserApi;
import com.google.android.material.snackbar.Snackbar;

import java.util.logging.Level;
import java.util.logging.Logger;

import kotlinx.coroutines.MainCoroutineDispatcher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterScreen extends AppCompatActivity {
    EditText usernameEditText;
    EditText nameEditText;
    EditText lastnameEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    EditText phoneEditText;
    EditText addressEditText;
    RadioGroup roleRadioGroup;
    RetrofitService retrofitService;
    UserApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        usernameEditText =  findViewById(R.id.RegFormUsernameTF);
        nameEditText = findViewById(R.id.RegFormNameTF);
        lastnameEditText = findViewById(R.id.RegFormLastnameTF);
        passwordEditText  = findViewById(R.id.RegFormPasswordTF);
        confirmPasswordEditText = findViewById(R.id.RegFormConfirmPasswordTF);
        phoneEditText = findViewById(R.id.RegFormPhoneTF);
        addressEditText = findViewById(R.id.RegFormAddressTF);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        retrofitService = new RetrofitService(getApplicationContext());
        api = retrofitService.getRetrofit().create(UserApi.class);
    }


    public void ConfirmRegistration(View view) {
        // Retrieve text from the EditText fields
        String username = usernameEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String lastname = lastnameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();

        int selectedRadioButtonId = roleRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String selectedRole = selectedRadioButton.getText().toString();

        String errCode = formValidation(username, name, lastname, password, confirmPassword, phone, address);
        if (!errCode.equals("NO_ERR")){
            handleErrors(errCode);
            return;
        }

        User toRegister;
        switch (selectedRole){
            case "Guest":
                toRegister = new User(name, lastname, username, password, confirmPassword, address, phone, Role.GUEST, false, false);
                api.register(toRegister);
                break;
            case "Owner":
                toRegister = new User(name, lastname, username, password, confirmPassword, address, phone, Role.OWNER, false, false);
                api.register(toRegister).enqueue(new Callback<UserCredentials>() {
                    @Override
                    public void onResponse(Call<UserCredentials> call, Response<UserCredentials> response) {
                        showSnackbar("Uspesno kreirao");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // This code will run after the delay
                                Intent intent = new Intent(RegisterScreen.this, LoginScreen.class);
                                startActivity(intent);
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(Call<UserCredentials> call, Throwable t) {
                        showSnackbar("You cant register right now with these informations");
                        Logger.getLogger(RegisterScreen.class.getName()).log(Level.SEVERE, "Error occurred", t);
                    }
                });
                break;
        }


    }

    public String formValidation(String username, String name, String lastname, String password,
                                 String confirmPassword, String phone, String address){

        if (username.trim().equals("") || name.trim().equals("")||lastname.trim().equals("")||password.trim().equals("")
                ||confirmPassword.trim().equals("")||phone.trim().equals("")||address.trim().equals("")){
            return "EMPTY_FIELD_ERR";
        }
        if (!phone.matches("[0-9]+")){
            return "PHONE_NUMBERS_ERR";
        }
        if (phone.length() != 10){
            return "PHONE_LENGTH_ERR";
        }
        if (!password.equals(confirmPassword)){
            return "PASSWORDS_NOT_MATCH_ERR";
        }
        return "NO_ERR";
    }

    public void handleErrors(String errorCode){
        switch (errorCode){
            case "EMPTY_FIELD_ERR":
                showSnackbar("All fields must be filled");
                break;
            case "PHONE_NUMBERS_ERR":
                showSnackbar("Phone field must contain only numbers");
                break;
            case "PASSWORDS_NOT_MATCH_ERR":
                showSnackbar("Passwords do not match");
                break;
            case "PHONE_LENGTH_ERR":
                showSnackbar("Phone must contain exactly 10 digits");
                break;
        }
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
