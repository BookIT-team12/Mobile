package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.model.Accommodation;
import com.example.bookit.model.ResponseAccommodationImages;
import com.example.bookit.model.Review;
import com.example.bookit.model.enums.ReviewStatus;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.ReviewApi;
import com.example.bookit.security.UserTokenService;
import com.example.bookit.utils.asyncTasks.FetchAccommodationForReviewTask;
import com.example.bookit.utils.asyncTasks.FetchAccommodationReviewsTask;
import com.example.bookit.utils.ReviewAccommodationRecycleViewAdapter;
import com.example.bookit.utils.asyncTasks.FetchAverageAccommodatioGradeTask;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewAccommodationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewAccommodationRecycleViewAdapter adapter;
    private List<Review> allAuthorReviews;
    private ReviewApi api;
    private AccommodationApi accommodationApi;
    String loggedUser;
    Button submitBtn;
    Spinner gradeSpinner;
    TextView nameTF;
    TextView maxGuestsTF;
    TextView minGuestsTF;
    TextView averageGradeTF;
    TextView ownerEmailTF;
    Integer reviewingAccommodationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_accommodation);

        allAuthorReviews = new ArrayList<>();
        reviewingAccommodationId = getIntent().getIntExtra("accommodationId", -100);

        nameTF = findViewById(R.id.accommodationNameTextView_review_accommodation_activity);
        maxGuestsTF = findViewById(R.id.maxGuestsTextView_review_accommodation_activity);
        minGuestsTF = findViewById(R.id.minGuestsTextView_review_accommodation_activity);
        averageGradeTF = findViewById(R.id.averageGradeTextView_review_accommodation_activity);
        ownerEmailTF = findViewById(R.id.ownerEmailTextView_review_accommodation_activity);
        gradeSpinner = findViewById(R.id.spinnerGrade_review_accommodation_activity);
        populateSpinner();
        submitBtn = findViewById(R.id.submitBtn_accommodation_review_activity);

        RetrofitService retrofitService = new RetrofitService(getApplicationContext());
        UserTokenService userTokenService = new UserTokenService(getApplicationContext());

        recyclerView = findViewById(R.id.recycler_view_accommodation_review_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReviewAccommodationRecycleViewAdapter(allAuthorReviews, retrofitService);
        recyclerView.setAdapter(adapter);

        try {
            loggedUser = userTokenService.getCurrentUser(AppPreferences.getToken(getApplicationContext()));
            api = retrofitService.getRetrofit().create(ReviewApi.class);
            new FetchAccommodationReviewsTask(api, loggedUser, allAuthorReviews, adapter).execute();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

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
                    showSnackbar("You didnt submit review for accommodation! Something went wrong");
                }
            });
        });

        accommodationApi = retrofitService.getRetrofit().create(AccommodationApi.class);
        new FetchAccommodationForReviewTask(accommodationApi, reviewingAccommodationId, nameTF, minGuestsTF, maxGuestsTF, ownerEmailTF).execute();
        new FetchAverageAccommodatioGradeTask(api, reviewingAccommodationId, averageGradeTF).execute();
    }

    public Review createNewReview(){
        EditText textTF = findViewById(R.id.editTextMultiline_review_accommodation_activity);
        String text = textTF.getText().toString();
        Double grade = (Double) gradeSpinner.getItemAtPosition(gradeSpinner.getSelectedItemPosition());
        String author = loggedUser;
        LocalDateTime createdAt = LocalDateTime.now();
        ReviewStatus status = ReviewStatus.PENDING;
        return new Review(null, reviewingAccommodationId, null, text, author, createdAt, grade, status);
    }

    public void populateSpinner(){
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
        gradeSpinner.setAdapter(adapter);

    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

}