package com.example.karan.traffikill.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Karan on 24-07-2017.
 */

public class NearbyPlacesAPI {
    public NearbyPlacesClient getNearbyPlaces() {
        String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(NearbyPlacesClient.class);
    }
    public NearbyPlacesClient getPhotos(){
        String BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(NearbyPlacesClient.class);
    }
}
