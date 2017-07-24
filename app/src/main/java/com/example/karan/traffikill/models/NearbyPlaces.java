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

        public class GeometryData {
            @SerializedName("location")
            @Expose
            LocationData locationData;

            public LocationData getLocationData() {
                return locationData;
            }

            public class LocationData {
                @SerializedName("lat")
                @Expose
                double latitude;

                @SerializedName("lng")
                @Expose
                double longitude;

                public double getLatitude() {
                    return latitude;
                }

                public double getLongitude() {
                    return longitude;
                }
            }
        }
    }


}
