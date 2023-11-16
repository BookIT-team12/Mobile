package com.example.bookit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

public class AccountDetails extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout home;
    private LinearLayout accountDetails;
    private LinearLayout favorites;
    private LinearLayout logout;

    private Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        favorites = findViewById(R.id.favorites);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        accountDetails = findViewById(R.id.account_details);
        home = findViewById(R.id.home);
        logout = findViewById(R.id.logout);
        editBtn = findViewById(R.id.edit_btn);

        favorites.setOnClickListener(v -> redirectActivity(AccountDetails.this, LoginScreen.class));

        menu.setOnClickListener(v -> openDrawer(drawerLayout));

        home.setOnClickListener(v -> redirectActivity(AccountDetails.this, HomeScreen.class));

        logout.setOnClickListener(v -> Toast.makeText(AccountDetails.this, "You have successfully logged out!", Toast.LENGTH_SHORT).show());

        accountDetails.setOnClickListener(v -> recreate());

        editBtn.setOnClickListener(v -> redirectActivity(AccountDetails.this, HomeScreen.class));
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
}