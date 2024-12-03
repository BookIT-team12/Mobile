package com.example.bookit.model;

import com.example.bookit.model.enums.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Date;

public class Review {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("accommodationId")
    private Integer accommodationId;
    @JsonProperty("ownerEmail")
    private String ownerEmail;
    @JsonProperty("text")
    private String text;
    @JsonProperty("authorEmail")
    private String authorEmail;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @JsonProperty("rating")
    private double rating;
    @JsonProperty("status")
    private ReviewStatus status;

    public Review(Integer id, Integer accommodationId, String ownerEmail, String text, String authorEmail, LocalDateTime createdAt, double rating, ReviewStatus status) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.ownerEmail = ownerEmail;
        this.text = text;
        this.authorEmail = authorEmail;
        this.createdAt = createdAt;
        this.rating = rating;
        this.status = status;
    }

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

    public void setStatus(ReviewStatus reviewStatus) {
        this.status = reviewStatus;
    }
}
