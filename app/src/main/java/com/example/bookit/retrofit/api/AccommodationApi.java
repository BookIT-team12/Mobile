package com.example.bookit.retrofit.api;

import android.util.Pair;

import com.example.bookit.model.Accommodation;
import com.example.bookit.model.ResponseAccommodationImages;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccommodationApi {

    @GET("/api/accommodations/{id}")
    Call<ResponseAccommodationImages> viewAccommodationDetails(@Path("id") int id);

    @GET("/api/accommodations/owner/{ownerEmail}")
    Call<List<Accommodation>> getOwnerAccommodations(@Path("ownerEmail") String ownerEmail);

    @GET("/api/accommodations/pending")
    Call<List<Accommodation>> getPendingAccommodations();
    @GET("/api/accommodations/name/{id}")
    Call<String> getNameById(@Path("id") int id);

    @GET("/api/accommodations")
    Call<List<Accommodation>> getAllApartments();

    @Multipart
    @POST("/api/accommodations")
    Call<Accommodation> createAccommodation(
            @Part("accommodationDTO") Accommodation accommodation,
            @Part List<MultipartBody.Part> images
    );

    @Multipart
    @PUT("/api/accommodations/{id}")
    Call<Accommodation> updateAccommodation(
            @Path("id") int id,
            @PartMap Map<String, RequestBody> accommodationPartMap,
            //    @Part("accommodation") RequestBody accommodationDto, // Use RequestBody
            @Part List<MultipartBody.Part> images
    );

    @POST("/api/accommodations/approve/{id}")
    Call<Void> approveAccommodation(@Path("id") int id);

    @POST("/api/accommodations/deny/{id}")
    Call<Void> denyAccommodation(@Path("id") int id);
    @GET("api/accommodations/accommodationOwner/{id}")
    Call<Map<String, String>> getUserIdBasedOnAccommodationId(@Path("id") Integer id);

}
