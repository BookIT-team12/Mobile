package com.example.bookit.model;

import java.util.List;
import java.util.Optional;

public class ResponseAccommodationImages {
    private Accommodation first;
    private List<String> second;

    public Accommodation getFirst() {
        return first;
    }

    public void setFirst(Accommodation first) {
        this.first = first;
    }

    public List<String> getSecond() {
        return second;
    }

    public void setSecond(List<String> second) {
        this.second = second;
    }
}
