package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.model.Review;
import com.example.bookit.model.User;
import com.example.bookit.model.enums.ReviewStatus;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.ReviewApi;
import com.example.bookit.retrofit.api.UserApi;
import com.example.bookit.security.UserTokenService;
import com.example.bookit.utils.asyncTasks.FetchOwnerReviewsTask;
import com.example.bookit.utils.ReviewOwnerRecycleViewAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewOwnerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReviewOwnerRecycleViewAdapter adapter;
    private List<Review> allAuthorReviews;
    private ReviewApi api;
    private UserApi userApi;
    Button submitBtn;
    String loggedUser;
    Spinner gradeSpinner;
    TextView nameTF;
    TextView lastNameTF;
    TextView averageGradeTF;
    TextView ownerEmailTF;

    String owner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_owner);
        allAuthorReviews = new ArrayList<>();
        owner = getIntent().getStringExtra("owner");

        nameTF = findViewById(R.id.ownerNameTextView_review_owner_activity);
        lastNameTF = findViewById(R.id.lastNameTextView_review_owner_activity);
        averageGradeTF = findViewById(R.id.averageGradeTextView_review_owner_activity);
        ownerEmailTF = findViewById(R.id.ownerEmailTextView_review_owner_activity);
        gradeSpinner = findViewById(R.id.spinnerGrade_review_owner_activity);
        populateSpinner();
        RetrofitService retrofitService = new RetrofitService(getApplicationContext());
        UserTokenService tokenService = new UserTokenService(getApplicationContext());

        recyclerView = findViewById(R.id.recycler_view_owner_review_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReviewOwnerRecycleViewAdapter(allAuthorReviews, retrofitService);
        recyclerView.setAdapter(adapter);

        try {
            loggedUser = tokenService.getCurrentUser(AppPreferences.getToken(getApplicationContext()));
            api = retrofitService.getRetrofit().create(ReviewApi.class);
            new FetchOwnerReviewsTask(api, loggedUser, allAuthorReviews, adapter).execute();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        submitBtn = findViewById(R.id.submitBtn_owner_review_activity);
        submitBtn.setOnClickListener(view -> {
            api.createReview(createNewReview()).enqueue(new Callback<Review>() {
                @Override
                public void onResponse(Call<Review> call, Response<Review> response) {
                    showSnackbar("You have submitted review for owner successfully");
                    // Start HomeScreenActivity
                    Intent intent = new Intent(view.getContext(), HomeScreen.class);
                    intent.putExtra("ROLE", "guest");
                    view.getContext().startActivity(intent);
                }

                @Override
                public void onFailure(Call<Review> call, Throwable t) {
                    showSnackbar("You didnt submit review for owner! Something went wrong");
                }
            });
        });

        userApi = retrofitService.getRetrofit().create(UserApi.class);
        userApi.getUser(owner).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User ownerToReview = response.body();
                nameTF.setText(String.format("Name: %s", ownerToReview.getName()));
                lastNameTF.setText(String.format("Last Name: %s", ownerToReview.getLastName()));
                ownerEmailTF.setText(String.format("Owner email: %s", ownerToReview.getEmail()));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showSnackbar("Something went wrong in fetching owner data");
            }
        });

        api.getOwnerAverageGrade(owner).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                Double averageGrade = response.body();
                averageGradeTF.setText(String.format("Average grade: %s", averageGrade));
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                showSnackbar("Something went wrong in fetching average grade for owner");
            }
        });

    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public Review createNewReview(){
        EditText textTF = findViewById(R.id.editTextMultiline);
        String text = textTF.getText().toString();
        Double grade = (Double) gradeSpinner.getItemAtPosition(gradeSpinner.getSelectedItemPosition());
        String author = loggedUser;
        LocalDateTime createdAt = LocalDateTime.now();
        ReviewStatus status = ReviewStatus.PENDING;
        return new Review(null, null, owner, text, author, createdAt, grade, status);
    }

    public void populateSpinner(){
        Spinner spinner = findViewById(R.id.spinnerGrade_review_owner_activity);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<Double> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Add items to the adapter (you can add them individually or from an array)
        adapter.add(1.0);
        adapter.add(2.0);
        adapter.add(3.0);
        adapter.add(4.0);
        adapter.add(5.0);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }
}