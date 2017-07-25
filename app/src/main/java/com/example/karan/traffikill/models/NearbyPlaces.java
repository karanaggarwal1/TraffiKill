package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Karan on 24-07-2017.
 */

public class NearbyPlaces {
    @SerializedName("results")
    @Expose
    ArrayList<ResultData> results;

    public ArrayList<ResultData> getResults() {
        return results;
    }

}
