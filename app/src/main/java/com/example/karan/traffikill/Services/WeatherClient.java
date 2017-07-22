package com.example.karan.traffikill.Services;

import com.example.karan.traffikill.models.WeatherInfo;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Karan on 22-07-2017.
 */

public interface WeatherClient {
    @GET("{latitude},{longitude}/?units=si&exclude=minutely,flags")
    Call<WeatherInfo> getWeatherInfo();
}
