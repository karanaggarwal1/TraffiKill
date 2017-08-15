package com.example.karan.traffikill.Services;

import com.example.karan.traffikill.models.WeatherInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherClient {
    @GET("{latitude},{longitude}/?units=si&exclude=minutely,flags")
    Call<WeatherInfo> getWeatherInfo(@Path("latitude") double Latitude, @Path("longitude") double Longitude);

    @GET("{latitude},{longitude}/?units=si&exclude=minutely,flags")
    Call<WeatherInfo> getWeatherInfo(@Path("latitude") double Latitude,
                                     @Path("longitude") double Longitude,
                                     @Query("extend") String hourly);
}
