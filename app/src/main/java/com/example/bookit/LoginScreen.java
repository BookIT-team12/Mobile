package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.databinding.ActivityLoginScreenBinding;
import com.example.bookit.model.User;
import com.example.bookit.model.UserCredentials;
import com.example.bookit.model.enums.Role;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.UserApi;
import com.example.bookit.security.UserTokenService;
import com.example.bookit.security.UserTokenState;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {

    RetrofitService retrofitService;
    UserApi api;
    EditText usernameTF;
    EditText passwordTF;
    UserTokenService tokenService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        usernameTF = findViewById(R.id.usernameTF);
        passwordTF = findViewById(R.id.passwordTF);
        retrofitService = new RetrofitService(getApplicationContext());
        api = retrofitService.getRetrofit().create(UserApi.class);
        tokenService = new UserTokenService(getApplicationContext());
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

    public void LogIn(View view) {
        String username = usernameTF.getText().toString();
        String password = passwordTF.getText().toString();
        UserCredentials toLogin = new UserCredentials();
        toLogin.setEmail(username);
        toLogin.setPassword(password);
        api.login(toLogin).enqueue(new Callback<UserTokenState>() {

            @Override
            public void onResponse(Call<UserTokenState> call, Response<UserTokenState> response) {
                UserTokenState jwtToken = response.body();
                if (jwtToken != null){
                    AppPreferences.saveToken(getApplicationContext(), jwtToken);
                    AppPreferences.debug_printAllSharedPreferences(getApplicationContext());
                    try {
                        Role loggedRole = tokenService.getRole(jwtToken);
                        Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                        intent.putExtra("USER_EMAIL", (JWTParser.parse(jwtToken.getAccessToken())).getJWTClaimsSet().getSubject());

                        switch (loggedRole) {
                            case OWNER:
                                intent.putExtra("ROLE", "owner");

                                startActivity(intent);
                                break;
                            case ADMINISTRATOR:
                                intent.putExtra("ROLE", "admin");
                                startActivity(intent);
                                break;
                            default:
                                intent.putExtra("ROLE", "guest");
                                startActivity(intent);
                        }
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    showSnackbar("Wrong credentials. Please try again we cant find you in database");
                }
            }

            @Override
            public void onFailure(Call<UserTokenState> call, Throwable t) {
                showSnackbar("ne radi");
                Logger.getLogger(RegisterScreen.class.getName()).log(Level.SEVERE, "Error occurred", t);
            }
        });

//        Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
//        intent.putExtra("ROLE", role);
//        startActivity(intent);
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
    public void Register(View view) {
        Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);
        startActivity(intent);
    }
}