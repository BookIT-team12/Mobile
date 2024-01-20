package com.example.bookit.model;

import java.time.LocalDateTime;

public class Notification {

    private Integer id;
    private String guestEmail;
    private String message;
    private LocalDateTime dateTime;

    public Notification(Integer id, String guestEmail, String message, LocalDateTime dateTime) {
        this.id = id;
        this.guestEmail = guestEmail;
        this.message = message;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
