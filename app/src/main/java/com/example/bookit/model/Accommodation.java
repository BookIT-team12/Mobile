package com.example.bookit.model;

import com.example.bookit.model.enums.AccommodationStatus;
import com.example.bookit.model.enums.AccommodationType;
import com.example.bookit.model.enums.BookingConfirmationType;

import java.util.List;

public class Accommodation {
    private int id;
    private User owner;
    private AccommodationType accommodationType;
    private String description;
    private String name;
    private int minGuests;
    private int maxGuests;
    private List<Amenity> amenities;
    private List<ReviewAccommodation> reviews;
    private List<Reservation> reservations;
    private BookingConfirmationType bookingConfirmationType;
    private AccommodationStatus accommodationStatus;
    private List<AvailabilityPeriod> availabilityPeriods;
    private String imagesFolder;
    private Location location;
    private boolean isFavorite;

    // Constructors, getters, and setters

    // Example constructor (you may need to create other constructors)
    public Accommodation(int id, User owner, AccommodationType accommodationType, String description, String name,
                         int minGuests, int maxGuests, List<Amenity> amenities, List<ReviewAccommodation> reviews,
                         List<Reservation> reservations, BookingConfirmationType bookingConfirmationType,
                         AccommodationStatus accommodationStatus, List<AvailabilityPeriod> availabilityPeriods,
                         String imagesFolder, Location location, boolean isFavorite) {
        this.id = id;
        this.owner = owner;
        this.accommodationType = accommodationType;
        this.description = description;
        this.name = name;
        this.minGuests = minGuests;
        this.maxGuests = maxGuests;
        this.amenities = amenities;
        this.reviews = reviews;
        this.reservations = reservations;
        this.bookingConfirmationType = bookingConfirmationType;
        this.accommodationStatus = accommodationStatus;
        this.availabilityPeriods = availabilityPeriods;
        this.imagesFolder = imagesFolder;
        this.location = location;
        this.isFavorite = isFavorite;
    }

    // Add other methods if needed

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
