package com.example.karan.traffikill.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationData implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LocationData createFromParcel(Parcel in) {
            return new LocationData(in);
        }

        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;

    private LocationData(Parcel in) {

        this.lat = in.readDouble();
        this.lng = in.readDouble();
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
    }
}
