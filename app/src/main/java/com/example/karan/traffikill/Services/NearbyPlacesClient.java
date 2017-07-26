package com.example.karan.traffikill.Services;

import com.example.karan.traffikill.models.NearbyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Karan on 24-07-2017.
 */

public interface NearbyPlacesClient {
    @GET("json?&key=AIzaSyCcWcxyuUpiemoDQzHGzJx-yd5jW0Pwt14&rankby=distance&sensor=true")
    Call<NearbyPlaces> getNearbyPlaces(@Query("location") String latLng,
                                       @Query("type") String type
    );



}
