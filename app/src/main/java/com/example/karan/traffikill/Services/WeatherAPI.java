package com.example.karan.traffikill.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherAPI{
        public WeatherClient getWeatherClient() {
            String BASE_URL = "https://api.darksky.net/forecast/cf8cf64a68e531a4894c9d60b70da868/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(WeatherClient.class);
        }
}
