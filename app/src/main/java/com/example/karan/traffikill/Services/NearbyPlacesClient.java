package com.example.karan.traffikill.Services;

import com.example.karan.traffikill.models.NearbyPlaces;
import com.example.karan.traffikill.models.Photos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Karan on 24-07-2017.
 */

public interface NearbyPlacesClient {
    @GET("json?&key=AIzaSyCcWcxyuUpiemoDQzHGzJx-yd5jW0Pwt14&rankby=distance")
    Call<NearbyPlaces> getNearbyPlaces(@Query("location") String latLng,
                                       @Query("type") String type
    );

    @GET("photo?maxwidth=400&key=AIzaSyCcWcxyuUpiemoDQzHGzJx-yd5jW0Pwt14")
    Call<Photos> getPhotos(@Query("photoreference") String photoReference);

}
