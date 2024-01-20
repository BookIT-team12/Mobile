package com.example.bookit.model.enums;

public enum AccommodationType {
    STUDIO, APARTMENT, ROOM, HOTEL;

    public static AccommodationType fromString(String str) {
        try {
            return AccommodationType.valueOf(str);
        } catch (IllegalArgumentException | NullPointerException e) {
            // Handle the case where the input string does not match any enum constant
            return null;
        }
    }
}
