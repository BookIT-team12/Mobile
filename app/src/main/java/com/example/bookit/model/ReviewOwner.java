package com.example.bookit.model;

public class ReviewOwner extends Review {
    private User owner;

    public ReviewOwner() {}

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
