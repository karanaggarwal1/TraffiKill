package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 25-07-2017.
 */

public class LocationData {
    @SerializedName("lat")
    @Expose
    double lat;

    @SerializedName("lng")
    @Expose
    double lng;

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }
}
