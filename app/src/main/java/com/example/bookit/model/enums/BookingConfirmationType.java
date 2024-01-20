package com.example.bookit.model.enums;

public enum BookingConfirmationType {
    AUTOMATIC, MANUAL;

    public static BookingConfirmationType fromString(String str) {
        try {
            return BookingConfirmationType.valueOf(str);
        } catch (IllegalArgumentException | NullPointerException e) {
            // Handle the case where the input string does not match any enum constant
            return null;
        }
    }
}
