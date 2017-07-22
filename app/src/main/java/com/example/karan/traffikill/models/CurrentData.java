package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 22-07-2017.
 */

public class CurrentData {
    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("summary")
    @Expose
    private String summary;

    @SerializedName("precipIntensity")
    @Expose
    private String precipIntensity;

    @SerializedName("precipProbability")
    @Expose
    private String precipProbability;

    @SerializedName("humidity")
    @Expose
    private String humidity;

    @SerializedName("dewPoint")
    @Expose
    private String dewPoint;

}
