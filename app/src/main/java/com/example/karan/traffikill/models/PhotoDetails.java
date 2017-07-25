package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 25-07-2017.
 */

public class PhotoDetails {
    @SerializedName("photo_reference")
    @Expose
    String photoReference;

    @SerializedName("height")
    @Expose
    int height;

    @SerializedName("width")
    @Expose
    int width;

    public String getPhotoReference() {
        return photoReference;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
