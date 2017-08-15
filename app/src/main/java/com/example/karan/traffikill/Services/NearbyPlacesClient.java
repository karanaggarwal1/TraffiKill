package com.example.karan.traffikill.Services;

import com.example.karan.traffikill.models.NearbyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface NearbyPlacesClient {
    @GET("json?&key=AIzaSyCcWcxyuUpiemoDQzHGzJx-yd5jW0Pwt14&sensor=true")
    Call<NearbyPlaces> getNearbyPlaces(@Query("location") String latLng,
                                       @Query("type") String type,
                                       @Query("radius") int radius
    );
}
