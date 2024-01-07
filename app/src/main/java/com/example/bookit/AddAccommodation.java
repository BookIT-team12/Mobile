package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddAccommodation extends AppCompatActivity {
    private WebView webView;
    private Spinner dateSpinnerFrom;
    private Spinner dateSpinnerTo;
    private Calendar selectedDate;

    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accommodation);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);


        dateSpinnerFrom = findViewById(R.id.dateFromSpinner);
        dateSpinnerTo = findViewById(R.id.dateToSpinner);


        // LEAFLET MAP INITIALIZATION
        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl("assets/leaflet_map.html");


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });



        // SPINNERS POPULATION


        setupDateSpinner();
        setupAccommodationTypeSpinnner();
        setupBookingConfirmationTypeSpinner();


        dateSpinnerFrom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showDatePickerDialog(dateSpinnerFrom);
                }
                return false;
            }
        });


        dateSpinnerTo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showDatePickerDialog(dateSpinnerTo);
                }
                return false;
            }
        });
    }





    private void setupDateSpinner() {
        // Populate the Spinner with date options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getDateOptions());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinnerFrom.setAdapter(adapter);
    }


    private void setupAccommodationTypeSpinnner(){
        Spinner accommodationTypeSpinner = findViewById(R.id.accommodationType);
        ArrayAdapter<String> accommodationTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Select accommodation type", "STUDIO", "APARTMENT", "HOTEL", "ROOM"}
        );
        accommodationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accommodationTypeSpinner.setAdapter(accommodationTypeAdapter);
    }

    private void setupBookingConfirmationTypeSpinner(){
            Spinner confirmationTypeSpinner = findViewById(R.id.confirmationType);
            ArrayAdapter<String> confirmationTypeAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item,
                    new String[]{"Select confirmation type", "MANUAL", "AUTOMATIC"}
            );
            confirmationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            confirmationTypeSpinner.setAdapter(confirmationTypeAdapter);
        }


    private void showDatePickerDialog(Spinner dateSpinnerFrom) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, R.style.MyDatePickerDialogTheme,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Handle the selected date
                    updateSelectedDateInView(dateSpinnerFrom);
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void updateSelectedDateInView(Spinner dateSpinnerFrom) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        String selectedDateStr = dateFormat.format(selectedDate.getTime());

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) dateSpinnerFrom.getAdapter();
        List<String> dateOptions = new ArrayList<>(Arrays.asList(getDateOptions()));

        int currentPosition = dateSpinnerFrom.getSelectedItemPosition();

        if (currentPosition == 0) {
            // The first item is the default "Select a date" option, replace it with the selected date
            dateOptions.remove("Select a date");
            dateOptions.add(selectedDateStr);
        } else {
            // The adapter already has a selected date, replace it
            dateOptions.set(currentPosition, selectedDateStr);
        }

        adapter.clear();
        adapter.addAll(dateOptions);
        dateSpinnerFrom.setSelection(currentPosition); // Set the selection to the updated position
    }

    private String[] getDateOptions() {
        // Provide date options for the Spinner (customize as needed)
        return new String[]{"Select a date"};
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