package com.example.karan.traffikill.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherAPI{
        public WeatherClient getWeatherClient() {
            String BASE_URL = "https://api.darksky.net/forecast/38d2752b399dfb6dfa27f764ec33ed24/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(WeatherClient.class);
        }
}
