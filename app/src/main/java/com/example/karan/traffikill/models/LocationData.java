package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 25-07-2017.
 */

public class LocationData {
    @SerializedName("lat")
    @Expose
    double latitude;

    @SerializedName("lng")
    @Expose
    double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
