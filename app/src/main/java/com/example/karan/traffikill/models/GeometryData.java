package com.example.karan.traffikill.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Karan on 25-07-2017.
 */

public class GeometryData implements Parcelable {
    @SerializedName("location")
    @Expose
    LocationData location;

    protected GeometryData(Parcel in) {
        location = in.readParcelable(LocationData.class.getClassLoader());
    }

    public static final Creator<GeometryData> CREATOR = new Creator<GeometryData>() {
        @Override
        public GeometryData createFromParcel(Parcel in) {
            return new GeometryData(in);
        }

        @Override
        public GeometryData[] newArray(int size) {
            return new GeometryData[size];
        }
    };

    public LocationData getLocationData() {
        return location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
    }
}
