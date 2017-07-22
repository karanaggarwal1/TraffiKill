package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 22-07-2017.
 */

public class DataItemsHourly {

    @SerializedName("time")
    @Expose
    public String time;

    @SerializedName("precipIntensity")
    @Expose
    public String precipIntensity;

    @SerializedName("precipProbability")
    @Expose
    public String precipProbability;

    @SerializedName("summary")
    @Expose
    public String summary;

    @SerializedName("humidity")
    @Expose
    public String humidity;

    public String getTime() {
        return time;
    }

    public String getPrecipIntensity() {
        return precipIntensity;
    }

    public String getPrecipProbability() {
        return precipProbability;
    }

    public String getSummary() {
        return summary;
    }

    public String getHumidity() {
        return humidity;
    }
}
