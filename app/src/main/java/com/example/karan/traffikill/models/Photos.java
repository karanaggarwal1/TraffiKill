package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Karan on 24-07-2017.
 */

public class Photos {
    @SerializedName("results")
    @Expose
    ArrayList<PhotosData> results;

    public ArrayList<PhotosData> getResults() {
        return results;
    }

    public class PhotosData {
        @SerializedName("photos")
        @Expose
        ArrayList<PhotoDetails> photosData;

        private class PhotoDetails {
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
    }
}
