package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 22-07-2017.
 */

class DataItemsDaily {

    @SerializedName("summary")
    @Expose
    public String summary;

    @SerializedName("precipIntensity")
    @Expose
    public String precipIntensity;

    @SerializedName("precipIntensityMax")
    @Expose
    public String precipIntensityMax;

    @SerializedName("precipIntensityMaxTime")
    @Expose
    public String precipIntensityMaxTime;

    @SerializedName("precipProbability")
    @Expose
    public String precipProbability;

    @SerializedName("humidity")
    @Expose
    public String humidity;

    public String getSummary() {
        return summary;
    }

    public String getPrecipIntensity() {
        return precipIntensity;
    }

    public String getPrecipIntensityMax() {
        return precipIntensityMax;
    }

    public String getPrecipIntensityMaxTime() {
        return precipIntensityMaxTime;
    }

    public String getPrecipProbability() {
        return precipProbability;
    }

    public String getHumidity() {
        return humidity;
    }
}
