package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.bookit.databinding.ActivityLoginScreenBinding;

public class LoginScreen extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
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
        String role = checkCredentials();
        if (role == null){
            Toast.makeText(getApplicationContext(), "Wrong credentials! We don't know your role!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
        intent.putExtra("ROLE", role);
        startActivity(intent);
    }

    private String checkCredentials(){
        String username = binding.usernameTF.getText().toString();

        if (username.equals("owner") || username.equals("admin") || username.equals("guest")){
            return username;
        } else {
            return null;
        }

    }

    public void Register(View view) {
        Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);
        startActivity(intent);
    }
}