package com.example.bookit.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Location {
    private Integer id;
    private String address;
    private double longitude;
    private double latitude;

    public Location(String address, double longitude, double latitude) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Constructor to create Location from GeoPoint
    public Location(Context context, GeoPoint geoPoint) {
        if (geoPoint != null) {
            this.latitude = geoPoint.getLatitude();
            this.longitude = geoPoint.getLongitude();
            this.address = getAddressFromGeoPoint(context, geoPoint);
        }
    }

    private String getAddressFromGeoPoint(Context context, GeoPoint geoPoint) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address addressObj = addresses.get(0);
                // You can customize the address format based on your requirements
                return addressObj.getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e("Location", "Error obtaining address from GeoPoint", e);
        }

        return null;
    }
    public Location() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
