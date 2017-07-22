package com.example.karan.traffikill.models;

import java.util.ArrayList;

/**
 * Created by Karan on 22-07-2017.
 */

public class WeatherInfo {
    ArrayList<CurrentData> currently;
    ArrayList<KeyListHourly> hourly;
    ArrayList<KeyListDaily> daily;

    public ArrayList<CurrentData> getCurrently() {
        return currently;
    }

    public ArrayList<KeyListHourly> getHourly() {
        return hourly;
    }

    public ArrayList<KeyListDaily> getDaily() {
        return daily;
    }
}
