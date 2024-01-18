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

import com.example.bookit.model.User;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.UserApi;

import org.mapsforge.map.rendertheme.renderinstruction.Line;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.security.UserTokenService;

public class HomeScreen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout home;
    private LinearLayout manageAccount;
    private LinearLayout logout;
    private LinearLayout favorites;

    private LinearLayout deleteAccount;

    private LinearLayout approveReviews;

    private LinearLayout addAccommodation;

    private LinearLayout manageGuestReservations;

    private LinearLayout manageMyReservations;

    private LinearLayout blockUsers;
    private LinearLayout approveAccommodations;
    private LinearLayout manageAccommodations;


    private Retrofit retrofit;

    private UserApi userApi;

    private User currentUser;

    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        String role = getIntent().getStringExtra("ROLE");

        setupRetrofit();
        currentUserEmail=getIntent().getStringExtra("USER_EMAIL");
        getUserData(currentUserEmail);

        setUpCommonUI();
        if ("admin".equals(role)) {
            setUpAdminUI();
        }
        else if ("owner".equals(role)) {
            setUpHostUI();
        }
        else {
            setUpGuestUI();
        }
    }

    private void setupRetrofit() {
        retrofit = new RetrofitService(getApplicationContext()).getRetrofit();
        userApi = retrofit.create(UserApi.class);
    }

    public void getUserData(String currentUserEmail){
        userApi.getUser(currentUserEmail).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                currentUser = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                currentUser = null;
            }
        });
    }

    public void setUpAdminUI(){
        includeNavDrawer(R.layout.nav_drawer_admin);
        manageAccount = findViewById(R.id.account_details_admin);
        home = findViewById(R.id.home_admin);
        logout = findViewById(R.id.logout_admin);
        approveAccommodations=findViewById(R.id.manage_apartments);
        blockUsers=findViewById(R.id.manage_accounts);
        approveReviews=findViewById(R.id.manage_reviews);


        approveAccommodations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(HomeScreen.this, AccommodationApprovalActivity.class);
            }
        });

        approveReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(HomeScreen.this, ApproveReviews.class);
            }
        });

        blockUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(HomeScreen.this, BlockUsers.class);
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
                Intent intent = new Intent(HomeScreen.this, ManageAccount.class);
                intent.putExtra("USER_VALUE", currentUser);
                startActivity(intent);
            }
        });

    }

    public void setUpGuestUI(){
        includeNavDrawer(R.layout.nav_drawer_guest);
        logout = findViewById(R.id.logout);
        manageAccount = findViewById(R.id.account_details);
        home = findViewById(R.id.home);
        manageMyReservations=findViewById(R.id.manage_my_reservations);

        manageMyReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ManageMyReservations.class);
                intent.putExtra("USER_VALUE", currentUser);
                startActivity(intent);
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
                Intent intent = new Intent(HomeScreen.this, ManageAccount.class);
                intent.putExtra("USER_VALUE", currentUser);
                startActivity(intent);
            }
        });

    }

    public void setUpHostUI(){
        includeNavDrawer(R.layout.nav_drawer_host);
        manageAccount = findViewById(R.id.account_details_host);
        home = findViewById(R.id.home_host);
        logout = findViewById(R.id.logout_host);
        addAccommodation=findViewById(R.id.add_accommodation);
        manageAccommodations=findViewById(R.id.manage_my_apartments);
        manageGuestReservations=findViewById(R.id.manage_reservations);


        manageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ManageAccount.class);
                intent.putExtra("USER_VALUE", currentUser);
                startActivity(intent);
            }
        });


        manageAccommodations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, UpdateAccommodation.class);
                intent.putExtra("USER_VALUE", currentUser);
                startActivity(intent);
            }
        });

        manageGuestReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ManageUserReservations.class);
                intent.putExtra("USER_VALUE", currentUser);
                startActivity(intent);
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

    }

    public void setUpCommonUI(){
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        deleteAccount=findViewById(R.id.deleteAccount);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    public void deleteAccount() {
        Call<Map<String, String>> call = userApi.deleteUser(currentUserEmail);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {

                    Map<String, String> responseBody = response.body();
                    if (responseBody != null && responseBody.containsKey("message")) {
                        String successMessage = responseBody.get("message");
                        Toast.makeText(HomeScreen.this, successMessage, Toast.LENGTH_SHORT).show();
                        redirectActivity(HomeScreen.this, LoginScreen.class);
                        Toast.makeText(HomeScreen.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(HomeScreen.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeScreen.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(HomeScreen.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
            }
        });
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