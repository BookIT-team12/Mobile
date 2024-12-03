package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookit.app.AppPreferences;
import com.example.bookit.model.Accommodation;
import com.example.bookit.model.AvailabilityPeriod;
import com.example.bookit.model.Location;
import com.example.bookit.model.Reservation;
import com.example.bookit.model.ResponseAccommodationImages;
import com.example.bookit.model.Review;
import com.example.bookit.model.enums.AccommodationStatus;
import com.example.bookit.model.enums.AccommodationType;
import com.example.bookit.model.enums.BookingConfirmationType;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.AccommodationApi;
import com.example.bookit.security.UserTokenService;
import com.example.bookit.utils.UriUtils;
import com.example.bookit.utils.asyncTasks.FetchAccommodationDetailsTask;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAccommodation extends AppCompatActivity {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

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

    private EditText accommodationName;
    private EditText accommodationDescription;
    private EditText minGuests;
    private EditText maxGuests;

    private EditText accommodationCancel;
    private EditText accommodationPrice;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Accommodation accommodation;
    private Spinner availabilityPeriodsSpinner;

    private Integer accommodationID;


    private List<String> accommodationImagesBytesList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_accommodation);

        userTokenService = new UserTokenService(getApplicationContext());

        RetrofitService retrofitService = new RetrofitService(getApplicationContext());
        accommodationApi = retrofitService.getRetrofit().create(AccommodationApi.class);
        accommodationID = getIntent().getIntExtra("UPDATE_ACCOMMODATION", 0);
        getAccommodationDetails(accommodationID);


        // Initialize map view
        customMapView = findViewById(R.id.mapView);
        customMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);


        // Enable controls over map
        customMapView.setBuiltInZoomControls(true);
        customMapView.setMultiTouchControls(true);
        customMapView.setClickable(true);

        // Initialize drawer and menu
        drawerLayout = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);


        dateSpinnerFrom = findViewById(R.id.dateFromSpinner);
        dateSpinnerTo = findViewById(R.id.dateToSpinner);
        availabilityPeriodsSpinner = findViewById(R.id.availabilityPeriods);



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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
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


    }

    private void setMarkerOnUserLocation(GeoPoint userLocation) {
        Marker userMarker = new Marker(customMapView);
        userMarker.setPosition(userLocation);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        customMapView.getOverlays().add(userMarker);
    }


    private void setUpAccommodationDetails(){

        minGuests = findViewById(R.id.updateMinGuests);
        minGuests.setText(String.valueOf(accommodation.getMinGuests()));

        maxGuests = findViewById(R.id.updateMaxGuests);
        maxGuests.setText(String.valueOf(accommodation.getMaxGuests()));

        accommodationDescription=findViewById(R.id.updateAccommodationDescription);
        accommodationDescription.setText(accommodation.getDescription());

        accommodationName=findViewById(R.id.updateAccommodationName);
        accommodationName.setText(accommodation.getName());


        accommodationCancel=findViewById(R.id.updateCancelAllow);
        accommodationCancel.setText(String.valueOf(accommodation.getCancelAllow()));

        accommodationPrice=findViewById(R.id.updatePricePerNight);
//        accommodationPrice.setText(String.valueOf(accommodation.getPricePerNight()));


    }

    public void getAccommodationDetails(int id){
        Call<ResponseAccommodationImages>call=accommodationApi.viewAccommodationDetails(id);
        call.enqueue(new Callback<ResponseAccommodationImages>() {
            @Override
            public void onResponse(Call<ResponseAccommodationImages> call, Response<ResponseAccommodationImages> response) {
                accommodation=response.body().getFirst();
                setUpAccommodationDetails();

                setupDateSpinner(dateSpinnerFrom);
                setupDateSpinner(dateSpinnerTo);

                setupAccommodationTypeSpinnner();
                setupBookingConfirmationTypeSpinner();

                //set up accommodation location
                GeoPoint initialCenter = new GeoPoint(accommodation.getLocation().getLatitude(), accommodation.getLocation().getLongitude());
                customMapView.getController().setCenter(initialCenter);
                customMapView.getController().setZoom(15);
                setMarkerOnUserLocation(initialCenter);

                //set accommodation amenities

                checkAccommodationAmenities(accommodation.getAmenities());

                setupAvailabilityPeriodsSpinner(accommodation.getAvailabilityPeriods());


            }

            @Override
            public void onFailure(Call<ResponseAccommodationImages> call, Throwable t) {

            }
        });
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


    //TODO:DONE
    private void setupAccommodationTypeSpinnner(){
        Spinner accommodationTypeSpinner = findViewById(R.id.accommodationType);
        String[] accommodationTypes = {"Select accommodation type", "STUDIO", "APARTMENT", "HOTEL", "ROOM"};

        ArrayAdapter<String> accommodationTypeAdapter = new ArrayAdapter<>(
                this, R.layout.custom_date_spinner_item, accommodationTypes
        );
        accommodationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accommodationTypeSpinner.setAdapter(accommodationTypeAdapter);
        //accommodation.getAccommodationType());

        if (accommodation.getAccommodationType() != null) {
            String accommodationType=accommodation.getAccommodationType().toString();
            for (int i = 0; i < accommodationTypes.length; i++) {
                if (accommodationType.equals(accommodationTypes[i])) {
                    accommodationTypeSpinner.setSelection(i);
                    break; // Exit the loop once the match is found
                }
            }
    }
    }

    private void setupBookingConfirmationTypeSpinner(){
        String[] confirmationTypes=new String[]{"Select confirmation type", "MANUAL", "AUTOMATIC"};
        Spinner confirmationTypeSpinner = findViewById(R.id.confirmationType);
        ArrayAdapter<String> confirmationTypeAdapter = new ArrayAdapter<>(
                this, R.layout.custom_date_spinner_item,confirmationTypes

        );
        confirmationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        confirmationTypeSpinner.setAdapter(confirmationTypeAdapter);

        if (accommodation.getBookingConfirmationType() != null) {
            String accommodationType=accommodation.getBookingConfirmationType().toString();

            for (int i = 0; i < confirmationTypes.length; i++) {
                if (accommodationType.equals(confirmationTypes[i])) {
                    confirmationTypeSpinner.setSelection(i);
                    break; // Exit the loop once the match is found
                }
            }
    }
    }

    private void setupAvailabilityPeriodsSpinner(List<AvailabilityPeriod> periods) {
        // Create a list of period names
        final String[] periodNames = new String[periods.size() + 1];
        periodNames[0] = "Select availability period";

        for (int i = 0; i < periods.size(); i++) {
            AvailabilityPeriod period = periods.get(i);
            periodNames[i + 1] = String.valueOf(period.getId()); // Assuming AvailabilityPeriod has a getName() method
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_date_spinner_item, periodNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        availabilityPeriodsSpinner.setAdapter(adapter);

        availabilityPeriodsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0) {
                    AvailabilityPeriod selectedPeriod = periods.get(position - 1);

                    accommodationPrice.setText(String.valueOf(selectedPeriod.getPrice()));
                    setupDateSpinners(selectedPeriod);
                }
                if(position==0){
                    accommodationPrice.setText("0");
                    dateSpinnerTo.setSelection(0);
                    dateSpinnerFrom.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void setupDateSpinners(AvailabilityPeriod selectedPeriod) {
        String startDateString = formatDate(selectedPeriod.getStartDate());
        String endDateString = formatDate(selectedPeriod.getEndDate());

        List<String> dateOptions = new ArrayList<>();
        dateOptions.add(startDateString);
        dateOptions.add(endDateString);

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this, R.layout.custom_date_spinner_item, dateOptions);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dateSpinnerFrom.setAdapter(dateAdapter);
        dateSpinnerTo.setAdapter(dateAdapter);

        dateAdapter.notifyDataSetChanged();

        dateSpinnerFrom.setSelection(0);
        dateSpinnerTo.setSelection(1);
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
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
            dateOptions.remove("Select a date");
            dateOptions.add(selectedDateStr);
        } else {
            dateOptions.remove("Select a date");
            dateOptions.add(selectedDateStr);
            //dateOptions.set(currentPosition, selectedDateStr);
        }

        ArrayAdapter<String> newAdapter = new ArrayAdapter<>(
                this, R.layout.custom_date_spinner_item, dateOptions);
        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dateSpinnerFrom.setAdapter(newAdapter);
        dateSpinnerFrom.setSelection(currentPosition); // Set the selection to the updated position

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
        EditText nameTF = findViewById(R.id.updateAccommodationName);
        EditText descriptionTF = findViewById(R.id.updateAccommodationDescription);
        Spinner typeSpinner = findViewById(R.id.accommodationType);
        int typeSpinnerSelectedItemPosition = typeSpinner.getSelectedItemPosition();
        Spinner confirmationTypeSpinner = findViewById(R.id.confirmationType);
        int confirmationTypeSelectedItemPosition = confirmationTypeSpinner.getSelectedItemPosition();
        EditText minGuestsTF = findViewById(R.id.updateMinGuests);
        EditText maxGuestsTF = findViewById(R.id.updateMaxGuests);
        EditText priceTF = findViewById(R.id.updatePricePerNight);
        EditText cancelAllowTF = findViewById(R.id.updateCancelAllow);
        Spinner startDateSpinner = findViewById(R.id.dateFromSpinner);
        String startDateString = (String) startDateSpinner.getItemAtPosition(0);
        Spinner endDateSpinner = findViewById(R.id.dateToSpinner);
        String endDateString = (String) endDateSpinner.getItemAtPosition(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //validate
        boolean invalid = handleErrors(
                nameTF.getText().toString(),
                (String) typeSpinner.getItemAtPosition(typeSpinnerSelectedItemPosition),
                (String) confirmationTypeSpinner.getItemAtPosition(confirmationTypeSelectedItemPosition),
                minGuestsTF.getText().toString(),
                maxGuestsTF.getText().toString(),
                startDateString, endDateString,
                priceTF.getText().toString(),
                amenitiesVal,
                locationVal,
                cancelAllowTF.getText().toString());
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
        int cancelAllowVal = Integer.parseInt(cancelAllowTF.getText().toString());

        int priceVal = Integer.parseInt(priceTF.getText().toString());
        LocalDateTime startDate = LocalDateTime.parse(startDateString+" 00:00:00", formatter);
        LocalDateTime endDate = LocalDateTime.parse(endDateString+" 00:00:00", formatter);
        List<AvailabilityPeriod> periodsVal = new ArrayList<>();
        periodsVal.add(new AvailabilityPeriod(startDate, endDate, priceVal));
        List<Reservation> reservations = new ArrayList<>();
        List<Review> reviews = new ArrayList<>();

        Accommodation toSubmit =
                new Accommodation(accommodationID, owner, typeVal,
                        descriptionVal, nameVal, minGuestsVal, maxGuestsVal,
                        amenitiesVal, reviews, reservations, confirmationTypeVal,
                        AccommodationStatus.PENDING,
                        periodsVal, null, locationVal,
                        false, cancelAllowVal,
                        isByNight());


        Gson gson = new Gson();
        String accommodationJson = gson.toJson(toSubmit);
        RequestBody accommodationRequestBody = RequestBody.create(MediaType.parse("application/json"), accommodationJson);

        Map<String, RequestBody> accommodationPartMap = new HashMap<>();
        accommodationPartMap.put("accommodation", accommodationRequestBody);

// Call the Retrofit method
        accommodationApi.updateAccommodation(accommodationID,accommodationPartMap, imagesVal).enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(Call<Accommodation> call, Response<Accommodation> response) {
                if (response.isSuccessful()) {
                    showSnackbar("You have updated accommodation successfully");
                    Intent intent = new Intent(UpdateAccommodation.this, HomeScreen.class);
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

    private void checkAccommodationAmenities(List<Integer> amenities) {
        CheckBox parkingCB = findViewById(R.id.checkBoxParking);
        CheckBox wifiCB = findViewById(R.id.checkBoxWifi);
        CheckBox balconyCB = findViewById(R.id.checkBoxBalcony);
        CheckBox ACCB = findViewById(R.id.checkBoxAirConditioning);
        CheckBox kitchenCB = findViewById(R.id.checkBoxKitchen);
        CheckBox poolCB = findViewById(R.id.checkBoxPool);
        CheckBox bathroomCB = findViewById(R.id.checkBoxBathroom);

        for (Integer amenity : amenities) {
            switch (amenity) {
                case 1:
                    parkingCB.setChecked(true);
                    break;
                case 2:
                    wifiCB.setChecked(true);
                    break;
                case 3:
                    balconyCB.setChecked(true);
                    break;
                case 4:
                    ACCB.setChecked(true);
                    break;
                case 5:
                    kitchenCB.setChecked(true);
                    break;
                case 6:
                    poolCB.setChecked(true);
                    break;
                case 7:
                    bathroomCB.setChecked(true);
                    break;
                // Add more cases if you have more amenities
            }
        }
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

    private boolean isByNight(){    //gets pricing plan boolean that we forward to constructor of accommodation
        RadioGroup radioGroup = findViewById(R.id.pricingPlan_radioGroup);

        // Retrieve the ID of the selected RadioButton
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        // Check if any RadioButton is selected
        if (checkedRadioButtonId != -1) {
            // Retrieve the selected RadioButton using its ID
            RadioButton selectedRadioButton = findViewById(checkedRadioButtonId);

            // Retrieve the text (value) of the selected RadioButton
            String selectedValue = selectedRadioButton.getText().toString();
            if (selectedValue.equals("By night")){
                return  true;
            }
            else {return false;}
        }
        return true;
    }

    public String getValidationErrors(String name, String typeVal, String confirmationType, String minGuests, String maxGuests,
                                      String startDate, String endDate, String price, List<Integer> amenities, Location location, String cancelAllow){
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
        if (!cancelAllow.matches("\\d+") || Integer.parseInt(cancelAllow) <= 0){
            return "CANCEL_ALLOW_NUMBERS_ERR";
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
                                 String startDate, String endDate, String price, List<Integer> amenities, Location location, String cancelAllow) {
        String err = getValidationErrors(name, typeVal, confirmationType, minGuests, maxGuests, startDate, endDate, price, amenities, location, cancelAllow);
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
            case "CANCEL_ALLOW_NUMBERS_ERR":
                showSnackbar("Number of days for free cancellation must be higher than 0 and only number");
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
