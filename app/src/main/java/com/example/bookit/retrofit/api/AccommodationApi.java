package com.example.bookit.retrofit.api;

import android.util.Pair;

import com.example.bookit.model.Accommodation;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Optional;

public interface AccommodationApi {

    @GET("/accommodations/{id}")
    Call<Pair<Optional<Accommodation>, List<byte[]>>> viewAccommodationDetails(@Path("id") int id);

    @GET("/accommodations/owner/{ownerEmail}")
    Call<List<Accommodation>> getOwnerAccommodations(@Path("ownerEmail") String ownerEmail);

    @GET("/accommodations/pending")
    Call<List<Accommodation>> getPendingAccommodations();

    @GET("/accommodations")
    Call<List<Accommodation>> getAllApartments();

    @Multipart
    @POST("/accommodations")
    Call<Accommodation> createAccommodation(
            @Part("accommodation") Accommodation accommodation,
            @Part List<MultipartBody.Part> images
    );

    @Multipart
    @PUT("/accommodations/{id}")
    Call<Accommodation> updateAccommodation(
            @Path("id") int id,
            @Part("accommodation") Accommodation accommodation,
            @Part List<MultipartBody.Part> images
    );

    @POST("/accommodations/approve/{id}")
    Call<Void> approveAccommodation(@Path("id") int id);

    @POST("/accommodations/deny/{id}")
    Call<Void> denyAccommodation(@Path("id") int id);

    @DELETE("/accommodations/{id}")
    Call<String> deleteAccommodation(@Path("id") int id);
}
