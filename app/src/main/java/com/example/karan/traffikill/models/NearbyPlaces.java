package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NearbyPlaces {
    @SerializedName("results")
    @Expose
    private ArrayList<ResultData> results;

    public ArrayList<ResultData> getResults() {
        return results;
    }

}
