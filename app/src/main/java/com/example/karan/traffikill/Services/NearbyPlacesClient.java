package com.example.karan.traffikill.Services;

import com.example.karan.traffikill.models.NearbyPlaces;
import com.example.karan.traffikill.models.Photos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Karan on 24-07-2017.
 */

public interface NearbyPlacesClient {
    @GET("location={latitude},{longitude}&key=AIzaSyCcWcxyuUpiemoDQzHGzJx-yd5jW0Pwt14&radius={radius}&type={type}")
    Call<NearbyPlaces> getNearbyPlaces(@Path("latitude") double Latitude,
                                       @Path("longitude") double Longitude,
                                       @Path("radius") int radius,
                                       @Path("type") String type
    );

    @GET("maxwidth=400&photoreference={photoReference}&key=AIzaSyCcWcxyuUpiemoDQzHGzJx-yd5jW0Pwt14")
    Call<Photos> getPhotos(@Path("photoReference") String photoReference);
}
