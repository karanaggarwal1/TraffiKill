package com.example.karan.traffikill.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Karan on 29-07-2017.
 */

public class FirebaseDataAPI {
    String BASE_URL = "https://crafty-router-159106.firebaseio.com/";

    public FirebaseDataClient getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(FirebaseDataClient.class);
    }
}
