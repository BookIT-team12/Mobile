package com.example.bookit;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bookit.model.Notification;
import com.example.bookit.model.User;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.NotificationApi;
import com.example.bookit.retrofit.api.UserApi;

import org.mapsforge.map.rendertheme.renderinstruction.Line;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.security.UserTokenService;
import com.example.bookit.utils.NotificationUtils;
import com.example.bookit.utils.asyncTasks.GetLatestNotificationTask;
import com.example.bookit.utils.asyncTasks.GetUserAsyncTask;

import org.mapsforge.map.rendertheme.renderinstruction.Line;

public class HomeScreen extends AppCompatActivity {

    private static final String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    private static final int PERMISSION_REQUEST_POST_NOTIFICATIONS = 123; // You can use any unique value
    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout home;
    private LinearLayout manageAccount;
    private LinearLayout logout;
    private LinearLayout favorites;
    private LinearLayout placesVisited;

    private LinearLayout deleteAccount;

    private LinearLayout approveReviews;

    private LinearLayout addAccommodation;
    private LinearLayout reportReviewsAccommodation;
    private LinearLayout reportReviewsOwner;
    private LinearLayout ownerReportUser;
    private LinearLayout userReportOwner;

    private LinearLayout manageGuestReservations;

    private LinearLayout manageMyReservations;

    private LinearLayout blockUsers;
    private LinearLayout approveAccommodations;
    private LinearLayout manageAccommodations;


    private Retrofit retrofit;

    private UserApi userApi;
    private NotificationApi notificationApi;

    private User currentUser;

    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        String role = getIntent().getStringExtra("ROLE");

        setupRetrofit();
        currentUserEmail = getIntent().getStringExtra("USER_EMAIL");
        getUserData(currentUserEmail);

//        new GetLatestNotificationTask(notificationApi, new GetLatestNotificationTask.GetLatestNotificationCallback() {
//            @Override
//            public void onSuccess(Notification notification) {
//                NotificationUtils.notifyPhone(getApplicationContext(), notification, "TITLE OF NOTIFICATION");
//            }
//
//            @Override
//            public void onFailure() {
//                System.out.println("LOSE PAO ZAHTEV ZA NTF");
//            }
//        }, currentUser).execute();



        setUpCommonUI();
        if ("admin".equals(role)) {
            setUpAdminUI();
        }
        else if ("owner".equals(role)) {
            setUpHostUI();
            fetchNotifications();
        } else {
            setUpGuestUI();
            fetchNotifications();
        }
    }

    private void setupRetrofit() {
        retrofit = new RetrofitService(getApplicationContext()).getRetrofit();
        userApi = retrofit.create(UserApi.class);
        notificationApi = retrofit.create(NotificationApi.class);
    }

    public void getUserData(String currentUserEmail){
        new GetUserAsyncTask(userApi, new GetUserAsyncTask.GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                // Handle successful response here
                currentUser = user;
            }

            @Override
            public void onFailure() {
                // Handle failure here
                currentUser = null;
            }
        }).execute(currentUserEmail);

    }

    public void setUpAdminUI() {
        includeNavDrawer(R.layout.nav_drawer_admin);
        manageAccount = findViewById(R.id.account_details_admin);
        home = findViewById(R.id.home_admin);
        logout = findViewById(R.id.logout_admin);
        approveAccommodations = findViewById(R.id.manage_apartments);
        blockUsers = findViewById(R.id.manage_accounts);
        approveReviews = findViewById(R.id.manage_reviews);


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
        placesVisited = findViewById(R.id.user_reviews);
        userReportOwner = findViewById(R.id.user_report_owner);


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

        placesVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, PlacesVisitedActivity.class);
                startActivity(intent);
            }
        });

        userReportOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, GuestReportOwner.class);
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
        reportReviewsAccommodation = findViewById(R.id.manage_user_comments_on_owner_accommodations);
        reportReviewsOwner = findViewById(R.id.manage_user_comments_on_owner);
        ownerReportUser = findViewById(R.id.owner_report_user);

        reportReviewsAccommodation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, OwnerReportReviewAccommodation.class);
                startActivity(intent);
            }
        });
        reportReviewsOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, OwnerReportReviewOwner.class);
                startActivity(intent);
            }
        });
        ownerReportUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, OwnerReportGuest.class);
                startActivity(intent);
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


        manageAccommodations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ManageAccommodations.class);
                intent.putExtra("USER_VALUE", currentUser);
                startActivity(intent);
            }
        });

        addAccommodation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, AddAccommodation.class);
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

        //        blockUsers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                redirectActivity(HomeScreen.this, AdminUserBlockingActivity.class);
//            }
//        });

    }


    //TODO: PROVERI DELETE METODU DA LI RADI KAKO TREBA!
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


    // FETCH AND DISPLAY NOTIFICATIONS
    public void fetchNotifications() {
        if (!getIntent().getBooleanExtra("shouldFetch", false)){
            return;
        }
        Call<Notification> call = notificationApi.getLatestNotification(currentUserEmail);
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful()) {
                    Notification notification = response.body();
                    if (notification != null) {
                        showNotification(notification);
                    }
                    getIntent().putExtra("shouldFetch", false);
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(HomeScreen.this, "No new notifications!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Toast.makeText(HomeScreen.this, "Failed to fetch notifications!", Toast.LENGTH_SHORT).show();

            }
        });
    }


    //TODO: CHANGE ID
    private static final int NOTIFICATION_ID = 1;

    private void showNotification(Notification notification) {
        Intent intent = new Intent(this, HomeScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.nav_notification)
                .setContentTitle("New message")
                .setContentText(notification.getMessage())
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Notification Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void requestPostNotificationsPermission() {
        // Request the POST_NOTIFICATIONS permission
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                PERMISSION_REQUEST_POST_NOTIFICATIONS);
    }


}