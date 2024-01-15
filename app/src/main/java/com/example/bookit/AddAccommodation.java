package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Create a custom MapEventsReceiver class
class MyMapEventsReceiver extends Overlay implements MapEventsReceiver {
    private MapView mapView;
    public MyMapEventsReceiver(MapView mapView) {
        this.mapView = mapView;
    }

    //// Set up the initial marker at the center
    //        Marker initialMarker = new Marker(mapView);
    //        initialMarker.setPosition(initialCenter);
    //        mapView.getOverlays().add(initialMarker);

    // Set up the initial marker at the center
    final Marker[] userMarker = {null};
    @Override
    public boolean singleTapConfirmedHelper(final GeoPoint geoPoint) {
        // Remove the old user-added marker if exists
        if (userMarker[0] != null) {
            mapView.getOverlays().remove(userMarker[0]);
        }

        // Add a new user-added marker at the clicked location
        userMarker[0] = new Marker(mapView);
        userMarker[0].setPosition(geoPoint);
        mapView.getOverlays().add(userMarker[0]);

        // Return true to indicate that the event has been consumed
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        return false;
    }
}

public class AddAccommodation extends AppCompatActivity {
    private WebView webView;
    private Spinner dateSpinnerFrom;
    private Spinner dateSpinnerTo;
    private Calendar selectedDate;

    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout home;
    private CustomMapView customMapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accommodation);
//        System.setProperty("javax.net.debug", "ssl");


        dateSpinnerFrom = findViewById(R.id.dateFromSpinner);
        dateSpinnerTo = findViewById(R.id.dateToSpinner);

        // Initialize map view
        customMapView = findViewById(R.id.mapView);
        customMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        GeoPoint initialCenter = new GeoPoint(45.2671, 19.8335); // Example: New York City
        customMapView.getController().setCenter(initialCenter);
        customMapView.getController().setZoom(15);

        // Enable zoom controls
        customMapView.setBuiltInZoomControls(true);

        // Enable touch controls
        customMapView.setMultiTouchControls(true);
        customMapView.setClickable(true);


//        customMapView.getOverlays().add(new MyMapEventsReceiver(customMapView));


        // Initialize drawer and menu
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);

        // SPINNERS POPULATION
        setupDateSpinner(dateSpinnerFrom);
        setupDateSpinner(dateSpinnerTo);
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
        Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this));
    }

    private void handleSelectedLocation(GeoPoint selectedPoint) {    }

    private void setupDateSpinner(Spinner dateSpinnerFrom) {
        // Populate the Spinner with date options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_date_spinner_item, getDateOptions());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinnerFrom.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dateSpinnerFrom.setSelection(0);
    }

    private void setupAccommodationTypeSpinnner(){
        Spinner accommodationTypeSpinner = findViewById(R.id.accommodationType);
        ArrayAdapter<String> accommodationTypeAdapter = new ArrayAdapter<>(
                this, R.layout.custom_date_spinner_item,
                new String[]{"Select accommodation type", "STUDIO", "APARTMENT", "HOTEL", "ROOM"}
        );
        accommodationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accommodationTypeSpinner.setAdapter(accommodationTypeAdapter);
    }

    private void setupBookingConfirmationTypeSpinner(){
            Spinner confirmationTypeSpinner = findViewById(R.id.confirmationType);
            ArrayAdapter<String> confirmationTypeAdapter = new ArrayAdapter<>(
                    this, R.layout.custom_date_spinner_item,
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

        ArrayAdapter<String> newAdapter = new ArrayAdapter<>(
                this, R.layout.custom_date_spinner_item, dateOptions);
        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dateSpinnerFrom.setAdapter(newAdapter);
        dateSpinnerFrom.setSelection(currentPosition); // Set the selection to the updated position

        // Manually update the displayed value in the Spinner
        runOnUiThread(() -> {
            View selectedView = dateSpinnerFrom.getSelectedView();
            if (selectedView instanceof TextView) {
                ((TextView) selectedView).setText(selectedDateStr);
            }
        });
    }

    private String[] getDateOptions() {
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


//// Set up the initial marker at the center
//        Marker initialMarker = new Marker(mapView);
//        initialMarker.setPosition(initialCenter);
//        mapView.getOverlays().add(initialMarker);

// Set up a click listener for the map using your custom MapEventsReceiver

// Set up a click listener for the ma

//        mapView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    IGeoPoint geoPoint = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
//
//                    Marker marker = new Marker(mapView);
//                    marker.setPosition(new GeoPoint(geoPoint.getLatitude(), geoPoint.getLongitude()));
//                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//                    mapView.getOverlayManager().add(marker);
//
//                    mapView.performClick();
//
//                    return true;
//                }
//
//                return false;
//            }
//        });