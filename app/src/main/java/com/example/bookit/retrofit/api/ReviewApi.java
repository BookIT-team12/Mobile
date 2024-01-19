package com.example.bookit.retrofit.api;

import com.example.bookit.model.Review;

import java.util.List;
import java.util.Optional;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ReviewApi {
    @POST("/api/reviews")
    Call<Review> createReview(@Body Review Review);

    @PUT("/api/reviews/{id}")
    Call<Review> updateReviewStatus(@Body Review review, @Path("id") int id);

    @DELETE("/api/reviews/owner/{id}")
    Call<ResponseBody> deleteOwnerReview(@Path("id") int id);

    @DELETE("/api/reviews/accommodation/{id}")
    Call<ResponseBody> deleteAccommodationReview(@Path("id") int id);

    @GET("/api/reviews/ownerReviews/{email}")
    Call<List<Optional<Review>>> getAllReviewsForOwner(@Path("email") String email);

    @GET("/api/reviews/ownersAccommodationReviews/{email}")
    Call<List<Optional<Review>>> getAllReviewsOnOwnerAccommodations(@Path("email") String email);

    @GET("/api/reviews/authorReviews/owners/{email}")
    Call<List<Review>> getReviewsOwnerByAuthor(@Path("email") String email);

    @GET("/api/reviews/authorReviews/accommodations/{email}")
    Call<List<Review>> getReviewsAccommodationByAuthor(@Path("email") String email);

    @GET("/api/reviews/averageGrade/owner/{email}")
    Call<Double> getOwnerAverageGrade(@Path("email") String email);

    @GET("/api/reviews/averageGrade/accommodation/{id}")
    Call<Double> getAccommodationAverageGrade(@Path("id") int id);

    @GET("/api/reviews/owner/toApprove")
    Call<List<Optional<Review>>> getAllReviewOwnerForApproval();

    @GET("/api/reviews/accommodation/toApprove")
    Call<List<Optional<Review>>> getAllReviewAccommodationForApproval();
}
