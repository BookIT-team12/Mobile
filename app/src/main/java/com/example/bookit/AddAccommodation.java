package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.bookit.app.AppPreferences;
import com.example.bookit.model.Accommodation;
import com.example.bookit.model.AvailabilityPeriod;
import com.example.bookit.model.Location;
import com.example.bookit.model.Reservation;
import com.example.bookit.model.ReviewAccommodation;
import com.example.bookit.model.User;
import com.example.bookit.model.enums.AccommodationStatus;
import com.example.bookit.model.enums.AccommodationType;
import com.example.bookit.model.enums.BookingConfirmationType;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.retrofit.api.UserApi;
import com.example.bookit.security.UserTokenService;
import com.example.bookit.utils.UriUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddAccommodation extends AppCompatActivity {
    private Spinner dateSpinnerFrom;
    private Spinner dateSpinnerTo;
    private Calendar selectedDate;
    private DrawerLayout drawerLayout;
    private CustomMapView customMapView;
    private List<File> selectedImagesFiles;
    private FlexboxLayout imageUrisListLayout;
    private ImageView selectedImageView;
    private UserTokenService userTokenService;

    private AccommodationApi accommodationApi;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accommodation);

        userTokenService = new UserTokenService(getApplicationContext());

        dateSpinnerFrom = findViewById(R.id.dateFromSpinner);
        dateSpinnerTo = findViewById(R.id.dateToSpinner);

        // Initialize map view
        customMapView = findViewById(R.id.mapView);
        customMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        GeoPoint initialCenter = new GeoPoint(45.2671, 19.8335);
        customMapView.getController().setCenter(initialCenter);
        customMapView.getController().setZoom(15);

        // Enable controls over map
        customMapView.setBuiltInZoomControls(true);
        customMapView.setMultiTouchControls(true);
        customMapView.setClickable(true);

        // Initialize drawer and menu
        drawerLayout = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);

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

        selectedImagesFiles = new ArrayList<>();

        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnDeleteImage = findViewById(R.id.btnDeleteImage);
        imageUrisListLayout = findViewById(R.id.imageUrisList);

        btnSelectImage.setOnClickListener(v -> {
            // Add this code before initiating image picking ==> asking for permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                // Permission is already granted, proceed with your code to pick an image
                pickImageFromGallery();
            }
        });

        btnDeleteImage.setOnClickListener(view -> {
            deleteImage();
        });

        Button submitBtn = findViewById(R.id.addAccommodationBtn);
        submitBtn.setOnClickListener(view -> {
            try {
                submitAccommodation();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this));

        RetrofitService retrofitService = new RetrofitService(getApplicationContext());
        accommodationApi = retrofitService.getRetrofit().create(AccommodationApi.class);
    }

    private void pickImageFromGallery() {   //open gallery to pick image
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override   //handle permission request result
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your code to pick an image
                pickImageFromGallery();
            } else {
                // Permission denied, handle accordingly (e.g., show a message or disable functionality)
                Toast.makeText(this, "Permission denied. Cannot access gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override   //handle what happens after you select image from gallery!
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            File imageFile = UriUtils.getFileFromUri(getApplicationContext(), selectedImageUri);
            this.selectedImagesFiles.add(imageFile);

            if (imageFile != null) {
                ImageView imageView = new ImageView(this);
                imageView.setTag(selectedImageUri);
                imageView.setImageURI(selectedImageUri);

                int desiredWidth = 200; // Adjust this value based on your requirements
                int desiredHeight = 200; // Adjust this value based on your requirements
                int marginBottomInDp = 5; // Desired bottom margin in dp

                float density = getResources().getDisplayMetrics().density;
                int marginBottomInPixels = Math.round(marginBottomInDp * density);
                ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(desiredWidth, desiredHeight);
                layoutParams.setMargins(0, 0, 0, marginBottomInPixels);
                imageView.setLayoutParams(layoutParams);

                imageView.setOnClickListener(v -> handleImageClick(imageView, selectedImageUri));
                imageUrisListLayout.addView(imageView);
            }
        }
    }

    private void handleImageClick(ImageView imageView, Uri selectedImageUri) {

        if (this.selectedImageView != null){
            this.selectedImageView.setBackground(null);
            if (this.selectedImageView == imageView){
                this.selectedImageView = null;
                return;
            }
            this.selectedImageView = null;
        }

        int borderWidthInDp = 5;
        float density = getResources().getDisplayMetrics().density;
        int borderWidthInPixels = Math.round(borderWidthInDp * density);

        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setStroke(borderWidthInPixels, Color.BLUE);
        imageView.setBackground(borderDrawable);

        this.selectedImageView = imageView;
    }
    private void deleteImage(){
        Uri UriToDelete = (Uri) this.selectedImageView.getTag();
        File fileToDelete = UriUtils.getFileFromUri(getApplicationContext(), UriToDelete);
        this.selectedImagesFiles.remove(fileToDelete);
        this.imageUrisListLayout.removeView(this.selectedImageView);
        this.selectedImageView = null;
    }

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
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

    private void submitAccommodation() throws ParseException {
        List<Integer> amenitiesVal = gatherAmenities();
        List<MultipartBody.Part> imagesVal = handleImages();
        Location locationVal = handleLocation();

        //get things from gui itself
        EditText nameTF = findViewById(R.id.accommodationName);
        EditText descriptionTF = findViewById(R.id.accommodationDescription);
        Spinner typeSpinner = findViewById(R.id.accommodationType);
        int typeSpinnerSelectedItemPosition = typeSpinner.getSelectedItemPosition();
        Spinner confirmationTypeSpinner = findViewById(R.id.confirmationType);
        int confirmationTypeSelectedItemPosition = confirmationTypeSpinner.getSelectedItemPosition();
        EditText minGuestsTF = findViewById(R.id.minGuests);
        EditText maxGuestsTF = findViewById(R.id.maxGuests);
        EditText priceTF = findViewById(R.id.pricePerNight);
        Spinner startDateSpinner = findViewById(R.id.dateFromSpinner);
        String startDateString = (String) startDateSpinner.getItemAtPosition(0);
        Spinner endDateSpinner = findViewById(R.id.dateToSpinner);
        String endDateString = (String) endDateSpinner.getItemAtPosition(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //validate
        boolean invalid = handleErrors(nameTF.getText().toString(), (String) typeSpinner.getItemAtPosition(typeSpinnerSelectedItemPosition),
                (String) confirmationTypeSpinner.getItemAtPosition(confirmationTypeSelectedItemPosition), minGuestsTF.getText().toString(),
                maxGuestsTF.getText().toString(), startDateString, endDateString, priceTF.getText().toString(), amenitiesVal, locationVal);
        if (invalid){
            return;
        }

        //turn things from gui to needed shape
        String nameVal = nameTF.getText().toString();
        String owner = userTokenService.getCurrentUser(AppPreferences.getToken(getApplicationContext()));
        AccommodationType typeVal = AccommodationType.fromString(typeSpinner.getItemAtPosition(typeSpinnerSelectedItemPosition).toString());
        BookingConfirmationType confirmationTypeVal = BookingConfirmationType.fromString(confirmationTypeSpinner.getItemAtPosition(confirmationTypeSelectedItemPosition).toString());
        String descriptionVal = descriptionTF.getText().toString();
        int minGuestsVal = Integer.parseInt(minGuestsTF.getText().toString());
        int maxGuestsVal = Integer.parseInt(maxGuestsTF.getText().toString());

        int priceVal = Integer.parseInt(priceTF.getText().toString());
        LocalDateTime startDate = LocalDateTime.parse(startDateString+" 00:00:00", formatter);
        LocalDateTime endDate = LocalDateTime.parse(endDateString+" 00:00:00", formatter);
        List<AvailabilityPeriod> periodsVal = new ArrayList<>();
        periodsVal.add(new AvailabilityPeriod(startDate, endDate, priceVal));
        List<Reservation> reservations = new ArrayList<>();
        List<ReviewAccommodation> reviews = new ArrayList<>();
        Accommodation toSubmit = new Accommodation(null, owner, typeVal, descriptionVal, nameVal, minGuestsVal, maxGuestsVal,
                amenitiesVal, reviews, reservations, confirmationTypeVal, AccommodationStatus.PENDING,
                periodsVal, null, locationVal, false);

        accommodationApi.createAccommodation(toSubmit, imagesVal).enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(Call<Accommodation> call, Response<Accommodation> response) {
                if (response.isSuccessful()) {
                    showSnackbar("You have registered accommodation successfully");
                    Intent intent = new Intent(AddAccommodation.this, HomeScreen.class);
                    intent.putExtra("ROLE", "owner");
                    startActivity(intent);
                }
                else {
                    showSnackbar("You didnt register accommodation successfully. Error happened");
                }
            }

            @Override
            public void onFailure(Call<Accommodation> call, Throwable t) {
                showSnackbar("You didnt register accommodation successfully. Error happened");
            }
        });
    }

    private List<Integer> gatherAmenities(){
        CheckBox parkingCB = findViewById(R.id.checkBoxParking);
        CheckBox wifiCB = findViewById(R.id.checkBoxWifi);
        CheckBox balconyCB = findViewById(R.id.checkBoxBalcony);
        CheckBox ACCB = findViewById(R.id.checkBoxAirConditioning);
        CheckBox kitchenCB = findViewById(R.id.checkBoxKitchen);
        CheckBox poolCB = findViewById(R.id.checkBoxPool);
        CheckBox bathroomCB = findViewById(R.id.checkBoxBathroom);

        List<Integer> retVal = new ArrayList<>();
        if (parkingCB.isChecked()){retVal.add(1);}
        if (wifiCB.isChecked()){retVal.add(2);}
        if (balconyCB.isChecked()){retVal.add(3);}
        if (ACCB.isChecked()){retVal.add(4);}
        if (kitchenCB.isChecked()){retVal.add(5);}
        if (poolCB.isChecked()){retVal.add(6);}
        if (bathroomCB.isChecked()){retVal.add(7);}
        return retVal;
    }

    private List<MultipartBody.Part> handleImages() {
        List<MultipartBody.Part> retVal = new ArrayList<>();

        for (File file : this.selectedImagesFiles) {
            // Create a RequestBody instance from the file
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // Create a MultipartBody.Part from the RequestBody
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("accommodation_images", file.getName(), requestFile);

            // Add the MultipartBody.Part to the list
            retVal.add(imagePart);
        }

        return retVal;
    }

    private Location handleLocation(){
        GeoPoint geoPoint = customMapView.getSelectedGeoPoint();
        return new Location(getApplicationContext(), geoPoint);
    }

    public String getValidationErrors(String name, String typeVal, String confirmationType, String minGuests, String maxGuests,
                                      String startDate, String endDate, String price, List<Integer> amenities, Location location){
        if (name.equals("")){
            return "NAME_ERR";
        }
        if (!typeVal.equals("STUDIO") && !typeVal.equals("APARTMENT") && !typeVal.equals("ROOM") && !typeVal.equals("HOTEL")){
            return "BAD_TYPE_ERR";
        }
        if (!confirmationType.equals("AUTOMATIC") && !confirmationType.equals("MANUAL")){
            return "BAD_CONFIRMATION_TYPE_ERR";
        }
        if (!minGuests.matches("\\d+")){
            return "MIN_GUESTS_NUMBERS_ERR";
        }
        if (!maxGuests.matches("\\d+")){
            return "MAX_GUESTS_NUMBERS_ERR";
        }
        if (Integer.parseInt(minGuests) > Integer.parseInt(maxGuests)){
            return "MAX_LOWER_THAN_MIN_ERR";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        LocalDateTime date1;
        LocalDateTime date2;
        try {
            date1 = LocalDateTime.parse(startDate+ " 00:00:00", formatter);
            date2 = LocalDateTime.parse(endDate+" 00:00:00", formatter);
        } catch (Exception e) {
            return "WRONG_DATE_FORMAT_ERR";
        }
        if (date2.isBefore(date1)){
            return "END_BEFORE_START_ERR";
        }
        if (!price.matches("\\d+")){
            return "PRICE_NUMBERS_ERR";
        }
        if (amenities.isEmpty()){
            return "NO_AMENITIES_ERR";
        }
        if (location == null){
            return "NO_LOCATION_ERR";
        }
        return "NO_ERR";
    }

    private boolean handleErrors(String name, String typeVal, String confirmationType, String minGuests, String maxGuests,
                              String startDate, String endDate, String price, List<Integer> amenities, Location location) {
        String err = getValidationErrors(name, typeVal, confirmationType, minGuests, maxGuests, startDate, endDate, price, amenities, location);
        switch (err) {
            case "NAME_ERR":
               showSnackbar("You have to enter name!");
               return true;
            case "BAD_TYPE_ERR":
                showSnackbar("You have to select accommodation type!");
                return true;
            case "BAD_CONFIRMATION_TYPE_ERR":
                showSnackbar("You have to select confirmation type!");
                return true;
            case "MIN_GUESTS_NUMBERS_ERR":
                showSnackbar("Min guest must be number higher than 0");
                return true;
            case "MAX_GUESTS_NUMBERS_ERR":
                showSnackbar("Max guest must be number higher than 0");
                return true;
            case "MAX_LOWER_THAN_MIN_ERR":
                showSnackbar("Max guest number must be bigger than min guest!");
                return true;
            case "WRONG_DATE_FORMAT_ERR":
                showSnackbar("You have entered wrong dates in wrong format!");
                return true;
            case "END_BEFORE_START_ERR":
                showSnackbar("End date must be after start date");
                return true;
            case "PRICE_NUMBERS_ERR":
                showSnackbar("You have to enter price!");
                return true;
            case "NO_AMENITIES_ERR":
                showSnackbar("You have to enter some amenities!");
                return true;
            case "NO_LOCATION_ERR":
                showSnackbar("You have to enter location!");
                return true;
            case "NO_ERR":
                return false;
            default:
                return true;
        }
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
