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

import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.UserApi;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeScreen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout home;
    private LinearLayout manageAccount;
    private LinearLayout logout;
    private LinearLayout favorites;

    private LinearLayout deleteAccount;

    private String userId; //TODO: RESOLVE USER ID FETCH


    private LinearLayout addAccommodation;

    private LinearLayout blockUsers;
    private LinearLayout approveAccommodations;

    private Retrofit retrofit;

    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        String role = getIntent().getStringExtra("ROLE");

        retrofit=new RetrofitService().getRetrofit();
        userApi = new RetrofitService().getRetrofit().create(UserApi.class);



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
        deleteAccount=findViewById(R.id.deleteAccount);


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
                Toast.makeText(HomeScreen.this, "You have successfully logged out!", Toast.LENGTH_SHORT).show();
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

        blockUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(HomeScreen.this, AdminUserBlockingActivity.class);
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
        Call<Map<String, String>> call = userApi.deleteUser(userId);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {

                    Map<String, String> responseBody = response.body();
                    if (responseBody != null && responseBody.containsKey("message")) {
                        String successMessage = responseBody.get("message");
                        Toast.makeText(HomeScreen.this, successMessage, Toast.LENGTH_SHORT).show();
                        //TODO: PREBACI USERA NA LOGIN SCREEN, NAKON OBRISANIH KREDENCIJALA
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