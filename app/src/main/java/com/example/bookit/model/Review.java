package com.example.bookit.model;

import com.example.bookit.model.enums.ReviewStatus;

import java.time.LocalDateTime;
import java.util.Date;

public class Review {
    private Integer id;
    private Integer accommodationId;
    private String ownerEmail;
    private String text;

    private String authorEmail;
    private LocalDateTime createdAt;

    private double rating;

    private ReviewStatus status;

    public Review() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(Integer accommodationId) {
        this.accommodationId = accommodationId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }
}