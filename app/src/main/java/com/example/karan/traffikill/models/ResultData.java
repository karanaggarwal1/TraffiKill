package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Karan on 25-07-2017.
 */

public class ResultData {

    @SerializedName("geometry")
    @Expose
    GeometryData geometry;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("rating")
    @Expose
    float rating;

    @SerializedName("vicinty")
    @Expose
    String vicinity;

    @SerializedName("photos")
    @Expose
    ArrayList<PhotoDetails> photos;

    public ArrayList<PhotoDetails> getPhotosData() {
        return photos;
    }

    public GeometryData getGeometry() {
        return geometry;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public String getVicinity() {
        return vicinity;
    }

}
