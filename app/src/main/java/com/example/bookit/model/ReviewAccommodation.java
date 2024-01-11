package com.example.bookit.model;

public class ReviewAccommodation extends Review {
    private Accommodation accommodation;

    public ReviewAccommodation() {}

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }
}
