package com.example.bookit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.security.UserTokenService;

public class HomeScreen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout home;
    private LinearLayout manageAccount;
    private LinearLayout logout;
    private LinearLayout favorites;

    private LinearLayout addAccommodation;

    private LinearLayout blockUsers;
    private LinearLayout approveAccommodations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        String role = getIntent().getStringExtra("ROLE");

        if ("admin".equals(role)) {

            includeNavDrawer(R.layout.nav_drawer_admin);
            manageAccount = findViewById(R.id.account_details_admin);
            home = findViewById(R.id.home_admin);
            logout = findViewById(R.id.logout_admin);
            approveAccommodations=findViewById(R.id.manage_apartments);
            blockUsers=findViewById(R.id.manage_accounts);


        }
        else if ("owner".equals(role)) {
            includeNavDrawer(R.layout.nav_drawer_host);
            manageAccount = findViewById(R.id.account_details_host);
            home = findViewById(R.id.home_host);
            logout = findViewById(R.id.logout_host);
            addAccommodation=findViewById(R.id.add_accommodation);

        }
        else {
            includeNavDrawer(R.layout.nav_drawer_guest);
            logout = findViewById(R.id.logout);
            manageAccount = findViewById(R.id.account_details);
            home = findViewById(R.id.home);
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreferences.deleteToken(getApplicationContext());
                redirectActivity(HomeScreen.this, LoginScreen.class);
            }

        });

        manageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(HomeScreen.this, AccountDetails.class);
            }
        });

//        addAccommodation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                redirectActivity(HomeScreen.this, AddAccommodation.class);
//            }
//        });

        approveAccommodations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(HomeScreen.this, AccommodationApprovalActivity.class);
            }
        });

//        blockUsers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                redirectActivity(HomeScreen.this, AdminUserBlockingActivity.class);
//            }
//        });


    }

    private void includeNavDrawer(int layoutResId) {
        // Include the navigation drawer layout
        RelativeLayout navDrawer = findViewById(R.id.navDrawer);
        navDrawer.removeAllViews(); // Clear existing views
        View customNavDrawer = getLayoutInflater().inflate(layoutResId, navDrawer, false);
        navDrawer.addView(customNavDrawer);
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}