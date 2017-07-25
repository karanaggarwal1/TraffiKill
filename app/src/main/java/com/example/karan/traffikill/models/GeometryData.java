package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Karan on 25-07-2017.
 */

public class GeometryData {
    @SerializedName("location")
    @Expose
    LocationData locationData;

    public LocationData getLocationData() {
        return locationData;
    }

}
