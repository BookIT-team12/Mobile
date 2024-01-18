package com.example.bookit.model;

import com.example.bookit.model.enums.AccommodationStatus;
import com.example.bookit.model.enums.AccommodationType;
import com.example.bookit.model.enums.BookingConfirmationType;

import java.util.List;

public class Accommodation {
    private Integer id;
    private String ownerEmail;
    private AccommodationType accommodationType;
    private String description;
    private String name;
    private int minGuests;
    private int maxGuests;
    private List<Integer> amenities;
    private List<Review> reviews;
    private List<Reservation> reservations;
    private BookingConfirmationType bookingConfirmationType;
    private AccommodationStatus accommodationStatus;
    private List<AvailabilityPeriod> availabilityPeriods;
    private String imagesFolder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public AccommodationType getAccommodationType() {
        return accommodationType;
    }

    public void setAccommodationType(AccommodationType accommodationType) {
        this.accommodationType = accommodationType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinGuests() {
        return minGuests;
    }

    public void setMinGuests(int minGuests) {
        this.minGuests = minGuests;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public List<Integer> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Integer> amenities) {
        this.amenities = amenities;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public BookingConfirmationType getBookingConfirmationType() {
        return bookingConfirmationType;
    }

    public void setBookingConfirmationType(BookingConfirmationType bookingConfirmationType) {
        this.bookingConfirmationType = bookingConfirmationType;
    }

    public AccommodationStatus getAccommodationStatus() {
        return accommodationStatus;
    }

    public void setAccommodationStatus(AccommodationStatus accommodationStatus) {
        this.accommodationStatus = accommodationStatus;
    }

    public List<AvailabilityPeriod> getAvailabilityPeriods() {
        return availabilityPeriods;
    }

    public void setAvailabilityPeriods(List<AvailabilityPeriod> availabilityPeriods) {
        this.availabilityPeriods = availabilityPeriods;
    }

    public String getImagesFolder() {
        return imagesFolder;
    }

    public void setImagesFolder(String imagesFolder) {
        this.imagesFolder = imagesFolder;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private Location location;
    private boolean isFavorite;

    // Constructors, getters, and setters

    // Example constructor (you may need to create other constructors)
    public Accommodation(Integer id, String owner, AccommodationType accommodationType, String description, String name,
                         int minGuests, int maxGuests, List<Integer> amenities, List<Review> reviews,
                         List<Reservation> reservations, BookingConfirmationType bookingConfirmationType,
                         AccommodationStatus accommodationStatus, List<AvailabilityPeriod> availabilityPeriods,
                         String imagesFolder, Location location, boolean isFavorite) {
        this.id = id;
        this.ownerEmail = owner;
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
