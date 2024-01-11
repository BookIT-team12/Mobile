package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.material.snackbar.Snackbar;

public class RegisterScreen extends AppCompatActivity {
    EditText usernameEditText = findViewById(R.id.RegFormUsernameTF);
    EditText nameEditText = findViewById(R.id.RegFormNameTF);
    EditText lastnameEditText = findViewById(R.id.RegFormLastnameTF);
    EditText passwordEditText = findViewById(R.id.RegFormPasswordTF);
    EditText confirmPasswordEditText = findViewById(R.id.RegFormConfirmPasswordTF);
    EditText phoneEditText = findViewById(R.id.RegFormPhoneTF);
    EditText addressEditText = findViewById(R.id.RegFormAddressTF);
    RadioGroup roleRadioGroup = findViewById(R.id.roleRadioGroup);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
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
        String errCode = formValidation(username, name, lastname, password, confirmPassword, phone, address);
        if (!errCode.equals("NO_ERR")){
            handleErrors(errCode);
            return;
        }

        switch (selectedRadioButtonId){
            case 0:
                break;
            case 1:
                break;
        }


        Intent intent = new Intent(RegisterScreen.this, LoginScreen.class);
        startActivity(intent);
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
